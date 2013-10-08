package model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class BuildingTest extends FunSuite with ShouldMatchers {

    test("building.up should increment floor by 1") {
        Building().up() should be (Building(floor = 1))
    }

    test("if the floor is reached maximum, building.up throw IllegalStateException") {
        val exception = evaluating { Building(maxFloor = 0).up() } should produce[IllegalStateException]
        exception should have ('message("the floor is reached maximum"))
    }

    test("if door is open, building.up throwIllegalSateException") {
        val exception = evaluating { Building(doorIsOpen = true).up() } should produce[IllegalStateException]
        exception should have ('message("the door is opened"))
    }

    test("building.down should decrement floor by 1") {
        Building(floor = 1).down() should be (Building(floor = 0))
    }

    test("if the floor is reached 0, building.down throw IllegalStateException") {
        val exception = evaluating { Building().down() } should produce[IllegalStateException]
        exception should have ('message("the floor is reached 0"))
    }

    test("if door is open, building.down throw IllegalSateException") {
        val exception = evaluating { Building(doorIsOpen = true).down() } should produce[IllegalStateException]
        exception should have ('message("the door is opened"))
    }

    test("if door is opened, building.close should close the door") {
        Building(doorIsOpen = true).close() should be (Building())
    }

    test("if door is closed, building.close should throw IllegalStateException") {
        val exception = evaluating { Building().close() } should produce[IllegalStateException]
        exception should have ('message("doors are already closed"))
    }

    test("if door is closed, building.open should open the door") {
        Building().open() should be (Building(doorIsOpen = true))
    }

    test("if door is opened, building.open should throw IllegalStateException") {
        val exception = evaluating { Building(doorIsOpen = true).open() } should produce[IllegalStateException]
        exception should have ('message("doors are already opened"))
    }

    // test addUser
        //ajoute un user dans le building
            // si limite atteinte - on ajoute pas
            // sinon add user avec floor de départ et floor souhaité

}