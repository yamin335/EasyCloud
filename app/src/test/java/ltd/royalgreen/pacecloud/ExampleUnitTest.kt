package ltd.royalgreen.pacecloud

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        fun superDigit(n: String, k: Int): Int {
            val map = mapOf('0' to 0L, '1' to 1L, '2' to 2L, '3' to 3L, '4' to 4L, '5' to 5L, '6' to 6L, '7' to 7L, '8' to 8L, '9' to 9L)
            if (n.length == 1 && k == 0) {
                return n.toInt()
            }
            var result = 0L
            var i = 0
            while (i < n.length) {
                result += map.getValue(n[i])
                i++
            }
            val temp = result
            i = 1
            while (i < k) {
                result += temp
                i++
            }
            val newN = result.toString()
            return superDigit(newN, 0)
        }

        println(superDigit("3546630947312051453014172159647935984478824945973141333062252613718025688716704470547449723886626736", 100000))
        assertEquals(4, 2 + 2)
    }

    internal object GFG {

        const val INT_SIZE = 32 /// Assumed int size
        var root: TrieNode? = null

        fun insert(pre_xor: Int) { /// Inserts pre_xor to trie with given root
            var temp = root
            // Start from the msb, insert all bits of pre_xor into Trie
            for (i in INT_SIZE - 1 downTo 0) { // Find current bit in given prefix
                val `val` = if (pre_xor and (1 shl i) >= 1) 1 else 0
                // Create a new node if needed
                if (temp!!.arr[`val`] == null) temp.arr[`val`] = TrieNode()
                temp = temp.arr[`val`]
            }
            // Store value at leaf node
            temp!!.value = pre_xor
        }

        // Finds the maximum XOR ending with last number in
// prefix XOR 'pre_xor' and returns the XOR of this
// maximum with pre_xor which is maximum XOR ending
// with last element of pre_xor.
        fun query(pre_xor: Int): Int {
            var temp = root
            for (i in INT_SIZE - 1 downTo 0) { // Find current bit in given prefix
                val `val` = if (pre_xor and (1 shl i) >= 1) 1 else 0
                // Traverse Trie, first look for a
// prefix that has opposite bit
                if (temp!!.arr[1 - `val`] != null) temp =
                    temp.arr[1 - `val`] else if (temp.arr[`val`] != null) temp =
                    temp.arr[`val`]
            }
            return pre_xor xor temp!!.value
        }

        // Returns maximum XOR value of a subarray in
// arr[0..n-1]
        fun maxSubarrayXOR(arr: IntArray, n: Int): Int { // Create a Trie and insert 0 into it
            root = TrieNode()
            insert(0)
            // Initialize answer and xor of current prefix
            var result = Int.MIN_VALUE
            var pre_xor = 0
            // Traverse all input array element
            for (i in 0 until n) { // update current prefix xor and insert it
// into Trie
                pre_xor = pre_xor xor arr[i]
                insert(pre_xor)
                // Query for current prefix xor in Trie and
// update result if required
                result = Math.max(result, query(pre_xor))
            }
            return result
        }

        // Driver program to test above functions
        @JvmStatic
        fun main(args: Array<String>) {
            val arr = intArrayOf(8, 1, 2, 12)
            val n = arr.size
            println(
                "Max subarray XOR is " +
                        maxSubarrayXOR(arr, n)
            )
        }

        // A Trie Node
        internal class TrieNode {
            var value // Only used in leaf nodes
                    = 0
            var arr = arrayOfNulls<TrieNode>(2)

            init {
                arr[0] = null
                arr[1] = null
            }
        }
    }
}

