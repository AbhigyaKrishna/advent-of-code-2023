import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Repeats the string n times with the given separator.
 */
fun String.repeat(n: Int, separator: String = "") = (1..n).joinToString(separator) { this }

/**
 * Repeats the list n times.
 */
fun <T> List<T>.repeat(n: Int) = (1..n).flatMap { this }

/**
 * Splits this collection into a list of lists with the given splitter condition.
 * The splitter is not included in the result.
 */
fun <T> Iterable<T>.chunked(splitter: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<MutableList<T>>()
    var curr = mutableListOf<T>()
    for (item in this) {
        if (splitter(item)) {
            result.add(curr)
            curr = mutableListOf()
        } else {
            curr.add(item)
        }
    }
    result.add(curr)
    return result
}

/**
 * Represents a 2 dimensional direction.
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}

/**
 * Represents a 2D point.
 */
data class Point2D(val x: Int, val y: Int) {

    companion object {
        val NORTH = Point2D(0, -1)
        val EAST = Point2D(1, 0)
        val SOUTH = Point2D(0, 1)
        val WEST = Point2D(-1, 0)
    }

    operator fun plus(other: Point2D) = Point2D(x + other.x, y + other.y)

    operator fun minus(other: Point2D) = Point2D(x - other.x, y - other.y)

    fun translate(direction: Direction): Point2D {
        return when (direction) {
            Direction.UP -> Point2D(x, y - 1)
            Direction.DOWN -> Point2D(x, y + 1)
            Direction.LEFT -> Point2D(x - 1, y)
            Direction.RIGHT -> Point2D(x + 1, y)
        }
    }

}
