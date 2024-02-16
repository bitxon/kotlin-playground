package bitxon.test

import bitxon.model.Department
import bitxon.model.Employee
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class StreamBusinessTest {

    companion object {
        const val SENIOR = "Senior"
        const val MIDDLE = "Middle"
        const val JUNIOR = "Junior"

        val EMPLOYEES_1 = listOf(
            Employee(title = SENIOR, salary = 10000),
            Employee(title = MIDDLE, salary = 7400),
            Employee(title = MIDDLE, salary = 7400),
            Employee(title = JUNIOR, salary = 4000),
            Employee(title = JUNIOR, salary = 3900),
            Employee(title = SENIOR, salary = 9500),
            Employee(title = SENIOR, salary = 9500),
            Employee(title = SENIOR, salary = 8300)
        )

        val EMPLOYEES_2_NO_JUNIORS = listOf(
            Employee(title = MIDDLE, salary = 7400),
            Employee(title = SENIOR, salary = 11100),
            Employee(title = SENIOR, salary = 8900),
            Employee(title = SENIOR, salary = 8900)
        )

        val EMPLOYEES_2_NO_MIDDLES = listOf(
            Employee(title = SENIOR, salary = 10000),
            Employee(title = JUNIOR, salary = 4000),
            Employee(title = JUNIOR, salary = 3900),
            Employee(title = SENIOR, salary = 9500),
            Employee(title = SENIOR, salary = 8300)
        )

        const val FINANCE = "Finance"
        const val IT = "IT"
        const val HR = "H&R"

        val DEPARTMENTS = listOf(
            Department(name = FINANCE, employees = EMPLOYEES_1),
            Department(name = IT, employees = EMPLOYEES_2_NO_MIDDLES),
            Department(name = HR, employees = EMPLOYEES_2_NO_JUNIORS)
        )
    }


    @Test
    fun averageSalaryByTitle() {
        val statistic = DEPARTMENTS.asSequence()
            .flatMap { it.employees }
            .groupBy { it.title }
            .mapValues { (title, employees) -> employees.map { it.salary }.average() }

        // Validate
        assertThat(statistic).isNotNull().containsExactlyInAnyOrderEntriesOf(
            mapOf(
                SENIOR to 9400.0,
                MIDDLE to 7400.0,
                JUNIOR to 3950.0
            )
        )
    }

    @Test
    fun minSalaryByTitle() {
        val statistic = DEPARTMENTS.asSequence()
            .flatMap { it.employees }
            .groupBy { it.title }
            .mapValues { (title, employees) -> employees.map { it.salary }.min() }


        // Validate
        assertThat(statistic).isNotNull().containsExactlyInAnyOrderEntriesOf(
            mapOf(
                SENIOR to 8300,
                MIDDLE to 7400,
                JUNIOR to 3900
            )
        )
    }

    @Test
    fun averageSalaryByDepartment() {
        val statistic = DEPARTMENTS.asSequence()
            .groupBy { it.name }
            .mapValues { (name, departments) -> departments.flatMap { it.employees }.map { it.salary }.average() }


        // Validate
        assertThat(statistic).isNotNull().containsExactlyInAnyOrderEntriesOf(
            mapOf(
                FINANCE to 7500.0,
                IT to 7140.0,
                HR to 9075.0
            )
        )
    }

    @Test
    fun sumSalaries() {
        val reduceResult = DEPARTMENTS.asSequence()
            .flatMap { it.employees }
            .map { it.salary }
            .reduce(Int::plus)
        assertThat(reduceResult).describedAs("Reduce check").isEqualTo(132000)


        val intSequenceSumResult = DEPARTMENTS.asSequence()
            .flatMap { it.employees }
            .map { it.salary }
            .sum()
        assertThat(intSequenceSumResult).describedAs("Sequence<Int>.sum() check").isEqualTo(132000)


        val intListSumOfResult = DEPARTMENTS
            .flatMap { it.employees }
            .sumOf { it.salary }
        assertThat(intListSumOfResult).describedAs("List<Int>.sumOf() check").isEqualTo(132000)

    }


    @Test
    fun allDepartmentsHasAtLeastOneJunior() {
        val departmentsWithJuniors = listOf(
            Department(name = IT, employees = EMPLOYEES_1),
            Department(name = HR, employees = EMPLOYEES_2_NO_MIDDLES)
        )
        val departmentsWithoutJuniors = listOf(
            Department(name = IT, employees = EMPLOYEES_1),
            Department(name = HR, employees = EMPLOYEES_2_NO_JUNIORS)
        )

        val result1 = departmentsWithJuniors
            .all { dep -> dep.employees.any { JUNIOR == it.title } }
        assertThat(result1).isTrue()

        val result2 = departmentsWithoutJuniors
            .all { dep -> dep.employees.any { JUNIOR == it.title } }
        assertThat(result2).isFalse()
    }

    @Test
    fun mutateToMonsterCompanyHahahahaha() {
        val result: Employee = DEPARTMENTS.asSequence()
            .flatMap { it.employees }
            .reduce { result, next ->
                Employee(title = result.title + next.title, salary = result.salary + next.salary)
            }

        assertThat(result).isNotNull()
        assertThat(result.title).isEqualTo("SeniorMiddleMiddleJuniorJuniorSeniorSeniorSeniorSeniorJuniorJuniorSeniorSeniorMiddleSeniorSeniorSenior")
        assertThat(result.salary).isEqualTo(132000)
    }


    @Test
    fun findTwoWithHighestSalary() {
        val highlyPaidEmployees = DEPARTMENTS.asSequence()
            .flatMap { it.employees }
            .sortedByDescending { it.salary }
            .take(2)
            .toList()

        assertThat(highlyPaidEmployees).containsExactly(
            Employee(title = SENIOR, salary = 11100),
            Employee(title = SENIOR, salary = 10000)
        )
    }
}