/**
 * Represents a pair of row and column indices.
 */
typealias RowCol = Pair<Int, Int>

/**
 * Represents a list of strings as input.
 */
typealias Input = List<String>

/**
 * Checks if the given row-column pair is out of the bounds of the input grid.
 *
 * @param rc The row and column to check.
 * @return True if the row-column pair is out of bounds, false otherwise.
 */
fun Input.isOutOfBounds(rc: RowCol): Boolean {
    val inRange = rc.first >= 0 && rc.first < this.size && rc.second >= 0 && rc.second < this[rc.first].length
    return !inRange
}

/**
 * Retrieves the character at the specified row-column pair in the input grid.
 *
 * @param rc The row and column of the character to retrieve.
 * @return The character at the specified position.
 */
fun Input.get(rc: RowCol): Char {
    return this[rc.first][rc.second]
}

/**
 * Moves the position one step up.
 * @return The new position.
 */
fun RowCol.up() = first - 1 to second

/**
 * Moves the position one step down.
 * @return The new position.
 */
fun RowCol.down() = first + 1 to second

/**
 * Moves the position one step to the left.
 * @return The new position.
 */
fun RowCol.left() = first to second - 1

/**
 * Moves the position one step to the right.
 * @return The new position.
 */
fun RowCol.right() = first to second + 1

/**
 * Finds all adjacent positions (up, down, left, right) to the given position that are within bounds.
 *
 * @param rc The position to find adjacent positions for.
 * @return A list of adjacent positions that are within bounds.
 */
fun Input.adjacent(rc: RowCol): List<RowCol> {
    return listOf(rc.up(), rc.down(), rc.left(), rc.right()).filterNot { isOutOfBounds(it) }
}

typealias MutableInput<T> = Array<Array<T>>

inline fun <reified T> Input.toMutableInput(fn:(Char) -> T): MutableInput<T> {
    return this.map { s ->
        s.map(fn).toTypedArray()
    }.toTypedArray()
}