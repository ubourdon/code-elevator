import actor.PlayersActor
import akka.actor.Props
import play.api._
import play.api.libs.concurrent._
import play.api.Play.current

object Global extends GlobalSettings {

    override def onStart(app: Application) {
        Logger.info("start")
        Akka.system.actorOf(Props[PlayersActor], name="players")
    }
}