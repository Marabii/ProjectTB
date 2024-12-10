//Hamza Dadda: hamza.dadda@etu.emse.fr

//Starting Kotlin course
//my first hello world code in Kotlin

fun main(args: Array<String>) {
    //Hello world:
    val name = "Hamza Dadda"
    println("Hello World I am $name")

    //Testing classes:
    val me: Person = Person("Hamza", "Dadda")
    println(me.sayHello())

}

class Person(private val firstName: String, private val lastName: String) {
    fun sayHello(): String {
        return "Hello $firstName $lastName"
    }
}

//My first project in Kotlin

