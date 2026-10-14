package com.ugg.admin.kit.stores.domain.entities

import kotlinx.datetime.DayOfWeek

/** `stores/{id}` as the workshop edits it. Hours are local time of [timezone]. */
data class Store(
    val id: String = "",
    val order: Int = 0,
    val city: String = "",
    val name: String = "",
    val address: String = "",
    /** E.164: `+79000000001`. */
    val phone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    /** IANA id: `Asia/Irkutsk`. */
    val timezone: String = DEFAULT_TIMEZONE,
    /** Always seven days, Monday first. */
    val schedule: List<StoreDay> = StoreDay.week(),
) {
    companion object {
        const val DEFAULT_TIMEZONE = "Asia/Irkutsk"

        val mocks: List<Store>
            get() = listOf(
                Store(
                    id = "irkutsk", order = 0, city = "Иркутск", name = "Мастерская и магазин",
                    address = "ул. Примерная, 12 · 2 этаж", phone = "+79000000000",
                    latitude = 52.287, longitude = 104.281,
                    schedule = StoreDay.week { StoreDay(it, isWorking = true, open = "10:00", close = "20:00") },
                ),
                Store(
                    id = "listvyanka", order = 1, city = "Листвянка", name = "Шоурум у набережной",
                    address = "ул. Береговая, 3", phone = "+79000000001",
                    latitude = 51.853, longitude = 104.87,
                    schedule = StoreDay.week {
                        StoreDay(it, isWorking = it >= DayOfWeek.FRIDAY, open = "11:00", close = "19:00")
                    },
                ),
            )

        val mock get() = mocks.last()
    }
}

/**
 * One weekday. A day off keeps its last hours, so switching it back on restores them;
 * Firestore stores a day off as `null`.
 */
data class StoreDay(
    val day: DayOfWeek,
    val isWorking: Boolean = false,
    /** «11:00». */
    val open: String = "10:00",
    /** «19:00». */
    val close: String = "20:00",
) {
    val hours: String
        get() = "$open–$close"

    companion object {
        fun week(day: (DayOfWeek) -> StoreDay = { StoreDay(it) }): List<StoreDay> = DayOfWeek.entries.map(day)
    }
}
