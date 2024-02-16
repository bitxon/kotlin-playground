package bitxon.test

import org.assertj.core.api.Assertions.*
import kotlin.test.Test

class StreamNumberTest {

    companion object {
        val NUMBERS_SORTED = (1..10).toList()
        val NUMBERS_SHUFFLED = listOf(1, 9, 8, 3, 7, 2, 5, 4, 10, 6)
    }

    @Test
    fun generateOddNumbers() {
        val numbers = generateSequence(1) { prev -> prev + 2  }
            .take(5)
            .toList()

        assertThat(numbers).containsExactly(1, 3, 5, 7, 9)
    }

    @Test
    fun splitOddAndEvenNumbers() {
        val (match, rest) = NUMBERS_SORTED
            .partition { it % 2 == 0 }

        assertThat(match).containsExactly(2, 4, 6, 8, 10)
        assertThat(rest).containsExactly(1, 3, 5, 7, 9)
    }

    @Test
    fun splitOddAndEvenNumbers2() {
        val values = NUMBERS_SORTED
            .groupBy { if (it % 2 == 0) "EVEN" else "ODD" }

        assertThat(values).isNotNull()
        assertThat(values["ODD"]).containsExactly(1, 3, 5, 7, 9)
        assertThat(values["EVEN"]).containsExactly(2, 4, 6, 8, 10)
    }

    @Test
    fun splitSortConcat() {
        val values = NUMBERS_SHUFFLED
            .groupBy { it % 2 == 0 }
            .flatMap { it.value.sorted() }

        assertThat(values).isNotNull()
        assertThat(values).containsExactly(1, 3, 5, 7, 9, 2, 4, 6, 8, 10)
    }

    @Test
    fun primeNumbers() {
        val primeNumbers = listOf(1, 9, 8, 3, 7, 2, 5, 4, 10, 6, 11)
            .filter(::prime)

        assertThat(primeNumbers).containsExactlyInAnyOrder(2, 3, 5, 7, 11)
    }

    private fun prime(number: Int): Boolean {
        if (number < 2) {
            return false
        }
        return (2 until number).toList().none { number % it == 0 }
    }

    @Test
    fun fibonacciSequenceRecursive() {
        assertThat(fibonacciSequenceRecursive(0)).containsExactly(0)
        assertThat(fibonacciSequenceRecursive(1)).containsExactly(0, 1)
        assertThat(fibonacciSequenceRecursive(2)).containsExactly(0, 1, 1)
        assertThat(fibonacciSequenceRecursive(3)).containsExactly(0, 1, 1, 2)
        assertThat(fibonacciSequenceRecursive(4)).containsExactly(0, 1, 1, 2, 3)
        assertThat(fibonacciSequenceRecursive(5)).containsExactly(0, 1, 1, 2, 3, 5)
        assertThat(fibonacciSequenceRecursive(6)).containsExactly(0, 1, 1, 2, 3, 5, 8)
        assertThat(fibonacciSequenceRecursive(7)).containsExactly(0, 1, 1, 2, 3, 5, 8, 13)
        assertThat(fibonacciSequenceRecursive(8)).containsExactly(0, 1, 1, 2, 3, 5, 8, 13, 21)
        assertThat(fibonacciSequenceRecursive(9)).containsExactly(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
    }

    private fun fibonacciSequenceRecursive(num: Int, a: Int = 0, b: Int = 1): List<Int> {
        return when (num) {
            0 -> listOf(a)
            else -> listOf(a) + fibonacciSequenceRecursive(num - 1, b, a + b)
        }
    }

    @Test
    fun fibonacciNumberRecursive() {
        assertThat(fibonacciNumberRecursive(0)).isEqualTo(0)
        assertThat(fibonacciNumberRecursive(1)).isEqualTo(1)
        assertThat(fibonacciNumberRecursive(2)).isEqualTo(1)
        assertThat(fibonacciNumberRecursive(3)).isEqualTo(2)
        assertThat(fibonacciNumberRecursive(4)).isEqualTo(3)
        assertThat(fibonacciNumberRecursive(5)).isEqualTo(5)
        assertThat(fibonacciNumberRecursive(6)).isEqualTo(8)
        assertThat(fibonacciNumberRecursive(7)).isEqualTo(13)
        assertThat(fibonacciNumberRecursive(8)).isEqualTo(21)
        assertThat(fibonacciNumberRecursive(9)).isEqualTo(34)
    }

    private fun fibonacciNumberRecursive(num: Int, a: Int = 0, b: Int = 1): Int {
        return when (num) {
            0 -> a
            else -> fibonacciNumberRecursive(num - 1, b, a + b)
        }
    }

    @Test
    fun fibonacciSequenceIterative() {
        assertThat(fibonacciSequenceIterative(0)).containsExactly(0)
        assertThat(fibonacciSequenceIterative(1)).containsExactly(0, 1)
        assertThat(fibonacciSequenceIterative(2)).containsExactly(0, 1, 1)
        assertThat(fibonacciSequenceIterative(3)).containsExactly(0, 1, 1, 2)
        assertThat(fibonacciSequenceIterative(4)).containsExactly(0, 1, 1, 2, 3)
        assertThat(fibonacciSequenceIterative(5)).containsExactly(0, 1, 1, 2, 3, 5)
        assertThat(fibonacciSequenceIterative(6)).containsExactly(0, 1, 1, 2, 3, 5, 8)
        assertThat(fibonacciSequenceIterative(7)).containsExactly(0, 1, 1, 2, 3, 5, 8, 13)
        assertThat(fibonacciSequenceIterative(8)).containsExactly(0, 1, 1, 2, 3, 5, 8, 13, 21)
        assertThat(fibonacciSequenceIterative(9)).containsExactly(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
    }

    private fun fibonacciSequenceIterative(num: Int): List<Int> {
        return generateSequence(Pair(0, 1)) { prev -> Pair(prev.second, prev.first + prev.second) }
            .take(num + 1)
            .map { it.first }
            .toList()
    }

    @Test
    fun factorial() {
        assertThat(factorial(0)).isEqualTo(1)
        assertThat(factorial(1)).isEqualTo(1)
        assertThat(factorial(2)).isEqualTo(2)
        assertThat(factorial(3)).isEqualTo(6)
        assertThat(factorial(4)).isEqualTo(24)
        assertThat(factorial(5)).isEqualTo(120)
        assertThat(factorial(6)).isEqualTo(720)
    }

    private fun factorial(num: Int): Int {
        return when (num) {
            0 -> 1
            else -> (1..num).toList().reduce { acc, i -> acc * i }
        }
    }
}