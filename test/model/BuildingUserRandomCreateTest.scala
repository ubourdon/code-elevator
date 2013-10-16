package model

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import org.mockito.Mockito._
import akka.testkit.{TestActorRef, TestKit}
import actor._
import akka.actor.ActorSystem
import testing.tools.{ActorTestingTools, ActorStub}
import concurrent.duration._
import actor.Go
import actor.CallPlayer
import actor.SendEventToPlayer

class BuildingUserRandomCreateTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers
                                   with MockitoSugar with BeforeAndAfterAll with BeforeAndAfter with ActorTestingTools {

    override def afterAll() { system.shutdown() }

    after { closeDummyActors("engine-test") }

    test("should create a user at floor not zero") {
        val building: Building = mock[Building]
        when(building.maxFloor).thenReturn(4)
        val random: Random = mock[Random]
        when(random.nextBoolean()).thenReturn(false)
        when(random.nextInt(4)).thenReturn(2)

        val user = BuildingUser.randomCreate(building, TestActorRef(new EngineActor(null, "")), random)

        user.from should be (3)
    }

    test("should create user at floor zero") {
        val maxFloor = 1

        val building: Building = mock[Building]
        when(building.maxFloor).thenReturn(maxFloor)
        val random: Random = mock[Random]
        when(random.nextBoolean()).thenReturn(true)
        when(random.nextInt(maxFloor + 1)).thenReturn(1)

        val user = BuildingUser.randomCreate(building, TestActorRef(new EngineActor(null, "")), random)

        user.from should be (0)
    }

    test("should create a user wanting to go at 5th floor") {
        val building: Building = mock[Building]
        when(building.maxFloor).thenReturn(4)
        val random: Random = mock[Random]
        when(random.nextInt(5)).thenReturn(4)

        val user = BuildingUser.randomCreate(building, TestActorRef(new EngineActor(null, "")), random)

        user.target should be (4)
    }

    test("should create a user which different from & to parameter") {
        val maxFloor = 4

        val building = mock[Building]
        when(building.maxFloor).thenReturn(maxFloor)
        val random: Random = mock[Random]
        when(random.nextBoolean()).thenReturn(true)
        when(random.nextInt(maxFloor + 1)).thenReturn(0).thenReturn(1)

        val engine = TestActorRef(new EngineActor(null, ""))
        val user = BuildingUser.randomCreate(building, engine, random)

        user should be (BuildingUser(from = 0, target = 1, parentActor = engine))
    }

    test("when create user with randomCreate should call his engineActor ! CallPlayer(BuildingUser)") {
        val building = mock[Building]
        when(building.maxFloor).thenReturn(1)
        val engine = TestActorRef(new ActorStub(testActor), "engine-test")

        val user = BuildingUser.randomCreate(building, engine)

        expectMsg(1 second, CallPlayer(user))
    }

    test("if building door is open & building is at user floor, when create user with randomCreate, user should be in TRAVELLING STATE") {
        val building = mock[Building]
        when(building.maxFloor).thenReturn(1)
        when(building.doorIsOpen).thenReturn(true)
        when(building.floor).thenReturn(0)           // floor = 0
        
        val random = mock[Random]
        when(random.nextBoolean()).thenReturn(true)  // from = 0
        when(random.nextInt(2)).thenReturn(1)

        val engine = TestActorRef(new ActorStub(testActor), "engine-test")

        val user = BuildingUser.randomCreate(building, engine, random)

        user.status should be (TRAVELLING)
    }

    test("if building door is open & building is at user floor, when create user with randomCreate, should send engine ! SendEventToPlayer(UserHasEntered)") {
        val building = mock[Building]
        when(building.maxFloor).thenReturn(1)
        when(building.doorIsOpen).thenReturn(true)
        when(building.floor).thenReturn(0)           // floor = 0

        val random = mock[Random]
        when(random.nextBoolean()).thenReturn(true)  // from = 0
        when(random.nextInt(2)).thenReturn(1)

        val engine = TestActorRef(new ActorStub(testActor), "engine-test")

        BuildingUser.randomCreate(building, engine, random)

        receiveN(3).collectFirst { case e: SendEventToPlayer if(e.event.isInstanceOf[UserHasEntered.type] ) => e } should be ('defined)
    }

    test("if building door is open & building is at user floor, when create user with randomCreate, should send engine ! SendEventToPlayer(Go)") {
        val building = mock[Building]
        when(building.maxFloor).thenReturn(1)
        when(building.doorIsOpen).thenReturn(true)
        when(building.floor).thenReturn(0)           // floor = 0

        val random = mock[Random]
        when(random.nextBoolean()).thenReturn(true)  // from = 0
        when(random.nextInt(2)).thenReturn(1)

        val engine = TestActorRef(new ActorStub(testActor), "engine-test")

        val user = BuildingUser.randomCreate(building, engine, random)

        receiveN(3).collectFirst { case e: SendEventToPlayer if(e.event == (Go(user))) => e } should be ('defined)
    }
}