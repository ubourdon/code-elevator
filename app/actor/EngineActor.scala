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
    private var floor: Int = 0
    private var doorIsOpened = false

    def receive = {
        case Tick =>
            val playerResponse = WS.url(s"$serverUrl/nextCommand").get()
            playerResponse onSuccess { case response =>
                val nextCommand = response.body

                // TODO implémenter toutes les commandes = changer l'état de l'engine
                // TODO renseigner PlayerInfo pour que l'état bouge sur le board player
                nextCommand match {
                    case "NOTHING" => // on fait rien
                    case unknownCommand => log.error(s"don't know this command : $unknownCommand")
                }
            }

        case _ => log.warning("unknow message send !")
    }
}

case class LaunchGame(player: Player)