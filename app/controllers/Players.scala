package controllers

import play.api.mvc._
import play.api.libs.concurrent._
import actor.{RetrievePlayer, Register}
import model.{PlayerJsonSerializer, Player}
import akka.pattern.ask

import play.api.Play.current
import scala.concurrent.Future
import akka.util.Timeout
import concurrent.duration._
import play.api.libs.json.Json
import concurrent.ExecutionContext.Implicits.global

object Players extends Controller with PlayerJsonSerializer {
    private val akkaSystem = Akka.system
    private lazy val playersActor = akkaSystem.actorSelection(akkaSystem / "players")

    def register(email: String, pseudo: String, serverUrl: String) = Action {
        val password = generatePassword
        playersActor ! Register(Player(email, pseudo, generatePassword))

        Ok(password)
    }

    def info(email: String) = Action.async {
        implicit val timeout = Timeout(3 seconds)
        val future: Future[Option[Player]] = (playersActor ? RetrievePlayer(email)).asInstanceOf[Future[Option[Player]]]
        future.map { maybePlayer =>
            maybePlayer match {
                case Some(player) => Ok(Json.toJson(player))
                case None => NotFound("player not found !")
            }
        }
    }

    private def generatePassword: String = "toto"
}