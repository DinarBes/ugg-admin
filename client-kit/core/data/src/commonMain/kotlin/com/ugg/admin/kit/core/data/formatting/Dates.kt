@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.core.data.formatting

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/** Epoch milliseconds → «18 октября», in the workshop's time zone. */
fun Long.toDayAndMonth(timezone: String = "Asia/Irkutsk"): String {
    val date = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.of(timezone)).date
    return "${date.day} ${date.month.genitive()}"
}

/** Epoch milliseconds → «сегодня», «вчера» or «12 сентября», relative to [now]. */
fun Long.toDayLabel(now: Long, timezone: String = "Asia/Irkutsk"): String {
    val zone = TimeZone.of(timezone)
    val date = Instant.fromEpochMilliseconds(this).toLocalDateTime(zone).date
    val today = Instant.fromEpochMilliseconds(now).toLocalDateTime(zone).date
    return when (date) {
        today -> "сегодня"
        today.minus(DatePeriod(days = 1)) -> "вчера"
        else -> "${date.day} ${date.month.genitive()}"
    }
}

/** Epoch milliseconds → «сегодня, 11:40», «вчера, 21:05», «10 октября, 15:20». */
fun Long.toMomentLabel(now: Long, timezone: String = "Asia/Irkutsk"): String {
    val time = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.of(timezone)).time
    val clock = "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
    return "${toDayLabel(now, timezone)}, $clock"
}

private fun Month.genitive() = when (this) {
    Month.JANUARY -> "января"
    Month.FEBRUARY -> "февраля"
    Month.MARCH -> "марта"
    Month.APRIL -> "апреля"
    Month.MAY -> "мая"
    Month.JUNE -> "июня"
    Month.JULY -> "июля"
    Month.AUGUST -> "августа"
    Month.SEPTEMBER -> "сентября"
    Month.OCTOBER -> "октября"
    Month.NOVEMBER -> "ноября"
    Month.DECEMBER -> "декабря"
}
