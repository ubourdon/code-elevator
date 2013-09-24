package controllers

import play.api.mvc._
import play.api.libs.concurrent._
import actor.Register
import model.Player

import play.api.Play.current

object Players extends Controller {
    private val akkaSystem = Akka.system
    // TODO use actorSelection instead of actorFor
    private lazy val playersActor = akkaSystem.actorFor(akkaSystem / "players")

    def register(email: String, pseudo: String, serverUrl: String) = Action {
        val password = generatePassword
        playersActor ! Register(Player(email, pseudo, generatePassword))

        Ok(password)
    }

    private def generatePassword: String = "toto"
}