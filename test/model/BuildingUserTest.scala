package model

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import testing.tools.ActorTestingTools
import org.mockito.Mockito._
import actor.{UserHasExited, Go, UserHasEntered, SendEventToPlayer}
import concurrent.duration._

class BuildingUserTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers with BeforeAndAfterAll
                                                            with BeforeAndAfter with MockitoSugar
                                                            with ImplicitSender  with ActorTestingTools {

    override def afterAll() { system.shutdown() }

    test("if user is waiting, when BuildingUser.tick() should increment tickToWait") {
        val engine = TestProbe().ref
        BuildingUser(engine, 0, 0, status = WAITING).tick(mock[Building]) should be (BuildingUser(engine, 0, 0, tickToWait = 1, status = WAITING))
    }

    test("if user is travelling, when BuildingUser.tick() should increment tickToGo") {
        val engine = TestProbe().ref
        BuildingUser(engine, 0, 0, status = TRAVELLING).tick(mock[Building]) should be (BuildingUser(engine, 0, 0, tickToGo = 1, status = TRAVELLING))
    }

    test("if user is waiting & user can enter into the elevator, when BuildingUser.tick() status should be TRAVELLING") {
        val engine = TestProbe().ref
        val building = mock[Building]
        when(building.floor).thenReturn(0)
        when(building.doorIsOpen).thenReturn(true)

        BuildingUser(engine, 0, 0).tick(building) should be (BuildingUser(engine, 0, 0, tickToWait = 1, status = TRAVELLING))
    }

    test("if user is waiting & user can enter into the elevator, when BuildingUser.tick() should send SendEventToPlayer x 2 to engineActorParent") {
        val engine = TestProbe()
        val building = mock[Building]
        when(building.floor).thenReturn(0)
        when(building.doorIsOpen).thenReturn(true)

        val expectedUser = BuildingUser(engine.ref, 0, 0).tick(building)

        engine.receiveN(2, 1 second).foreach {
            case SendEventToPlayer(UserHasEntered) =>
            case SendEventToPlayer(Go(user)) => user should be (expectedUser)
            case _ => fail("should receive SendEventToPlayer")
        }
    }

    test("if user is travelling & can leave the elevator, when BuildingUser.tick() status should be DONE") {
        val engine = TestProbe().ref
        val building = mock[Building]
        when(building.floor).thenReturn(1)
        when(building.doorIsOpen).thenReturn(true)

        BuildingUser(engine, 0, 1, status = TRAVELLING).tick(building) should be (BuildingUser(engine, 0, 1, tickToGo = 1, status = DONE))
    }

    test("if user is travelling & user can leave into the elevator, when BuildingUser.tick() should send SendEventToPlayer(UserHasExited) to engineActorParent") {
        val engine = TestProbe()
        val building = mock[Building]
        when(building.floor).thenReturn(1)
        when(building.doorIsOpen).thenReturn(true)

        BuildingUser(engine.ref, 0, 1, status = TRAVELLING).tick(building)

        engine.expectMsg(SendEventToPlayer(UserHasExited))
    }

    // créer User si limite pas dépassée
    // BuildingUser(from, target, tickToWait, tickToGo, currentBuildingFloor, currentBuildingDoorsStatus, status: Waiting/travelling/DONE)

    //dans le user
    //à sa construction
        // User.call vers le player    ?at=from&direction=up/down         // response 200 ok sinon RESET
        // si porte ouverte et ascenseur à son étage d'origine => status = travelling & GET /userHasEntered + GET /go?floorToGo=[0-5]      // response 200 ok sinon RESET

    // quand il est notifié
    // envoi event             // response 200 ok sinon RESET

    // score quand user sors de l'ascenseur envoie son score à building
    // GET /userHasExited                                    // response 200 ok sinon RESET
    //voir github score tests
}