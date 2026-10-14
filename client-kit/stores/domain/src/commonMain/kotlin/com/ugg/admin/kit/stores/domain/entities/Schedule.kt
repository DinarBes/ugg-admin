@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.stores.domain.entities

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/*
 * The same rules the client app uses on «Точки», so the preview on the store form shows exactly
 * what customers will see.
 */

/**
 * The week in one line, consecutive days with the same hours joined:
 * «Ежедневно 10:00–20:00», «Пт–Вс 11:00–19:00». A week without working days is «Временно закрыто».
 */
fun List<StoreDay>.summary(): String {
    val working = filter { it.isWorking }
    if (working.isEmpty()) return "Временно закрыто"
    if (working.size == 7 && working.all { it.hours == working.first().hours }) return "Ежедневно ${working.first().hours}"

    val groups = mutableListOf<Triple<DayOfWeek, DayOfWeek, String>>()
    for (day in sortedBy { it.day }) {
        if (!day.isWorking) continue
        val last = groups.lastOrNull()
        if (last != null && last.third == day.hours && last.second.ordinal + 1 == day.day.ordinal) {
            groups[groups.lastIndex] = last.copy(second = day.day)
        } else {
            groups += Triple(day.day, day.day, day.hours)
        }
    }

    return groups.joinToString(", ") { (from, to, hours) ->
        val span = if (from == to) from.short else "${from.short}–${to.short}"
        "$span $hours"
    }
}

/** «Открыто до 20:00», «Откроется в 10:00», «Откроется завтра», «Откроется в пт», «Закрыто». */
fun Store.openingLabel(now: Instant): String {
    val zone = runCatching { TimeZone.of(timezone) }.getOrDefault(TimeZone.of(Store.DEFAULT_TIMEZONE))
    val local = now.toLocalDateTime(zone)
    val byDay = schedule.associateBy { it.day }

    byDay[local.dayOfWeek]?.takeIf { it.isWorking }?.let { today ->
        val open = today.open.toLocalTimeOrNull()
        val close = today.close.toLocalTimeOrNull()
        if (open != null && close != null) {
            if (local.time >= open && local.time < close) return "Открыто до ${today.close}"
            if (local.time < open) return "Откроется в ${today.open}"
        }
    }

    for (daysAhead in 1..7) {
        val day = DayOfWeek.entries[(local.dayOfWeek.ordinal + daysAhead) % 7]
        if (byDay[day]?.isWorking != true) continue
        return if (daysAhead == 1) "Откроется завтра" else "Откроется ${day.inShort}"
    }
    return "Закрыто"
}

/** «11:00» → 11:00; `null` for anything unreadable. */
fun String.toLocalTimeOrNull(): LocalTime? = runCatching { LocalTime.parse(this) }.getOrNull()

val DayOfWeek.short: String
    get() = when (this) {
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
        DayOfWeek.SUNDAY -> "Вс"
    }

/** «Понедельник», for accessibility labels. */
val DayOfWeek.full: String
    get() = when (this) {
        DayOfWeek.MONDAY -> "Понедельник"
        DayOfWeek.TUESDAY -> "Вторник"
        DayOfWeek.WEDNESDAY -> "Среда"
        DayOfWeek.THURSDAY -> "Четверг"
        DayOfWeek.FRIDAY -> "Пятница"
        DayOfWeek.SATURDAY -> "Суббота"
        DayOfWeek.SUNDAY -> "Воскресенье"
    }

private val DayOfWeek.inShort: String
    get() = if (this == DayOfWeek.TUESDAY) "во вт" else "в ${short.lowercase()}"
