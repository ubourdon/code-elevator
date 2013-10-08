package actor

import akka.actor.{Actor, ActorLogging}
import model.{PlayerInfo, Building, Player}
import play.api.libs.ws.WS
import concurrent.ExecutionContext.Implicits.global

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
        case Tick =>
            building.addUser()
            val playerResponse = WS.url(s"$serverUrl/nextCommand").get()

            playerResponse onSuccess { case response =>
                NextCommand(response.body) match {
                    case UP => building = building.up()
                    case DOWN => building = building.down()
                    case OPEN => building = building.open()
                    case CLOSE => building = building.close()
                    case NOTHING => log.info("nothing")
                    case UNKNNOW_COMMAND => log.error("unknown command !")
                }

                playersActor ! UpdatePlayerInfo(new PlayerInfo(player, building))
            }

        case _ => log.warning("unknow message send !")
    }
}

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