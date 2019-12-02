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

        fun abbreviation(a: String, b: String): String {
            var temp = ""
            if (a.length < b.length) {
                return "NO"
            } else {
                var tempA = a
                var tempB = b
                while (tempA.length >= tempB.length && tempA.isNotEmpty()) {
                    if (tempB.isNotEmpty()) {
                        if (tempB[0] == tempA[0].toUpperCase()) {
                            temp += tempB[0]
                            tempA = tempA.removeRange(0..0)
                            tempB = tempB.removeRange(0..0)
                        } else if (tempA[0].isLowerCase()){
                            tempA = tempA.removeRange(0..0)
                        } else {
                            temp = ""
                            break
                        }
                    } else if (tempA[0].isLowerCase()){
                        tempA = tempA.removeRange(0..0)
                    } else {
                        temp = ""
                        break
                    }
                }

                return if (temp.equals(b, false)) {
                    "YES"
                } else {
                    "NO"
                }
            }
        }

        println(abbreviation("BFZZVHdQYHQEMNEFFRFJTQmNWHFVXRXlGTFNBqWQmyOWYWSTDSTMJRYHjBNTEWADLgHVgGIRGKFQSeCXNFNaIFAXOiQORUDROaNoJPXWZXIAABZKSZYFTDDTRGZXVZZNWNRHMvSTGEQCYAJSFvbqivjuqvuzafvwwifnrlcxgbjmigkms", "BFZZVHQYHQEMNEFFRFJTQNWHFVXRXGTFNBWQOWYWSTDSTMJRYHBNTEWADLHVGIRGKFQSCXNFNIFAXOQORUDRONJPXWZXIAABZKSZYFTDDTRGZXVZZNWNRHMSTGEQCYAJSF"))

        assertEquals(4, 2 + 2)
    }
}
