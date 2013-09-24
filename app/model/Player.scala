package model

case class Player(email: String, pseudo: String, password: String)


trait PlayerJsonSerializer {
    import play.api.libs.json._

    implicit val playerWrites = Json.writes[Player]
    implicit val playerReads = Json.reads[Player]
}