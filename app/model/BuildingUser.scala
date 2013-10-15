package model

import scala.util.Random
import akka.actor.ActorRef
import actor.CallPlayer

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

        val user = BuildingUser(parentActor, from = random_from, target = random_to)

        parentActor ! CallPlayer(user)

        user
    }
}

/*, currentBuildingFloor: Int*//*, currentBuildingDoorsStatus: Boolean, tickToGo: Int = 0, status: BuildingUserStatus = WAITING*/
case class BuildingUser(private val parentActor: ActorRef, from: Int, target: Int, tickToWait: Int = 0) {

    def tick(): BuildingUser = null   // TODO implement method
}

sealed trait BuildingUserStatus
case object WAITING extends BuildingUserStatus
case object TRAVELLING extends BuildingUserStatus
case object DONE extends BuildingUserStatus

class PlayerServerConnectError(private val message: String) extends RuntimeException {
    override def getMessage: String = message
}