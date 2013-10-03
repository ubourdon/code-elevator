package actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import model.{Building, Player}
import org.mockito.Mockito
import fr.simply.{StubServer, StaticServerResponse, GET}
import fr.simply.util.Text_Plain
import fr.simply.fixture.StubServerFixture

class EngineActorTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers
                      with BeforeAndAfterAll with BeforeAndAfter with MockitoSugar
                      with ImplicitSender with StubServerFixture {
    test("when engineActor receive Tick message, try to add user in buidling") {
        val player = Player("toto", "titi", "tata")
        val building = mock[Building]

        val engineActor = TestActorRef(new EngineActor(player, "http://localhost:8080", building))

        engineActor ! Tick
        Mockito.verify(building).addUser
    }

    test("when engineActor receive Tick message, update building state with /nextCommand returning UP") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "UP", 200)
        )


        withStubServerFixture(8080, route) { server =>
            val player = Player("toto", "titi", "tata")
            val building = mock[Building]

            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            // TODO use awaitint event based blocking sleep
            Thread.sleep(100)
            Mockito.verify(building).up()
        }
    }

    test("when engineActor receive Tick message, update building state with /nextCommand  returning DOWN") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "DOWN", 200)
        )


        withStubServerFixture(8080, route) { server =>
            val player = Player("toto", "titi", "tata")
            val building = mock[Building]

            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            // TODO use awaiting event based blocking sleep
            Thread.sleep(100)
            Mockito.verify(building).down()
        }
    }

    test("when engineActor receive Tick message, update building state with /nextCommand  returning OPEN") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "OPEN", 200)
        )


        withStubServerFixture(8080, route) { server =>
            val player = Player("toto", "titi", "tata")
            val building = mock[Building]

            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            // TODO use awaiting event based blocking sleep
            Thread.sleep(100)
            Mockito.verify(building).open()
        }
    }

    test("when engineActor receive Tick message, update building state with /nextCommand  returning CLOSE") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "CLOSE", 200)
        )


        withStubServerFixture(8080, route) { server =>
            val player = Player("toto", "titi", "tata")
            val building = mock[Building]

            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            // TODO use awaiting event based blocking sleep
            Thread.sleep(100)
            Mockito.verify(building).close()
        }
    }

}