package actor

import akka.actor.{ActorLogging, Actor}
import model.{PlayerInfo, Player}

class PlayersActor extends Actor with ActorLogging {
    private var players: Set[Player] = Set()

    def receive = {
        case Register(player) => players = players + player; log.info(s"user register : ${players.size}")

        case RetrievePlayerInfo(email) => sender ! players.find(p => p.email == email).map( new PlayerInfo(_) )

        case RetrievePlayersInfo => sender ! players.map { player => new PlayerInfo(player) }

        case _ => log.warning("unknow message send !")
    }
}

case class Register(player: Player)
case class RetrievePlayerInfo(email: String)
case object RetrievePlayersInfo