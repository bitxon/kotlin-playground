package bitxon.test

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat


class MultiThreadTest {

    @Test
    fun waitForOneCoroutines(): Unit = runBlocking {
        // given
        val tasks = listOf(
            async { sleepAndGet(3000, "A") },
            async { sleepAndGet(1000, "B") },
            async { sleepAndGet(2000, "C") },
        )

        // when
        val result = tasks.awaitAny() // see custom implementation of awaitAny in the bottom of the file

        // then
        assertThat(result).isEqualTo("B")
    }

    @Test
    fun waitForFewCoroutines(): Unit = runBlocking {

        // given
        val tasks = listOf(
            async { sleepAndGet(3000, "A") },
            async { sleepAndGet(1000, "B") },
            async { sleepAndGet(2000, "C") },
            async { sleepAndGet(99000, "D") }, // supper long task
        )

        // when
        val result = tasks.awaitAtLeast(2) // see custom implementation of awaitAtLeast in the bottom of the file

        // then
        assertThat(result).containsExactly("B", "C")
    }

    @Test
    fun waitForAllCoroutines(): Unit = runBlocking {

        // given
        val tasks = listOf(
            async { sleepAndGet(3000, "A") },
            async { sleepAndGet(1000, "B") },
            async { sleepAndGet(2000, "C") },
        )

        // when
        val result = tasks.awaitAll()

        // then
        assertThat(result).containsExactly("A", "B", "C")
    }


    private suspend fun sleepAndGet(millis: Long, value: String): String {
        delay(millis)
        return value
    }

    private suspend fun <T> Collection<Deferred<T>>.awaitAny(): T {
        val tasks = this.toList() // copy to local variable to avoid miss-using of `this` in the select block

        return select {
            tasks.forEach { task -> task.onAwait { it } }
        }.also {
            tasks.filter { task -> task.isActive }
                .forEach { task -> task.cancelAndJoin() } // Cancel remaining tasks
        }
    }

    private suspend fun <T> Collection<Deferred<T>>.awaitAtLeast(count: Int): List<T> {
        val tasks = this.toMutableList() // copy to local variable to avoid miss-using of `this` in the select block
        val completedTasks = mutableListOf<T>()

        repeat(count) {
            val completedTask = select {
                tasks.forEach { task ->
                    task.onAwait { result ->
                        tasks.remove(task) // Remove completed task
                        result
                    }
                }
            }
            completedTasks.add(completedTask)
        }

        tasks.filter { it.isActive }
            .forEach { it.cancelAndJoin() } // Cancel remaining tasks

        return completedTasks
    }

}