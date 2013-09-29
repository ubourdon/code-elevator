package actor

import akka.actor.{Actor, Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.MustMatchers

class MySpecTest(_system: ActorSystem) extends TestKit(_system) with FunSuite with ImplicitSender
with MustMatchers with BeforeAndAfterAll {

    def this() = this(ActorSystem("MySpec"))

    import MySpec._

    override def afterAll { TestKit.shutdownActorSystem(system) }

    test("spike") {
        val echo = system.actorOf(Props[EchoActor])
        echo ! "hello world"
        expectMsg("hello world")
    }
}

object MySpec {
    class EchoActor extends Actor {
        def receive = {
            case x ⇒ sender ! x
        }
    }
}