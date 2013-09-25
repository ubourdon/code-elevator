package actor

import akka.actor.{ActorLogging, Actor}
import model.{PlayerInfo, Player}

class PlayersActor extends Actor with ActorLogging {
    private var players: Set[Player] = Set()

    def receive = {
        case Register(player) => players = players + player; log.info(s"user register : ${players.size}")

        case RetrievePlayer(email) => sender ! players.find(p => p.email == email)

        case RetrievePlayersInfo => sender ! players.map { player => PlayerInfo(player.pseudo, player.email, 0L, Array(0, 0, 3, 5, 2, 0), 2, 1, false, "last error message", "RESUME") } // TODO WTF

        case _ => log.warning("unknow message send !")
    }
}

case class Register(player: Player)
case class RetrievePlayer(email: String)
case object RetrievePlayersInfo