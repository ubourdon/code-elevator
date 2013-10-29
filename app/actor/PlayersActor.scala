package actor

import akka.actor._
import model.Player
import model.PlayerInfo

class PlayersActor(private var players: Set[PlayerInfo] = Set(), private var playerEngines: Set[ActorRef] = Set()) extends Actor with ActorLogging {

    def receive = {
        case Register(player, serverUrl) => {
            players = players + new PlayerInfo(player)
            log.info(s"user register : ${players.size}")
            // TODO refuser un nouveau joueur si email déjà pris
            // TODO clean url rentré par le participant
                // pas de http en doublon, pas de slash à la fin
            playerEngines = playerEngines + createEngine(player, serverUrl)
        }

        case RetrievePlayerInfo(email) => sender ! players.find(p => p.email == email)

        case RetrievePlayersInfo => sender ! players

        case Tick => playerEngines.foreach { playerEngine => playerEngine ! Tick }

        case UpdatePlayerInfo(playerInfo) => players = players.filter( p => p.email != playerInfo.email ) + playerInfo

        case _ => log.warning("unknow message send !")
    }

    private def createEngine(player: Player, serverUrl: String): ActorRef =
        context.actorOf(Props(new EngineActor(player, serverUrl)), name=s"engine-${player.email}")
}

case class Register(player: Player, serverUrl: String)
case class RetrievePlayerInfo(email: String)
case object RetrievePlayersInfo
case object Tick
case class UpdatePlayerInfo(playerInfo: PlayerInfo)