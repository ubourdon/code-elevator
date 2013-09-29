package testing.tools

import akka.actor.{ActorLogging, Actor}

class ActorStub[S](spy: Spy[S]) extends Actor with ActorLogging {
    override def receive = {
        case message => /*spy.store(message);*/ sender ! message
        case _ => log.error("Wrong Type")
    }

    override def preStart() = log.info("actor stub about to start")
}

trait Spy[T] {
    def store(saved: T)
}

class ActorSpy[T] extends Spy[T] {
    private var saved: T = _

    def store(saved: T) = this.saved = saved

    def get: T = this.saved
}

class ActorSpies extends Spy[AnyRef] {
    private var saved: List[AnyRef] = Nil

    def store(saved: AnyRef) = this.saved = this.saved :+ saved

    def get: List[AnyRef] = this.saved
}