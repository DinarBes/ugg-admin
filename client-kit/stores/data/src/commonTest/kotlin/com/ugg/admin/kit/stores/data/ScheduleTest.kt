@file:OptIn(ExperimentalTime::class)

package com.ugg.admin.kit.stores.data

import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.entities.StoreDay
import com.ugg.admin.kit.stores.domain.entities.openingLabel
import com.ugg.admin.kit.stores.domain.entities.summary
import com.ugg.admin.kit.stores.domain.states.StoreState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek

class ScheduleTest {
    private val listvyanka = Store.mocks.first { it.id == "listvyanka" }
    private val irkutsk = Store.mocks.first { it.id == "irkutsk" }

    // Wednesday 14 October 2026, 12:00 in Irkutsk (UTC+8).
    private val wednesdayNoon = Instant.parse("2026-10-14T04:00:00Z")

    @Test
    fun summaries() {
        assertEquals("Пт–Вс 11:00–19:00", listvyanka.schedule.summary())
        assertEquals("Ежедневно 10:00–20:00", irkutsk.schedule.summary())
        assertEquals("Временно закрыто", StoreDay.week().summary())
    }

    @Test
    fun differentHoursSplitTheWeek() {
        val schedule = StoreDay.week {
            when (it) {
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> StoreDay(it, true, "11:00", "18:00")
                else -> StoreDay(it, true, "10:00", "20:00")
            }
        }
        assertEquals("Пн–Пт 10:00–20:00, Сб–Вс 11:00–18:00", schedule.summary())
    }

    @Test
    fun openingLabels() {
        assertEquals("Открыто до 20:00", irkutsk.openingLabel(wednesdayNoon))
        assertEquals("Откроется в пт", listvyanka.openingLabel(wednesdayNoon))
    }

    @Test
    fun phoneIsNormalised() {
        val state = StoreState(saved = listvyanka, phone = "8 (900) 000-00-01")
        assertEquals("+79000000001", state.parsedPhone)
        assertNull(state.copy(phone = "12345").parsedPhone)
    }
}
