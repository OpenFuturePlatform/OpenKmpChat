package com.mutualmobile.harvestKmp.utils

import kotlinx.datetime.*

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}
fun LocalDate.Companion.now(): LocalDate {
    return LocalDateTime.now().date
}

fun LocalDateTime.plus(value: Long, unit: DateTimeUnit.TimeBased): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    return this.toInstant(timeZone)
        .plus(value, unit)
        .toLocalDateTime(timeZone)
}
fun LocalDateTime.minus(value: Long, unit: DateTimeUnit.TimeBased): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    return this.toInstant(timeZone)
        .minus(value, unit)
        .toLocalDateTime(timeZone)
}