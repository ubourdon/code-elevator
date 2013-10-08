package model

case class Building(score: Int = 0, peopleWaitingTheElevator: Vector[Int] = Vector(0, 0, 0, 0, 0, 0), floor: Int = 0, peopleInTheElevator: Int = 0, doorIsOpen: Boolean = false, maxFloor: Int = 5) {
    /*private val maxFloor = 5

    private var floor: Int = 0
    private var doorIsOpened = false
    score: Int
    */

    def addUser(): Unit = ()

    // TODO use validation ???
    def up(): Building =
        if(doorIsOpen) throw new IllegalStateException("the door is opened")
        else if(floor < maxFloor) this.copy(floor = this.floor + 1)
        else throw new IllegalStateException("the floor is reached maximum")

    def down(): Building =
        if(doorIsOpen) throw new IllegalStateException("the door is opened")
        else if(floor > 0) this.copy(floor = this.floor - 1)
        else throw new IllegalStateException("the floor is reached 0")

    def open(): Building =
        if(doorIsOpen) throw new IllegalStateException("doors are already opened")
        else this.copy(doorIsOpen = true)

    def close(): Building =
        if(!doorIsOpen) throw new IllegalStateException("doors are already closed")
        else this.copy(doorIsOpen = false)
}