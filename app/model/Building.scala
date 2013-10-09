package model

import scalaz._
import Scalaz._

case class Building(score: Int = 0,
                    peopleWaitingTheElevator: Vector[Int] = Vector(0, 0, 0, 0, 0, 0),
                    peopleInTheElevator: Int = 0,
                    doorIsOpen: Boolean = false,
                    maxFloor: Int = 5,
                    floor: Int = 0,
                    maxUser: Int = 10,
                    users: List[BuildingUser] = Nil) {

    def addBuildingUser(): Building = {
        if(maxUserIsReached) this
        else this.copy(users = BuildingUser.randomCreate() :: this.users)
    }

    def tick(): Building = this.copy(users = this.users.map( user => user.tick() ))

    // TODO notifie user nouvel etat building
    def up(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor < maxFloor) this.copy(floor = this.floor + 1).success
        else IncoherentInstructionForStateBuilding("the floor is reached maximum").fail

    // TODO notifie user nouvel etat building
    def down(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor > 0) this.copy(floor = this.floor - 1).success
        else IncoherentInstructionForStateBuilding("the floor is reached 0").fail

    // TODO notifie user nouvel etat building
    def open(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("doors are already opened").fail
        else this.copy(doorIsOpen = true).success

    // TODO notifie user nouvel etat building
    def close(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(!doorIsOpen) IncoherentInstructionForStateBuilding("doors are already closed").fail
        else this.copy(doorIsOpen = false).success

    private def maxUserIsReached: Boolean = this.users.size >= maxUser
}

case class IncoherentInstructionForStateBuilding(message: String)