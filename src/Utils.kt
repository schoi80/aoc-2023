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
 * Caps a number. Wrap around if exceeds.
 */
fun Int.cap(cap: Int): Int {
    val r = this % cap
    return if (r == 0) cap else r
}

fun Char.hexToBinary() = digitToInt(16).toString(2).padStart(4, '0')