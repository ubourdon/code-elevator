package model

case class Building(score: Int = 0, peopleWaitingTheElevator: Vector[Int] = Vector(0, 0, 0, 0, 0, 0), floor: Int = 0, peopleInTheElevator: Int = 0, doorIsOpen: Boolean = false) {
    /*private val maxFloor = 5

    private var floor: Int = 0
    private var doorIsOpened = false
    score: Int
    */

    def addUser(): Unit = ()
    def up(): Building = throw new IllegalStateException("not implemented")                                  /*if(floor < maxFloor) floor = floor + 1$*/
    def down(): Building = throw new IllegalStateException("not implemented")                                 //if(floor > 0) floor = floor - 1
    def open(): Building = throw new IllegalStateException("not implemented")
    def close(): Building = throw new IllegalStateException("not implemented")
}