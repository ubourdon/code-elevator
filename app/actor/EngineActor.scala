package actor

import akka.actor.{Actor, ActorLogging}
import model.Player
import play.api.libs.ws.WS
import concurrent.ExecutionContext.Implicits.global

/**
 *    Toutes les secondes essayer de rajouter un utilisateur d'ascenseur dans l'immeuble - limite max
 *    l'utilisateur à un étage d'origine et une destination
 *    dès qu'il entre dans l'immeuble il demande UP | DOWN
 */
class EngineActor(private val player: Player, private val serverUrl: String) extends Actor with ActorLogging {
    private val maxFloor = 5

    private var floor: Int = 0
    private var doorIsOpened = false

    def receive = {
        case Tick =>
            val playerResponse = WS.url(s"$serverUrl/nextCommand").get()
            playerResponse onSuccess { case response =>
                // TODO implémenter toutes les commandes = changer l'état de l'engine
                // TODO renseigner PlayerInfo pour que l'état bouge sur le board player
                NextCommand(response.body) match {
                    case UP => if(floor < maxFloor) floor = floor + 1
                    case DOWN => if(floor > 0) floor = floor - 1
                    case OPEN =>
                    case CLOSE =>
                    case NOTHING => // on fait rien
                    case UNKNNOW_COMMAND => log.error("unknown command !")
                }
            }

        case _ => log.warning("unknow message send !")
    }
}

sealed trait NextCommand
object NextCommand {
    def apply(state: String): NextCommand = {
        if(state.equalsIgnoreCase("NOTHING")) NOTHING
        else if(state.equalsIgnoreCase("UP")) UP
        else if(state.equalsIgnoreCase("DOWN")) DOWN
        else if(state.equalsIgnoreCase("OPEN")) OPEN
        else if(state.equalsIgnoreCase("CLOSE")) CLOSE
        else UNKNNOW_COMMAND
    }
}

case object NOTHING extends NextCommand
case object UP extends NextCommand
case object DOWN extends NextCommand
case object OPEN extends NextCommand
case object CLOSE extends NextCommand
case object UNKNNOW_COMMAND extends NextCommand

case class LaunchGame(player: Player)