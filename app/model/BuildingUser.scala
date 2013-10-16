package model

import scala.util.Random
import akka.actor.ActorRef
import actor.{UserHasEntered, Go, SendEventToPlayer, CallPlayer}

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

        if(userCanEnterIntoElevator)
            parentActor ! SendEventToPlayer(UserHasEntered)
            parentActor ! SendEventToPlayer(Go(user))
    }

}

/*, currentBuildingFloor: Int*//*, currentBuildingDoorsStatus: Boolean, tickToGo: Int = 0*/
case class BuildingUser(private val parentActor: ActorRef, from: Int, target: Int, tickToWait: Int = 0, status: BuildingUserStatus = WAITING) {

    def tick(): BuildingUser = null   // TODO implement method   besoin de l'état du building ???

    // TODO quand le user est arrivé comment je calcul le score et comment je l'envoi à building ?
}

sealed trait BuildingUserStatus
case object WAITING extends BuildingUserStatus
case object TRAVELLING extends BuildingUserStatus
case object DONE extends BuildingUserStatus

class PlayerServerConnectError(private val message: String) extends RuntimeException {
    override def getMessage: String = message
}