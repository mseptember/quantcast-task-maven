import java.io.File

class CookieCommand {
    fun parseArguments(args: Array<String>): Pair<String, String> {
        if (args.size != 4) {
            throw IllegalArgumentException("Usage: -f <filename> -d <date>")
        }
        var fileName = ""
        var date = ""

        for (i in args.indices step 2) {
            when (args[i]) {
                "-f" -> fileName = args[i + 1]
                "-d" -> date = args[i + 1]
                else -> {
                    throw IllegalArgumentException("Unknown argument: ${args[i]}")
                }
            }
        }

        if (fileName.isBlank() || date.isBlank()) {
            throw IllegalArgumentException("Both -f and -d arguments must be provided")
        }

        return fileName to date
    }

    fun readCookiesForDate(filePath: String, targetDate: String): List<CookieEntry> {
        return File(filePath).useLines { lines ->
            lines.drop(1) // skip header
                .mapNotNull { parseLine(it, targetDate) }.toList()
        }
    }

    fun parseLine(line: String, targetDate: String): CookieEntry? {
        val parts = line.split(",")
        if (parts.size != 2) return null

        val cookie = parts[0].trim()
        val timestamp = parts[1].trim()
        val dateOnly = timestamp.take(10)

        return if (dateOnly == targetDate) CookieEntry(cookie, timestamp) else null
    }

    fun findMostActiveCookies(cookies: List<String>): List<String> {
        if (cookies.isEmpty()) return emptyList()
        val frequencyMap = cookies.groupingBy { it }.eachCount()
        val maxCount = frequencyMap.values.maxOrNull() ?: return emptyList()

        return frequencyMap.filterValues { it == maxCount }.keys.toList()
    }
}

fun main(args: Array<String>) {
    val command = CookieCommand()

    try {
        val (filePath, date) = command.parseArguments(args)
        val cookieEntries = command.readCookiesForDate(filePath, date)
        val mostActive = command.findMostActiveCookies(cookieEntries.map { it.cookie })

        mostActive.forEach { println(it) }

    } catch (e: IllegalArgumentException) {
        System.err.println(e.message)
    }
}

