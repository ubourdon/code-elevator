package model

import scalaz._
import Scalaz._

case class Building(score: Int = 0,
                    peopleWaitingTheElevator: Vector[Int] = Vector(0, 0, 0, 0, 0, 0),
                    floor: Int = 0,
                    peopleInTheElevator: Int = 0,
                    doorIsOpen: Boolean = false,
                    maxFloor: Int = 5) {

    def addUser(): Building = null
        // créer User si limite pas dépasser
            // User(from, target, tickToWait, tickToGo, currentBuildingFloor, currentBuildingDoorsStatus, status: Waiting/travelling/DONE)

            //dans le user
                //à sa construction
                    // User.call vers le player    ?at=from&direction=up/down         fire & forget
                    // si porte ouverte et ascenseur à son étage d'origine => status = travelling & userAreEntered + go

                // quand il est notifié
                    // envoi event

            // score quand user sors de l'scenseur envoie son score à building
                //voir github score tests

    // def tick()     notifie tous les users que 1 tick est passé

    // TODO notifie user nouvel etat building
    def up(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor < maxFloor) this.copy(floor = this.floor + 1).success
        else IncoherentInstructionForStateBuilding("the floor is reached maximum").fail

    // TODO notifie user nouvel etat building
    def down(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("the door is opened").fail
        else if(floor > 0) this.copy(floor = this.floor - 1).success
        else IncoherentInstructionForStateBuilding("the floor is reached 0").fail

    // TODO notifie user nouvel etat building
    def open(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(doorIsOpen) IncoherentInstructionForStateBuilding("doors are already opened").fail
        else this.copy(doorIsOpen = true).success

    // TODO notifie user nouvel etat building
    def close(): Validation[IncoherentInstructionForStateBuilding, Building] =
        if(!doorIsOpen) IncoherentInstructionForStateBuilding("doors are already closed").fail
        else this.copy(doorIsOpen = false).success
}

case class IncoherentInstructionForStateBuilding(message: String)