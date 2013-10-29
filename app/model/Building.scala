package model

import scalaz._
import Scalaz._
import akka.actor.ActorRef
import actor.{ResetCause, Reset, SendEventToPlayer}
import scala.annotation.tailrec

case class Building(score: Int = 0,
                    peopleWaitingTheElevator: Vector[Int] = Vector(0, 0, 0, 0, 0, 0),
                    peopleInTheElevator: Int = 0,
                    doorIsOpen: Boolean = false,
                    maxFloor: Int = 5,
                    floor: Int = 0,
                    maxUser: Int = 3,
                    users: List[BuildingUser] = Nil) extends BuildingUserRandomCreator {

    def addBuildingUser(parentActor: ActorRef): Building = {
        if(maxUserIsReached) this
        else {
            val new_user = createUser(building = this, parentActor = parentActor)

            this.copy(
                users = new_user :: this.users,
                peopleWaitingTheElevator = addPeopleWaitingInTheElevator(new_user, this.peopleWaitingTheElevator)
            )
        }
    }

    def tick(): Building = notifyTickToUsers(this)

    def reset(parentActor: ActorRef, resetCause: ResetCause): Building = {
        parentActor ! SendEventToPlayer(Reset(resetCause))
        Building(score = score - 10)
    }

    def up(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor >= maxFloor) IncoherentInstructionForStateBuilding("the floor is reached maximum").fail
        else notifyTickToUsers(this).copy(floor = this.floor + 1).success

    def down(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor == 0) IncoherentInstructionForStateBuilding("the floor is reached 0").fail
        else notifyTickToUsers(this).copy(floor = this.floor - 1).success

    def open(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("doors are already opened").fail
        else notifyTickToUsers(this).copy(doorIsOpen = true).success

    def close(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(!doorIsOpen) IncoherentInstructionForStateBuilding("doors are already closed").fail
        else notifyTickToUsers(this).copy(doorIsOpen = false).success

    private def maxUserIsReached: Boolean = this.users.size >= maxUser

    private def notifyTickToUsers(building: Building): Building = {
        val new_building = building.copy(users = building.users.map( _.tick(this) ))

        new_building.copy(
            score = new_building.score +
                new_building.users
                    .filter(_.status == DONE)
                    .map(score(_))
                    .reduceOption(_ + _)
                    .getOrElse(0),
            users = new_building.users.filterNot(_.status == DONE),
            peopleInTheElevator = new_building.users.filter(_.status == TRAVELLING).size,
            peopleWaitingTheElevator = buildPeopleWaitingTheElevator(Vector(0,0,0,0,0,0), new_building.users.filter( _.status == WAITING ))
        )
    }

    private def addPeopleWaitingInTheElevator(new_user: BuildingUser, peopleWaiting: Vector[Int]): Vector[Int] = {
        val peopleNumberWaitingInFloor = peopleWaiting.apply(new_user.from) + 1
        peopleWaiting.updated(new_user.from, peopleNumberWaitingInFloor)
    }

    @tailrec
    private def buildPeopleWaitingTheElevator(peopleWaitingTheElevator: Vector[Int], users: List[BuildingUser]): Vector[Int] = {
        users match {
            case user :: tail => buildPeopleWaitingTheElevator(addPeopleWaitingInTheElevator(user, peopleWaitingTheElevator), tail)
            case Nil => peopleWaitingTheElevator
        }
    }

    private def score(user: BuildingUser): Int = {
        val score = 20 - user.tickToWait - user.tickToGo + bestTickToGo(user.from, user.target)

        if(score > 20) 20
        else if(score < 0) 0
        else score
    }

    private def bestTickToGo(from: Int, target: Int): Int = Math.abs(target - from) + 2
}

case class IncoherentInstructionForStateBuilding(message: String)