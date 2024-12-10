fun main(args: Array<String>) {
    // 1. Display Hello World to the console:
    print("Q1 ")
    println("Hello Kotlin World")

    // 4.
    print("Q4 ")
    DisplayNamesOfRooms()

    // 5.
    print("Q5 ")
    DisplayFilteredRooms()

    // 8.
    print("Q8 ")
    printNotificationSummary(51)
    print("Q8 ")
    printNotificationSummary(135)

    // 9.

    val child = 5
    val adult = 28
    val senior = 87
    val oops = -1

    print("Q9 ")
    println("The movie ticket price for a person aged $child is $${ticketPrice(child)}.")
    println("The movie ticket price for a person aged $adult is $${ticketPrice(adult)}.")
    println("The movie ticket price for a person aged $senior is $${ticketPrice(senior)}.")
    println("The movie ticket price for a person aged oops is ${ticketPrice(oops)}.")

    // 10.

    val elodie = Profile("Elodie", 21, "Tennis", null)
    val eduardo = Profile("Eduardo", 22, "Tennis", elodie)

    displayUserProfile(listOf(elodie, eduardo))
}

// 2. Create a data class to manage your rooms.
data class RoomDto (
    val id: Long,
    val name: String,
    val currentTemperature: Double? = null
)

// 3. Create an immutable List in your main function with several rooms.
val rooms = listOf(
    RoomDto(1, "Room1"),
    RoomDto(2, "Room2", 20.3),
    RoomDto(id = 3, name = "Room3", currentTemperature = 20.3),
    RoomDto(4, "Room4", currentTemperature = 19.3),
)

// 4. Display the name of each room in the console.
fun DisplayNamesOfRooms() {
    val roomNamesList = rooms.map { room ->  room.name }
    val roomNames = roomNamesList.joinToString()
    println(roomNames)
}

// 5. Filter the rooms with a temperature greater than 20°
fun DisplayFilteredRooms() {
    val roomNamesList = rooms
        .filter { it.currentTemperature != null && it.currentTemperature >= 20 }
        .map {it.name}
    val roomNames = roomNamesList.joinToString()
    println(roomNames)
}

// 6. Declare a nullable variable called mainRoom in your code.

fun defineMainRoom() {
    // Declare a nullable variable
    var mainRoom: RoomDto? = RoomDto(5, "Room5", currentTemperature = 19.3)

    println("Current temperature: ${mainRoom?.currentTemperature}")
}

// 7. Create a function to compute the number of characters in a room name.

fun countNumberOfCharacters(inputRoom: RoomDto?): Int? {
    val roomLength: Int? = inputRoom?.name?.length
    return roomLength
}

// 8. Implement printNotificationSummary:

fun printNotificationSummary(numberOfMessages: Int) {
    val numberToShow = if (numberOfMessages > 99) "99+" else numberOfMessages.toString()
    println("You received $numberToShow notifications")
}

// 9. Concert ticket price

fun ticketPrice(age: Int): Int? {
    return when {
        age in 0..12 -> 10          // Children's ticket price: $10
        age in 13..64 -> 20         // Standard ticket price: $20
        age >= 65 -> 15             // Senior ticket price: $15
        else -> null                // Invalid age returns null
    }
}

// 10. Display user profile

data class Profile(
    val username: String,
    val age: Int,
    val hobby: String,
    val partner: Profile?
)

fun displayUserProfile(profiles: List<Profile>) {
    profiles.forEach{
        println("Name : ${it.username}")
        println("Age: ${it.age}")
        println("Hobby: ${it.hobby}")
        println("Partner: ${it.partner}")
    }
}
