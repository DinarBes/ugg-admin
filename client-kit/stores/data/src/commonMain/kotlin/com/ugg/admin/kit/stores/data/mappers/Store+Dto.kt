package com.ugg.admin.kit.stores.data.mappers

import com.ugg.admin.infrastructure.firebase.firestore.dto.GeoPointDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.StoreDto
import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.entities.StoreDay
import kotlinx.datetime.DayOfWeek

internal fun StoreDto.toDomain() = Store(
    id = id,
    order = order,
    city = city,
    name = name,
    address = address,
    phone = phone,
    latitude = location.latitude,
    longitude = location.longitude,
    timezone = timezone.ifBlank { Store.DEFAULT_TIMEZONE },
    schedule = StoreDay.week { day ->
        val hours = schedule.hoursOn(day)
        if (hours != null) StoreDay(day, isWorking = true, open = hours.open, close = hours.close) else StoreDay(day)
    },
)

/** The edited store over the [dto] as read, so services and the rest stay as they were. */
internal fun Store.applyTo(dto: StoreDto): StoreDto {
    fun hours(day: DayOfWeek) = schedule.firstOrNull { it.day == day }
        ?.takeIf { it.isWorking }
        ?.let { StoreDto.HoursDto(open = it.open, close = it.close) }

    return dto.copy(
        name = name,
        address = address,
        phone = phone,
        location = GeoPointDto(latitude = latitude, longitude = longitude),
        schedule = StoreDto.ScheduleDto(
            mon = hours(DayOfWeek.MONDAY),
            tue = hours(DayOfWeek.TUESDAY),
            wed = hours(DayOfWeek.WEDNESDAY),
            thu = hours(DayOfWeek.THURSDAY),
            fri = hours(DayOfWeek.FRIDAY),
            sat = hours(DayOfWeek.SATURDAY),
            sun = hours(DayOfWeek.SUNDAY),
        ),
    )
}

private fun StoreDto.ScheduleDto.hoursOn(day: DayOfWeek): StoreDto.HoursDto? = when (day) {
    DayOfWeek.MONDAY -> mon
    DayOfWeek.TUESDAY -> tue
    DayOfWeek.WEDNESDAY -> wed
    DayOfWeek.THURSDAY -> thu
    DayOfWeek.FRIDAY -> fri
    DayOfWeek.SATURDAY -> sat
    DayOfWeek.SUNDAY -> sun
}
