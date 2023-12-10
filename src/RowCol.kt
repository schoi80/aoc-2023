typealias RowCol = Pair<Int, Int>

fun Input.isOutOfBounds(rc: RowCol): Boolean {
    val inRange = rc.first >= 0 && rc.first < this.size && rc.second >= 0 && rc.second < this[rc.first].length
    return !inRange
}

fun Input.get(rc: RowCol): Char {
    return this[rc.first][rc.second]
}

fun RowCol.up() = first - 1 to second
fun RowCol.down() = first + 1 to second
fun RowCol.left() = first to second - 1
fun RowCol.right() = first to second + 1

fun Input.adjacent(rc: RowCol): List<RowCol> {
    return listOf(rc.up(), rc.down(), rc.left(), rc.right()).filterNot { isOutOfBounds(it) }
}