package actor

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import model.{BuildingUser, PlayerInfo, Building, Player}
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
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "NOTHING", 200)
        )

        val player = Player("toto", "titi", "tata")
        val building = mock[Building]
        Mockito.when(building.addBuildingUser()).thenReturn(Building(users = List(BuildingUser())))
        Mockito.when(building.up()).thenReturn(Building(floor = 1).success)

        withStubServerFixture(8080, route) { server =>
            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo( new PlayerInfo(player, Building(users = List(BuildingUser()))) ))
        }
    }

    test("when engineActor receive Tick message & /nextCommand returning UP, should notify PlayersActor with new building state") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "UP", 200)
        )

        val player = Player("toto", "titi", "tata")
        val building = mock[Building]
        Mockito.when(building.addBuildingUser()).thenReturn(building)
        Mockito.when(building.up()).thenReturn(Building(floor = 1).success)

        withStubServerFixture(8080, route) { server =>
            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(floor = 1))))
        }
    }

    test("when engineActor receive Tick message & /nextCommand returning DOWN, should notify PlayersActor with new building state") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "DOWN", 200)
        )

        val player = Player("toto", "titi", "tata")
        val building = mock[Building]
        Mockito.when(building.addBuildingUser()).thenReturn(building)
        Mockito.when(building.down()).thenReturn(Building(floor = -1).success)

        withStubServerFixture(8080, route) { server =>
            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(floor = -1))))
        }
    }

    test("when engineActor receive Tick message & /nextCommand returning OPEN, should notify PlayersActor with new building state") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "OPEN", 200)
        )

        val player = Player("toto", "titi", "tata")
        val building = mock[Building]
        Mockito.when(building.addBuildingUser()).thenReturn(building)
        Mockito.when(building.open()).thenReturn(Building(doorIsOpen = true).success)

        withStubServerFixture(8080, route) { server =>
            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(doorIsOpen = true))))
        }
    }

    test("when engineActor receive Tick message & /nextCommand returning CLOSE, should notify PlayersActor with new building state") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "CLOSE", 200)
        )

        val player = Player("toto", "titi", "tata")
        val building = mock[Building]
        Mockito.when(building.addBuildingUser()).thenReturn(building)
        Mockito.when(building.close()).thenReturn(Building(doorIsOpen = true).success)

        withStubServerFixture(8080, route) { server =>
            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, Building(doorIsOpen = true))))
        }
    }

    test("when engineActor receive Tick message & /nextCommand response is NOTHING, should notify PlayersActor with same building state") {
        val route = GET (
            path = "/nextCommand",
            response = StaticServerResponse(Text_Plain, "NOTHING", 200)
        )

        val player = Player("toto", "titi", "tata")
        val building = mock[Building]
        Mockito.when(building.addBuildingUser()).thenReturn(building)
        Mockito.when(building.tick()).thenReturn(building)

        withStubServerFixture(8080, route) { server =>
            TestActorRef(new ActorStub(testActor), "players")
            val engineActor = TestActorRef(new EngineActor(player, s"http://localhost:${server.portInUse}", building))

            engineActor ! Tick

            expectMsg(1 second, UpdatePlayerInfo(new PlayerInfo(player, building)))
        }
    }
}