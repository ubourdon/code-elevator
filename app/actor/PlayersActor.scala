package actor

import akka.actor.{ActorLogging, Actor}
import model.Player

class PlayersActor extends Actor with ActorLogging {
    private var players: Set[Player] = Set()

    def receive = {
        case Register(player) => players = players + player; log.info(s"user register : ${players.size}")
        case _ => log.warning("unknow message send !")
    }
}

case class Register(player: Player)