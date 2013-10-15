package model

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import testing.tools.ActorTestingTools

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
}