package model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import org.mockito.Mockito

class BuildingUserRandomCreateTest extends FunSuite with ShouldMatchers with MockitoSugar {

    test("should create a user at floor not zero") {
        val building: Building = mock[Building]
        Mockito.when(building.maxFloor).thenReturn(4)
        val random: Random = mock[Random]
        Mockito.when(random.nextBoolean()).thenReturn(false)
        Mockito.when(random.nextInt(4)).thenReturn(2)

        val user = BuildingUser.randomCreate(random, building)

        user.from should be (3)
    }

    test("should create user at floor zero") {
        val maxFloor = 1

        val building: Building = mock[Building]
        Mockito.when(building.maxFloor).thenReturn(maxFloor)
        val random: Random = mock[Random]
        Mockito.when(random.nextBoolean()).thenReturn(true)
        Mockito.when(random.nextInt(maxFloor + 1)).thenReturn(1)

        val user = BuildingUser.randomCreate(random, building)

        user.from should be (0)
    }

    test("should create a user wanting to go at 5th floor") {
        val building: Building = mock[Building]
        Mockito.when(building.maxFloor).thenReturn(4)
        val random: Random = mock[Random]
        Mockito.when(random.nextInt(5)).thenReturn(4)

        val user = BuildingUser.randomCreate(random, building)

        user.target should be (4)
    }

    test("should create a user which different from & to parameter") {
        val maxFloor = 4

        val building: Building = mock[Building]
        Mockito.when(building.maxFloor).thenReturn(maxFloor)
        val random: Random = mock[Random]
        Mockito.when(random.nextBoolean()).thenReturn(true)
        Mockito.when(random.nextInt(maxFloor + 1)).thenReturn(0).thenReturn(1)

        val user = BuildingUser.randomCreate(random, building)

        user should be (BuildingUser(from = 0, target = 1))
    }
}