import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

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
 * Calculates the area of the given coordinates using Pick's Theorem.
 */
fun calculateAreaPicksTheorem(coords: List<Point2D>): Long {
    var area = 0L
    var boundary = 0L
    for (i in coords.indices) {
        val (x1, y1) = coords.elementAt(i)
        val (x2, y2) = coords.elementAt((i + 1) % coords.size)
        area += x1.toLong() * y2.toLong() - x2.toLong() * y1.toLong()
        boundary += abs(x1.toLong() - x2.toLong()) + abs(y1.toLong() - y2.toLong())
    }

    return (abs(area) - boundary + 2) / 2
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
        val ZERO = Point2D(0, 0)

        val NORTH = Point2D(0, -1)
        val EAST = Point2D(1, 0)
        val SOUTH = Point2D(0, 1)
        val WEST = Point2D(-1, 0)
    }

    operator fun plus(other: Point2D) = Point2D(x + other.x, y + other.y)

    operator fun minus(other: Point2D) = Point2D(x - other.x, y - other.y)

    fun translate(direction: Direction, units: Int = 1): Point2D {
        return when (direction) {
            Direction.UP -> Point2D(x, y - units)
            Direction.DOWN -> Point2D(x, y + units)
            Direction.LEFT -> Point2D(x - units, y)
            Direction.RIGHT -> Point2D(x + units, y)
        }
    }

    override fun toString(): String {
        return "($x, $y)"
    }

}
