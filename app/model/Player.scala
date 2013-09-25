package model

case class Player(email: String, pseudo: String, password: String)

case class PlayerInfo(pseudo: String, email: String, score: Long, peopleWaitingTheElevator: Array[Int], elevatorAtFloor: Int, peopleInTheElevator: Int, doorIsOpen: Boolean, lastErrorMessage: String, state: String) {
    def this(player: Player) = this(player.pseudo, player.email, 0L, Array(0, 0, 3, 5, 2, 0), 2, 1, false, "last error message", "RESUME")
}


trait PlayerJsonSerializer {
    import play.api.libs.json._

    implicit val playerWrites = Json.writes[Player]
    implicit val playerReads = Json.reads[Player]
    implicit val playerInfoWrites = Json.writes[PlayerInfo]
    implicit val playerInfoReads = Json.reads[PlayerInfo]
}
