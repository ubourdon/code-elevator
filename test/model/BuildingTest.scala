package model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import scalaz.{Validation, Failure, Success}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito

class BuildingTest extends FunSuite with ShouldMatchers with MockitoSugar {

    test("building.up should increment floor by 1") {
        Building().up() should be (Success(Building(floor = 1)))
    }

    test("if the floor is reached maximum, building.up return Failure[IncoherentInstructionForStateBuilding]") {
        Building(maxFloor = 0).up() should be(Failure(IncoherentInstructionForStateBuilding("the floor is reached maximum")))
    }

    test("if door is open, building.up return Failure[IncoherentInstructionForStateBuilding]") {
        Building(doorIsOpen = true).up() should be(Failure(IncoherentInstructionForStateBuilding("the door is opened")))
    }

    test("building.down should decrement floor by 1") {
        Building(floor = 1).down() should be (Success(Building(floor = 0)))
    }

    test("if the floor is reached 0, building.down return Failure[IncoherentInstructionForStateBuilding]") {
        Building().down() should be(Failure(IncoherentInstructionForStateBuilding("the floor is reached 0")))
    }

    test("if door is open, building.down return Failure[IncoherentInstructionForStateBuilding]") {
        Building(doorIsOpen = true).down() should be(Failure(IncoherentInstructionForStateBuilding("the door is opened")))
    }

    test("if door is opened, building.close should close the door") {
        Building(doorIsOpen = true).close() should be (Success(Building()))
    }

    test("if door is closed, building.close should Failure[IncoherentInstructionForStateBuilding]") {
        Building().close() should be(Failure(IncoherentInstructionForStateBuilding("doors are already closed")))
    }

    test("if door is closed, building.open should open the door") {
        Building().open() should be (Success(Building(doorIsOpen = true)))
    }

    test("if door is opened, building.open should Failure[IncoherentInstructionForStateBuilding]") {
        Building(doorIsOpen = true).open() should be(Failure(IncoherentInstructionForStateBuilding("doors are already opened")))
    }

    test("building.addBuildingUser should add user") {
        Building(users = List(BuildingUser())).addBuildingUser().users should have size 2
    }

    test("if users number is reached maximum should don't addUser") {
        Building(maxUser = 0).addBuildingUser().users should be ('empty)
    }

    test("when Building.tick(), building.users.tick should be call") {
        val user = mock[BuildingUser]
        Building(users = List(user)).tick()

        Mockito.verify(user).tick()
    }
}