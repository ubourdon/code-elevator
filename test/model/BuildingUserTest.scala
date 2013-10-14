package model

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import testing.tools.{ActorStub, ActorTestingTools}
import concurrent.duration._
import actor.CallPlayer

class BuildingUserTest extends TestKit(ActorSystem("test")) with FunSuite with ShouldMatchers with BeforeAndAfterAll
                                                            with BeforeAndAfter with MockitoSugar
                                                            with ImplicitSender  with ActorTestingTools {

    override def afterAll() { system.shutdown() }

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

    ignore("when BuildingUser is created should call his engineActor ! CallPlayer(BuildingUser)") {
        val engine = TestActorRef(new ActorStub(testActor), "engine-test")

        BuildingUser(engine, 0, 0)

        expectMsg(1 second, CallPlayer(BuildingUser(engine, 0, 0)))
    }

    /*ignore("if new BuildingUser call GET /call?atFloor=[0-5]&to=[UP|DOWN] & server does not respond, should throw PlayerServerConnectError") {
        val exception = evaluating { BuildingUser(parentActor = null, from = 0, target = 0) } should produce[ConnectException]
        exception.getMessage should be("Connection refused: localhost/127.0.0.1:8080 to http://localhost:8080/call?atFloor=0&to=UP")
    }

    ignore("if new BuildingUser call GET /call?atFloor=[0-5]&to=[UP|DOWN] & server does not respond 200, should throw PlayerServerConnectError") {
        val route = GET (
            path = "/call",
            response = StaticServerResponse(Text_Plain, "not 200", 201)
        )

        withStubServerFixture(8080, route) { server =>
            val exception = evaluating { BuildingUser(parentActor = null, from = 0, target = 0) } should produce[PlayerServerConnectError]
            exception.getMessage should be(s"remote server respond 201 from [GET] http://localhost:${server.portInUse}/call?atFloor=0&to=UP")
        }
    }*/
}