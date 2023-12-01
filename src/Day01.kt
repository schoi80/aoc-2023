import java.util.regex.Pattern

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            it.replace("\\D".toRegex(), "").let {
                "${it.first()}${it.last()}".toInt()
            }
        }
    }

    fun replaceNumber(r:String): String {
        return when (r) {
            "one" -> "1"
            "two" -> "2"
            "three" -> "3"
            "four" -> "4"
            "five" -> "5"
            "six" -> "6"
            "seven" -> "7"
            "eight" -> "8"
            "nine" -> "9"
            else -> r
        }
    }

    fun findFirstAndLast(inputString: String): Int {
        val regex = "(?=(one|two|three|four|five|six|seven|eight|nine|\\d))"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(inputString)
        val found = mutableListOf<String>()
        while (matcher.find()) {
            found.add(matcher.group(1))
        }
        val first = replaceNumber(found.first()).toInt()
        val last = replaceNumber(found.last()).toInt()
        return first * 10 + last
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {orig ->
            findFirstAndLast(orig).also {
                println("$orig: $it")
            }
        }
    }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
