package model

object BuildingUser {
    // TODO implemente ramdom method
    def randomCreate(): BuildingUser = {
        BuildingUser()
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
case class BuildingUser() {

}