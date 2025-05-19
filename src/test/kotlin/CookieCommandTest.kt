import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.createTempFile

class CookieCommandTest {

    private val command = CookieCommand()

    @Test
    fun `parseArguments returns filename and date when correct args are passed`() {
        val args = arrayOf("-f", "cookie_log.csv", "-d", "2018-12-09")
        val (file, date) = command.parseArguments(args)
        assertEquals("cookie_log.csv", file)
        assertEquals("2018-12-09", date)
    }

    @Test
    fun `parseArguments throws exception if arguments are missing`() {
        val args = arrayOf("-f", "cookie_log.csv")
        val exception = assertThrows<IllegalArgumentException> {
            command.parseArguments(args)
        }
        assertTrue(exception.message!!.contains("Usage"))
    }

    @Test
    fun `parseArguments throws exception for unknown flag`() {
        val args = arrayOf("-x", "cookie_log.csv", "-d", "2018-12-09")
        val exception = assertThrows<IllegalArgumentException> {
            command.parseArguments(args)
        }
        assertTrue(exception.message!!.contains("Unknown argument"))
    }

    @Test
    fun `parseArguments throws exception when values are blank`() {
        val args = arrayOf("-f", "", "-d", "")
        val exception = assertThrows<IllegalArgumentException> {
            command.parseArguments(args)
        }
        assertTrue(exception.message!!.contains("Both -f and -d"))
    }

    @Test
    fun `parseLine returns CookieEntry if date matches`() {
        val line = "ABC123,2018-12-09T10:13:00+00:00"
        val result = command.parseLine(line, "2018-12-09")
        assertEquals(CookieEntry("ABC123", "2018-12-09T10:13:00+00:00"), result)
    }

    @Test
    fun `parseLine returns null if date does not match`() {
        val line = "XYZ789,2018-12-08T10:13:00+00:00"
        val result = command.parseLine(line, "2018-12-09")
        assertNull(result)
    }

    @Test
    fun `parseLine returns null for empty line`() {
        val result = command.parseLine("", "2018-12-09")
        assertNull(result)
    }

    @Test
    fun `parseLine returns null for line with only one field`() {
        val result = command.parseLine("onlycookie", "2018-12-09")
        assertNull(result)
    }

    @Test
    fun `parseLine returns null for badly formatted timestamp`() {
        val line = "cookie123,not-a-valid-timestamp"
        val result = command.parseLine(line, "2018-12-09")
        assertNull(result)
    }

    @Test
    fun `findMostActiveCookies returns correct cookie`() {
        val input = listOf("a", "b", "a", "c", "a", "b")
        val result = command.findMostActiveCookies(input)
        assertEquals(listOf("a"), result)
    }

    @Test
    fun `findMostActiveCookies returns all with max frequency`() {
        val input = listOf("a", "b", "a", "b", "c")
        val result = command.findMostActiveCookies(input)
        assertEquals(setOf("a", "b"), result.toSet())
    }

    @Test
    fun `readCookiesForDate filters only matching lines`() {
        val tmpFile = createTempFile().toFile()
        tmpFile.writeText(
            """
            cookie,timestamp
            abc,2018-12-09T10:00:00+00:00
            def,2018-12-09T12:00:00+00:00
            xyz,2018-12-08T09:00:00+00:00
        """.trimIndent()
        )

        val result = command.readCookiesForDate(tmpFile.absolutePath, "2018-12-09")
        assertEquals(
            listOf(
                CookieEntry("abc", "2018-12-09T10:00:00+00:00"),
                CookieEntry("def", "2018-12-09T12:00:00+00:00")
            ),
            result
        )
    }

    @Test
    fun `readCookiesForDate returns empty list when no cookies match date`() {
        val tmpFile = createTempFile().toFile()
        tmpFile.writeText(
            """
        cookie,timestamp
        abc,2018-12-08T10:00:00+00:00
        def,2018-12-08T12:00:00+00:00
        xyz,2018-12-08T09:00:00+00:00
        """.trimIndent()
        )

        val result = command.readCookiesForDate(tmpFile.absolutePath, "2018-12-09")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `integration test with sample csv`() {
        val file = createTempFile().toFile()
        file.writeText("""
            cookie,timestamp
            AtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00
            SAZuXPGUrfbcn5UA,2018-12-09T10:13:00+00:00
            5UAVanZf6UtGyKVS,2018-12-09T07:25:00+00:00
            AtY0laUfhglK3lC7,2018-12-09T06:19:00+00:00
            SAZuXPGUrfbcn5UA,2018-12-08T22:03:00+00:00
            4sMM2LxV07bPJzwf,2018-12-08T21:30:00+00:00
        """.trimIndent())

        val cookies = command.readCookiesForDate(file.absolutePath, "2018-12-09")
        val result = command.findMostActiveCookies(cookies.map { it.cookie })

        assertEquals(listOf("AtY0laUfhglK3lC7"), result)
    }
}
