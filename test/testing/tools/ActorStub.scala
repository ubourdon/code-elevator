package testing.tools

import akka.actor.{ActorRef, Actor}

class ActorStub(sender: ActorRef) extends Actor {
    def receive = { case message => sender ! message }
}
