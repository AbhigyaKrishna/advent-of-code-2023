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
