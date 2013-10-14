package actor

import akka.actor.{Actor, ActorLogging}
import model._
import play.api.libs.ws.WS
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Validation
import model.Player
import play.api.libs.ws.Response
import model.PlayerInfo
import model.IncoherentInstructionForStateBuilding
import model.Building

/**
 *    Toutes les secondes essayer de rajouter un utilisateur d'ascenseur dans l'immeuble - limite max
 *    l'utilisateur à un étage d'origine et une destination
 *    dès qu'il entre dans l'immeuble il demande UP | DOWN
 */
class EngineActor(private val player: Player,
                  private val serverUrl: String,
                  private var building: Building = Building()) extends Actor with ActorLogging {

    private val playersActor = context.system.actorSelection(context.system / "players")

    def receive = {
        case Tick => {
            building = building.addBuildingUser(self)

            val playerResponse = WS.url(s"$serverUrl/nextCommand").get()

            successCase(playerResponse)
            
            // TODO Future.failure case ???
        }

        case _ => log.warning("unknow message send !")
    }

    private def successCase(playerResponse: Future[Response]) {
        playerResponse onSuccess { case response =>
            val new_building_valid = buildNewBuildingFromNextCommand(response)

            new_building_valid.map { new_building =>
                building = new_building           // TODO !!! accès concurrent à building ???
                playersActor ! UpdatePlayerInfo(new PlayerInfo(player, building))
            }
        }

        // TODO Validation.Failure case ???
    }

    private def buildNewBuildingFromNextCommand(response: Response): Validation[IncoherentInstructionForStateBuilding, Building] = {
        import scalaz.Scalaz._

        NextCommand(response.body) match {
            case UP => building.up()
            case DOWN => building.down()
            case OPEN => building.open()
            case CLOSE => building.close()
            case NOTHING => log.info("nothing"); building.tick().success
            case UNKNNOW_COMMAND => log.error("unknown command !"); building.tick().success     // TODO unknown command ???  // TODO test
        }
    }
}

case class CallPlayer(user: BuildingUser)

sealed trait NextCommand
object NextCommand {
    def apply(state: String): NextCommand = {
        val trim = state.trim
        if(trim.equalsIgnoreCase("NOTHING")) NOTHING
        else if(trim.equalsIgnoreCase("UP")) UP
        else if(trim.equalsIgnoreCase("DOWN")) DOWN
        else if(trim.equalsIgnoreCase("OPEN")) OPEN
        else if(trim.equalsIgnoreCase("CLOSE")) CLOSE
        else UNKNNOW_COMMAND
    }
}

case object NOTHING extends NextCommand
case object UP extends NextCommand
case object DOWN extends NextCommand
case object OPEN extends NextCommand
case object CLOSE extends NextCommand
case object UNKNNOW_COMMAND extends NextCommand