package model

import scala.util.{Try, Random}
import play.api.libs.ws.WS
import scala.concurrent.Await
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

    def tick(): BuildingUser = null

    private def callPlayer() {                      // TODO transférer ca dans engineActor - il s'occupe de tous les appels http
        import concurrent.duration._

        // call?atFloor=[0-5]&to=[UP|DOWN]
        val url = s"http://localhost:8080/call?atFloor=0&to=UP"

        val tryResponse = Try(Await.result(WS.url(url).get(), 1 second))
        tryResponse.map { response =>
            if(response.status != 200) throw new PlayerServerConnectError(s"remote server respond ${response.status} from [GET] $url")
        }.get
    }
}

sealed trait BuildingUserStatus
case object WAITING extends BuildingUserStatus
case object TRAVELLING extends BuildingUserStatus
case object DONE extends BuildingUserStatus

class PlayerServerConnectError(private val message: String) extends RuntimeException {
    override def getMessage: String = message
}