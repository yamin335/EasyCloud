package ltd.royalgreen.pacecloud

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        fun getResult(): String {
            val s = "abcdefghhgfedecba".toCharArray()
            val charValue = mapOf(
                'a' to 0, 'b' to 1, 'c' to 2, 'd' to 3, 'e' to 4, 'f' to 5,
                'g' to 6, 'h' to 7, 'i' to 8, 'j' to 9, 'k' to 10, 'l' to 11,
                'm' to 12, 'n' to 13, 'o' to 14, 'p' to 15, 'q' to 16, 'r' to 17,
                's' to 18, 't' to 19, 'u' to 20, 'v' to 21, 'w' to 22, 'x' to 23,
                'y' to 24, 'z' to 25)
            val frequency = Array(26){0}
            var i = 0
            var upper = 0
            var lower = 0
            var upperCount = 0

            while (i < s.size) {
                val index = charValue[s[i]]
                if ( index != null) {
                    frequency[index]++
                    if (lower == 0) {
                        lower = frequency[index]
                    }
                    if (frequency[index] > upper) {
                        lower = upper
                        upper = frequency[index]
                    }
                }
                i++
            }

            i = 0

            while (i < 26) {
                if (frequency[i] == upper) {
                    upperCount++
                }
                if (frequency[i] < lower && frequency[i] != 0) {
                    return "NO"
                }
                i++
            }

            return if (upper - lower < 2 && upperCount == 1) {
                "YES"
            } else {
                "NO"
            }
        }

        println(getResult())

        assertEquals(4, 2 + 2)
    }
}
