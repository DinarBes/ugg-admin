package com.ugg.admin.kit.stores.domain.states

import com.ugg.admin.kit.stores.domain.entities.Store
import com.ugg.admin.kit.stores.domain.entities.StoreDay

/**
 * The store form. [saved] is what customers see now; text fields are kept as typed and checked
 * on the way to [draft].
 */
data class StoreState(
    val saved: Store? = null,
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val schedule: List<StoreDay> = emptyList(),
    /** «Пт–Вс 11:00–19:00 · Откроется в пт» for the form as it is now. */
    val preview: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isJustSaved: Boolean = false,
    val error: Throwable? = null,
) {
    /** `+79000000001`, or `null` while the phone isn't a Russian number. */
    val parsedPhone: String?
        get() {
            val digits = phone.filter { it.isDigit() }
            if (digits.length != 11 || !(digits.startsWith("7") || digits.startsWith("8"))) return null
            return "+7" + digits.drop(1)
        }

    val parsedLatitude: Double?
        get() = latitude.replace(',', '.').trim().toDoubleOrNull()?.takeIf { it in -90.0..90.0 }

    val parsedLongitude: Double?
        get() = longitude.replace(',', '.').trim().toDoubleOrNull()?.takeIf { it in -180.0..180.0 }

    val nameError: String?
        get() = if (saved != null && name.isBlank()) "Как точка называется в приложении" else null

    val addressError: String?
        get() = if (saved != null && address.isBlank()) "Нужен адрес" else null

    val phoneError: String?
        get() = if (saved != null && parsedPhone == null) "Номер вида +7 900 000-00-00" else null

    val coordinatesError: String?
        get() = if (saved != null && (parsedLatitude == null || parsedLongitude == null)) "Координаты в градусах, например 51.853" else null

    /** The store as it will be saved, or `null` while something is invalid. */
    val draft: Store?
        get() {
            val base = saved ?: return null
            if (name.isBlank() || address.isBlank()) return null
            return base.copy(
                name = name.trim(),
                address = address.trim(),
                phone = parsedPhone ?: return null,
                latitude = parsedLatitude ?: return null,
                longitude = parsedLongitude ?: return null,
                schedule = schedule,
            )
        }

    val hasChanges: Boolean
        get() = saved != null && (draft ?: return true) != saved

    val canSave: Boolean
        get() = hasChanges && draft != null && !isSaving
}
