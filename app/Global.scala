import actor.{Tick, PlayersActor}
import akka.actor.{ActorRef, Props}
import play.api._
import play.api.libs.concurrent._
import play.api.Play.current
import concurrent.duration._
import concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {

    override def onStart(app: Application) {
        Logger.info("start")
        val playersActor: ActorRef = Akka.system.actorOf(Props[PlayersActor], name="players")

        Akka.system.scheduler.schedule(initialDelay = 2 second, interval = 1 second) { playersActor ! Tick }
    }
}