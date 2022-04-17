package halma

fun starhalmaFieldsToString(
    fields: List<Any>,
    width: Int = 1,
    padChar: Char = ' ',
    lines: Boolean = false
) = buildString {
    val spaceChar = if(lines) "-" else " "
    var idx = 0
    var indent = 12
    val space = width + 2

    fun line(n: Int, block: () -> Unit = { } ) {
        append(" ".repeat((width + space) / 2 * indent))
        repeat(n) { i ->
            if (i > 0) append(spaceChar.repeat(space))
            append(fields[idx++]
                .toString()
                .take(width)
                .padStart(width, padChar))
        }
        block()
        appendLine()
    }

    fun lineA(n: Int) {
        if (lines) {
            appendLine()
            append(" ".repeat(((width + space) / 2 * indent) - 1))
            repeat(n) { i ->
                if (i > 0) append(" ".repeat(width))
                append("/")
                append(" ".repeat(width))
                append("\\")
            }
        }
    }

    fun lineW(n: Int) {
        if (lines) {
            appendLine()
            append(" ".repeat(((width + space) / 2 * indent) + width))
            repeat(n - 1) { i ->
                if (i > 0) append(" ".repeat(width))
                append("\\")
                append(" ".repeat(width))
                append("/")
            }
        }
    }

    for (i in 1..4) { line(i) { lineA(i); indent-- } }
    indent = 0
    for (i in 13 downTo 10) { line(i) { lineW(i); indent++ } }
    for (i in 9..12) { line(i) { lineA(i); indent-- } }
    line(13) { indent = 8; lineW(5); indent++ }
    for (i in 4 downTo 1) { line(i) { lineW(i); indent++ } }
}

fun displayStarhalmaFields(
    fields: List<Any>,
    width: Int = 1,
    padChar: Char = ' ',
    lines: Boolean = false
) {
    println(starhalmaFieldsToString(fields, width, padChar, lines))
}