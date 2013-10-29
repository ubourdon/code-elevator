package model

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import scalaz.{Failure, Success}
import org.scalatest.mock.MockitoSugar
import akka.testkit.{TestProbe, TestActorRef, TestKit}
import actor.{ResetCause, Reset, SendEventToPlayer, EngineActor}
import akka.actor.{ActorRef, ActorSystem}
import org.mockito.Matchers._
import org.mockito.Mockito._
import scalaz.Scalaz._
import scala.util.Random


class BuildingTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers with MockitoSugar with BeforeAndAfterAll {

    override def afterAll() { system.shutdown() }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      *                          Building.up()                         *
      ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    test("building.up should increment floor by 1") {
        Building().up() should be (Success(Building(floor = 1)))
    }

    test("if the floor is reached maximum, building.up return Failure[IncoherentInstructionForStateBuilding]") {
        Building(maxFloor = 0).up() should be(Failure(IncoherentInstructionForStateBuilding("the floor is reached maximum")))
    }

    test("if door is open, building.up return Failure[IncoherentInstructionForStateBuilding]") {
        Building(doorIsOpen = true).up() should be(Failure(IncoherentInstructionForStateBuilding("the door is opened")))
    }

    test("when Building.up should tick all building.users") {
        val expectedBuildingUser = BuildingUser(parentActor = null, tickToWait = 1, from = 0, target = 1)

        val buildingUser = mock[BuildingUser]
        when(buildingUser.tick(any[Building])).thenReturn(expectedBuildingUser)

        Building(users = List(buildingUser)).up() should be (Success(Building(floor = 1, users = List(expectedBuildingUser), peopleWaitingTheElevator = Vector(1,0,0,0,0,0))))
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      *                          Building.down()                         *
      ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    test("building.down should decrement floor by 1") {
        Building(floor = 1).down() should be (Success(Building(floor = 0)))
    }

    test("if the floor is reached 0, building.down return Failure[IncoherentInstructionForStateBuilding]") {
        Building().down() should be(Failure(IncoherentInstructionForStateBuilding("the floor is reached 0")))
    }

    test("if door is open, building.down return Failure[IncoherentInstructionForStateBuilding]") {
        Building(doorIsOpen = true).down() should be(Failure(IncoherentInstructionForStateBuilding("the door is opened")))
    }

    test("when Building.down should tick all building.users") {
        val expectedBuildingUser = BuildingUser(parentActor = null, tickToWait = 1, from = 0, target = 1)

        val buildingUser = mock[BuildingUser]
        when(buildingUser.tick(any[Building])).thenReturn(expectedBuildingUser)

        Building(floor = 1, users = List(buildingUser)).down() should be (Success(Building(users = List(expectedBuildingUser), peopleWaitingTheElevator = Vector(1,0,0,0,0,0))))
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      *                          Building.close()                         *
      ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    test("if door is opened, building.close should close the door") {
        Building(doorIsOpen = true).close() should be (Success(Building()))
    }

    test("if door is closed, building.close should Failure[IncoherentInstructionForStateBuilding]") {
        Building().close() should be(Failure(IncoherentInstructionForStateBuilding("doors are already closed")))
    }

    test("when Building.close should tick all building.users") {
        val expectedBuildingUser = BuildingUser(parentActor = null, tickToWait = 1, from = 0, target = 1)

        val buildingUser = mock[BuildingUser]
        when(buildingUser.tick(any[Building])).thenReturn(expectedBuildingUser)

        Building(doorIsOpen = true, users = List(buildingUser)).close() should be (Success(Building(users = List(expectedBuildingUser), peopleWaitingTheElevator = Vector(1,0,0,0,0,0))))
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                          Building.open()                         *
     ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    test("if door is closed, building.open should open the door") {
        Building().open() should be (Success(Building(doorIsOpen = true)))
    }

    test("if door is opened, building.open should Failure[IncoherentInstructionForStateBuilding]") {
        Building(doorIsOpen = true).open() should be(Failure(IncoherentInstructionForStateBuilding("doors are already opened")))
    }

    test("when Building.open should tick all building.users") {
        val expectedBuildingUser = BuildingUser(parentActor = null, tickToWait = 1, from = 0, target = 1)

        val buildingUser = mock[BuildingUser]
        when(buildingUser.tick(any[Building])).thenReturn(expectedBuildingUser)

        Building(users = List(buildingUser)).open() should be (Success(Building(doorIsOpen = true, users = List(expectedBuildingUser), peopleWaitingTheElevator = Vector(1,0,0,0,0,0))))
    }

    test("value peopleWaitingTheElevator when Building.open()") {
        val user = mock[BuildingUser]
        when(user.tick(any[Building])).thenReturn(user)
        when(user.status).thenReturn(WAITING)
        when(user.from).thenReturn(0)

        val result = Building(users = List(user), peopleWaitingTheElevator = Vector(0, 0, 0, 0, 0, 0)).open().map(_.peopleWaitingTheElevator)

        result should be (Vector(1, 0, 0, 0, 0, 0).success)
    }

    test("if user enter into elevator, when Building.open(), should update peopleWaitingTheElevator") {
        val user = mock[BuildingUser]
        when(user.tick(any[Building])).thenReturn(user)
        when(user.status).thenReturn(TRAVELLING)
        when(user.from).thenReturn(0)

        val result = Building(users = List(user), peopleWaitingTheElevator = Vector(1, 0, 0, 0, 0, 0)).open().map(_.peopleWaitingTheElevator)
        result should be (Vector(0, 0, 0, 0, 0, 0).success)
    }

    test("change peopleWaitingTheElevator only if user enter into elevator") {
        val userTravelling = mock[BuildingUser]
        when(userTravelling.tick(any[Building])).thenReturn(userTravelling)
        when(userTravelling.status).thenReturn(TRAVELLING)
        when(userTravelling.from).thenReturn(0)

        val userWillTravelling = mock[BuildingUser]
        when(userWillTravelling.tick(any[Building])).thenReturn(userWillTravelling)
        when(userTravelling.from).thenReturn(1)
        when(userWillTravelling.status).thenReturn(WAITING).thenReturn(TRAVELLING)

        val result = Building(users = List(userTravelling), peopleWaitingTheElevator = Vector(0, 0, 0, 0, 0, 0)).open().map(_.peopleWaitingTheElevator)
        result should be (Vector(0, 0, 0, 0, 0, 0).success)
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                     Building.addBuildingUser()                   *
     ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    test("building.addBuildingUser should add user") {
        Building(users = List(BuildingUser(parentActor = null, from = 0, target = 1))).addBuildingUser(TestActorRef(new EngineActor(null, ""))).users should have size 2
    }

    test("if users number is reached maximum should don't addUser") {
        Building(maxUser = 0).addBuildingUser(null).users should be ('empty)
    }

    test("when Building.tick(), building.users.tick should be call") {
        val user = mock[BuildingUser]
        when(user.status).thenReturn(WAITING)
        when(user.tick(any[Building])).thenReturn(user)

        val building = Building(users = List(user))

        building.tick()

        verify(user).tick(building)
    }

    test("when Building.addBuildingUser(), should update peopleWaitingTheElevator") {
        val user = mock[BuildingUser]
        when(user.from).thenReturn(0)

        trait BuildingUserDeterministicCreator extends BuildingUserRandomCreator {
            override def createUser(building: Building, parentActor: ActorRef, random: Random = new Random()) = user
        }

        val building = new Building() with BuildingUserDeterministicCreator

        building.addBuildingUser(null).peopleWaitingTheElevator should be (Vector(1, 0, 0, 0, 0, 0))
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                     Building.reset()                             *
     ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    test("Building.reset() should return building reseted with -10 score") {
        val user = mock[BuildingUser]
        val building = Building(score = 10, peopleInTheElevator = 1, peopleWaitingTheElevator = Vector(0,0,0,0,0,1), doorIsOpen = true, floor = 0, users = List(user))
        building.reset(TestProbe().ref, ResetCause("")) should be (Building(score = 0))
    }

    test("Building.reset() should send engine ! SendEventToPlayer(Reset)") {
        val parentActor = TestProbe()

        Building().reset(parentActor.ref, ResetCause("reset"))

        parentActor.expectMsg(SendEventToPlayer(Reset(ResetCause("reset"))))
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                      USER DONE - SCORE                           *
     ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // https://github.com/xebia-france/code-elevator/blob/master/elevator-server/src/main/java/elevator/server/Score.java
    test("if user status is DONE when Building.open(), should calcul score") {
        val user = mock[BuildingUser]
        when(user.tick(any[Building]))
            .thenReturn(BuildingUser(null, 0, 1, tickToGo = 0, tickToWait = 0, status = DONE))
            .thenReturn(BuildingUser(null, 0, 1, tickToGo = 10, tickToWait = 0, status = DONE))
            .thenReturn(BuildingUser(null, 0, 1, tickToGo = 20, tickToWait = 20, status = DONE))
            .thenReturn(BuildingUser(null, 0, 1, tickToGo = 20, tickToWait = 20, status = DONE))
            .thenReturn(BuildingUser(null, 0, 1, tickToGo = 0, tickToWait = 20, status = DONE))

        Building(users = List(user)).open().map(_.score) should be (20.success)
        Building(users = List(user)).open().map(_.score) should be (13.success)
        Building(users = List(user)).open().map(_.score) should be (0.success)
        Building(score = 1, users = List(user)).open().map(_.score) should be (1.success)
        Building(users = List(user)).open().map(_.score) should be (13.success)
    }

    test("if user status is DONE when Building.open(), should delete this user") {
        import scalaz.Scalaz._

        val user = mock[BuildingUser]
        when(user.tick(any[Building]))
            .thenReturn(BuildingUser(null, 0, 1, tickToGo = 0, tickToWait = 0, status = DONE))

        Building(users = List(user)).open().map(_.users) should be (Nil.success)
    }
}