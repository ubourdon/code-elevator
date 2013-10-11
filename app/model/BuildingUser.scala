package model

import scala.util.Random

object BuildingUser {
    def randomCreate(random: Random = new Random(), building: Building): BuildingUser = {
        val random_from = if(random.nextBoolean()) 0 else random.nextInt(building.maxFloor) + 1
        val random_to = {
            def to_different_of_from(): Int = {
                val to = random.nextInt(building.maxFloor + 1)
                if (to == random_from) to_different_of_from()
                else to
            }
            to_different_of_from()
        }

        BuildingUser(from = random_from, target = random_to)
    }
}

// créer User si limite pas dépassée
// BuildingUser(from, target, tickToWait, tickToGo, currentBuildingFloor, currentBuildingDoorsStatus, status: Waiting/travelling/DONE)

//dans le user
    //à sa construction
        // User.call vers le player    ?at=from&direction=up/down         fire & forget
        // si porte ouverte et ascenseur à son étage d'origine => status = travelling & userAreEntered + go

// quand il est notifié
    // envoi event

// score quand user sors de l'scenseur envoie son score à building
    //voir github score tests
case class BuildingUser(from: Int, target: Int, tickToWait: Int = 0/*, currentBuildingFloor: Int*//*, currentBuildingDoorsStatus: Boolen, tickToGo: Int = 0, status: BuildingUserStatus = WAITING*/) {
    def tick(): BuildingUser = null
}

sealed trait BuildingUserStatus
case object WAITING extends BuildingUserStatus
case object TRAVELLING extends BuildingUserStatus
case object DONE extends BuildingUserStatus