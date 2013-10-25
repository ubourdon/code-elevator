package model

import scala.util.Random
import akka.actor.ActorRef
import actor._
import actor.Go
import actor.CallPlayer
import actor.SendEventToPlayer

trait BuildingUserRandomCreator {
    def createUser(building: Building, parentActor: ActorRef, random: Random = new Random()): BuildingUser =
        BuildingUser.randomCreate(building, parentActor, random)
}

object BuildingUser {
    def randomCreate(building: Building, parentActor: ActorRef, random: Random = new Random()): BuildingUser = {
        val random_from = if(random.nextBoolean()) 0 else random.nextInt(building.maxFloor) + 1
        val random_to = {
            def to_different_of_from(): Int = {
                val to = random.nextInt(building.maxFloor + 1)
                if (to == random_from) to_different_of_from()
                else to
            }
            to_different_of_from()
        }

        val userCanEnterIntoElevator = building.doorIsOpen && building.floor == random_from

        val status = if(userCanEnterIntoElevator) TRAVELLING else WAITING

        val user = BuildingUser(parentActor, from = random_from, target = random_to, status = status)

        sendEventToPlayer(userCanEnterIntoElevator, parentActor, user)

        user
    }

    /** l'ordre des call est important - 1. CallPlayer, 2.SendEventToPlayer(UserHasEntered), 3.SendEventToPlayer(Go(user)) */
    private def sendEventToPlayer(userCanEnterIntoElevator: Boolean, parentActor: ActorRef, user: BuildingUser) {
        parentActor ! CallPlayer(user)

        if(userCanEnterIntoElevator) {
            parentActor ! SendEventToPlayer(Go(user))
            parentActor ! SendEventToPlayer(UserHasEntered)
        }
    }
}

/*, currentBuildingFloor: Int*//*, currentBuildingDoorsStatus: Boolean*/
case class BuildingUser(private val parentActor: ActorRef, from: Int, target: Int, tickToWait: Int = 0, tickToGo: Int = 0, status: BuildingUserStatus = WAITING) {

    def tick(building: Building): BuildingUser = {
        val userCanEnterIntoElevator = building.doorIsOpen && building.floor == from
        val userCanLeaveTheElevator = building.doorIsOpen && building.floor == target

        val new_tickToWait = if(status == WAITING) tickToWait + 1 else tickToWait
        val new_tickToGo = if(status == TRAVELLING) tickToGo + 1 else tickToGo

        val new_status = if(userCanEnterIntoElevator) TRAVELLING
                         else if(userCanLeaveTheElevator) DONE
                         else status

        val user = BuildingUser(parentActor, from = this.from, target = this.target, tickToWait = new_tickToWait, tickToGo = new_tickToGo, status = new_status)

        if(userCanEnterIntoElevator) {
            parentActor ! SendEventToPlayer(Go(user))
            parentActor ! SendEventToPlayer(UserHasEntered)
        }

        if(userCanLeaveTheElevator) parentActor ! SendEventToPlayer(UserHasExited)

        user
    }
}

sealed trait BuildingUserStatus
case object WAITING extends BuildingUserStatus
case object TRAVELLING extends BuildingUserStatus
case object DONE extends BuildingUserStatus

class PlayerServerConnectError(private val message: String) extends RuntimeException {
    override def getMessage: String = message
}