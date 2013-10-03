package actor

import akka.actor.{Actor, ActorLogging}
import model.{Building, Player}
import play.api.libs.ws.WS
import concurrent.ExecutionContext.Implicits.global

/**
 *    Toutes les secondes essayer de rajouter un utilisateur d'ascenseur dans l'immeuble - limite max
 *    l'utilisateur à un étage d'origine et une destination
 *    dès qu'il entre dans l'immeuble il demande UP | DOWN
 */
class EngineActor(private val player: Player,
                  private val serverUrl: String,
                  private val building: Building = Building()) extends Actor with ActorLogging {

    def receive = {
        case Tick =>
            building.addUser()
            // try to add user in building
                // add into Building
                // /call
            // update engine state => /nextCommand
            // validate nextCommand
            // ???
            val playerResponse = WS.url(s"$serverUrl/nextCommand").get()
            playerResponse onSuccess { case response =>
                // TODO implémenter toutes les commandes = changer l'état de l'engine
                // TODO renseigner PlayerInfo pour que l'état bouge sur le board player
                NextCommand(response.body) match {
                    case UP => building.up()
                    case DOWN => building.down()
                    case OPEN => building.open()
                    case CLOSE => building.close()
                    case NOTHING => log.info("nothing")
                    case UNKNNOW_COMMAND => log.error("unknown command !")
                }
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

// ???
case class LaunchGame(player: Player)