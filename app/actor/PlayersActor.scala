package actor

import akka.actor._
import model.{PlayerInfo, Player}
import play.api.libs.concurrent.Akka
import model.Player
import model.PlayerInfo
import actor.LaunchGame
import actor.Register
import actor.RetrievePlayerInfo

class PlayersActor extends Actor with ActorLogging {

    private val system = context.system

    private var players: Set[Player] = Set()
    private var playerEngines: Set[ActorRef] = Set()

    def receive = {
        case Register(player, serverUrl) => {
            players = players + player
            log.info(s"user register : ${players.size}")
            // TODO refuser un nouveau jouer si email déjà pris

            playerEngines = playerEngines + createEngine(player, serverUrl)
        }

        case RetrievePlayerInfo(email) => sender ! players.find(p => p.email == email).map( new PlayerInfo(_) )

        case RetrievePlayersInfo => sender ! players.map { player => new PlayerInfo(player) }

        case Tick => playerEngines.foreach { playerEngine => playerEngine ! Tick }

        case _ => log.warning("unknow message send !")
    }

    private def createEngine(player: Player, serverUrl: String): ActorRef = system.actorOf(Props(new EngineActor(player, serverUrl)), name=s"engine-${player.email}")
}

case class Register(player: Player, serverUrl: String)
case class RetrievePlayerInfo(email: String)
case object RetrievePlayersInfo
case object Tick