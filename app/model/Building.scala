package model

import scalaz._
import Scalaz._

case class Building(score: Int = 0,
                    peopleWaitingTheElevator: Vector[Int] = Vector(0, 0, 0, 0, 0, 0),
                    peopleInTheElevator: Int = 0,
                    doorIsOpen: Boolean = false,
                    maxFloor: Int = 5,
                    floor: Int = 0,
                    maxUser: Int = 3,
                    users: List[BuildingUser] = Nil) {

    def addBuildingUser(): Building = {
        if(maxUserIsReached) this
        else this.copy(users = BuildingUser.randomCreate(building = this) :: this.users)
    }

    def tick(): Building = notifyTickToUsers(this)

    def up(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor >= maxFloor) IncoherentInstructionForStateBuilding("the floor is reached maximum").fail
        else notifyTickToUsers(this).copy(floor = this.floor + 1).success

    def down(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor == 0) IncoherentInstructionForStateBuilding("the floor is reached 0").fail
        else notifyTickToUsers(this).copy(floor = this.floor - 1).success

    def open(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("doors are already opened").fail
        else notifyTickToUsers(this).copy(doorIsOpen = true).success

    def close(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(!doorIsOpen) IncoherentInstructionForStateBuilding("doors are already closed").fail
        else notifyTickToUsers(this).copy(doorIsOpen = false).success

    private def maxUserIsReached: Boolean = this.users.size >= maxUser

    private def notifyTickToUsers(building: Building): Building = building.copy(users = building.users.map( _.tick() ))
}

case class IncoherentInstructionForStateBuilding(message: String)