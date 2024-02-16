package bitxon.test

import org.assertj.core.api.Assertions.*
import kotlin.test.Test

class StreamStringTest {
    companion object {
        const val TEXT = "Hello my        name is      Nikita, i like listen to music. My favorite - rock"
    }

    @Test
    fun mostFrequentWord() {
        val word = TEXT.splitToSequence(" ")
            .filter { it.isNotEmpty() }
            .map { it.lowercase() }
            .groupBy { it }
            .maxBy { it.value.size }
            .key

        assertThat(word).isEqualTo("my")
    }

    @Test
    fun longestWord() {
        val word = TEXT.splitToSequence(" ")
            .maxBy { it.length }

        assertThat(word).isEqualTo("favorite")
    }

    @Test
    fun reverseString() {
        val str = TEXT.split(" ")
            .reversed()
            .joinToString(separator = " ")

        assertThat(str)
            .isEqualTo("rock - favorite My music. to listen like i Nikita,      is name        my Hello")
    }

}