package actor

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import akka.actor.ActorSystem
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.{TestProbe, ImplicitSender, TestActorRef, TestKit}
import akka.pattern.ask
import model.{Player, PlayerInfo}
import scala.concurrent.{Await, Future}
import concurrent.duration._
import akka.util.Timeout
import org.scalatest.mock.MockitoSugar

class PlayersActorTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers
                       with BeforeAndAfterAll with BeforeAndAfter with MockitoSugar with ImplicitSender {

    override def afterAll() { system.shutdown() }

    test("playersActor ! RetrievePlayerInfo should return expected PlayerInfo") {
        val player = Player("email", "pseudo", "password")
        val playerActor = TestActorRef(new PlayersActor(Set(new PlayerInfo(player))))

        implicit val timeout = Timeout(300)
        val result = (playerActor ? RetrievePlayerInfo(player.email)).asInstanceOf[Future[Option[PlayerInfo]]]

        Await.result(result, 1 second) should be (Option(new PlayerInfo(player)))
    }

    test("playersActor ! RetrievePlayersInfo should return expected Set[PlayerInfo]") {
        val player = Player("email", "pseudo", "password")
        val playerActor = TestActorRef(new PlayersActor(Set(new PlayerInfo(player))))

        implicit val timeout = Timeout(300)
        val result = (playerActor ? RetrievePlayersInfo).asInstanceOf[Future[Set[PlayerInfo]]]

        Await.result(result, 1 second) should be (Set(new PlayerInfo(player)))
    }

    test("playersActor ! Register should add one player") {
        val player = Player("email", "pseudo", "password")

        val playerActor = TestActorRef(new PlayersActor())
        playerActor ! Register(player, "")

        implicit val timeout = Timeout(300)
        val result = (playerActor ? RetrievePlayerInfo(player.email)).asInstanceOf[Future[Option[PlayerInfo]]]

        Await.result(result, 1 second) should be (Option(new PlayerInfo(player)))
    }

    test("playersActor ! Register should create EngineActor") {
        val player = Player("email1", "pseudo", "password")

        val playerActor = TestActorRef(new PlayersActor())
        playerActor ! Register(player, "")

        val actor = system.actorSelection(system / "engine-email1")
        actor.toString() should include ("engine-email1")
    }

    test("when playersActor ! Tick should send Tick message to engineActor(s) registered in playerActor") {
        val engineStub1 = TestProbe()
        val engineStub2 = TestProbe()

        val playerActor = TestActorRef(new PlayersActor(playerEngines = Set(engineStub1.ref, engineStub2.ref)))

        playerActor ! Tick

        engineStub1.expectMsg(1 second, Tick)
        engineStub2.expectMsg(1 second, Tick)
    }

    test("when playersActor ! UpdatePlayerInfo should update PlayerInfo") {
        val player1 = Player("email", "pseudo1", "password1")
        val player2 = Player("email", "pseudo2", "password2")

        val playerActor = TestActorRef(new PlayersActor(players = Set(new PlayerInfo(player1))))

        playerActor ! UpdatePlayerInfo(new PlayerInfo(player2))

        implicit val timeout = Timeout(300)
        val result = (playerActor ? RetrievePlayersInfo).asInstanceOf[Future[Set[PlayerInfo]]]

        Await.result(result, 1 second) should be (Set(new PlayerInfo(player2)))
    }
}