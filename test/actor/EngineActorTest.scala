package actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import model.{PlayerInfo, Building, Player}
import org.mockito.Mockito
import fr.simply.{StaticServerResponse, GET}
import fr.simply.util.Text_Plain
import fr.simply.fixture.StubServerFixture
import testing.tools.{ActorTestingTools, ActorStub}
import concurrent.duration._
import scalaz.Scalaz._

class EngineActorTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers
                      with BeforeAndAfterAll with BeforeAndAfter with MockitoSugar
                      with ImplicitSender with StubServerFixture with ActorTestingTools {

    after { closeDummyActors("players") }

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
            Mockito.when(building.up()).thenReturn(Building(floor = 1).success)

            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(floor = 1))))
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
            Mockito.when(building.down()).thenReturn(Building(floor = -1).success)

            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(floor = -1))))
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
            Mockito.when(building.open()).thenReturn(Building(doorIsOpen = true).success)

            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(doorIsOpen = true))))
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
            Mockito.when(building.close()).thenReturn(Building(doorIsOpen = true).success)

            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(doorIsOpen = true))))
        }
    }

    test("when engineActor receive Tick message should notify PlayersActor with new building state for this player") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "NOTHING", 200)
        )


        withStubServerFixture(8080, route) { server =>
            val player = Player("toto", "titi", "tata")
            val building = Building()

            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, building)))
        }
    }
}