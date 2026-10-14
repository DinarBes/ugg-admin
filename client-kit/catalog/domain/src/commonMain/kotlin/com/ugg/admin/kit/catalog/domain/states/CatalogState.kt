package com.ugg.admin.kit.catalog.domain.states

import com.ugg.admin.kit.catalog.domain.entities.Catalog
import com.ugg.admin.kit.catalog.domain.entities.CatalogShade
import com.ugg.admin.kit.catalog.domain.entities.CatalogSize
import com.ugg.admin.kit.catalog.domain.entities.LeadTime

/**
 * The catalog form. [saved] is what the client app sees now; the other fields are the edits,
 * kept as typed so a half-written price doesn't jump around.
 */
data class CatalogState(
    val saved: Catalog? = null,
    val price: String = "",
    val leadTime: String = "",
    val shades: List<CatalogShade> = emptyList(),
    val sizes: List<CatalogSize> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSaving: Boolean = false,
    /** «Сохранено» on the button until the next edit. */
    val isJustSaved: Boolean = false,
    val error: Throwable? = null,
) {
    val parsedPrice: Int?
        get() = price.filter { it.isDigit() }.takeIf { it.isNotEmpty() && it.length <= 7 }?.toInt()?.takeIf { it > 0 }

    val parsedLeadTime: LeadTime?
        get() = LeadTime.parse(leadTime)

    val priceError: String?
        get() = if (saved != null && parsedPrice == null) "Укажите цену в рублях" else null

    val leadTimeError: String?
        get() = if (saved != null && parsedLeadTime == null) "Например, 5–7" else null

    /** The catalog as it will be saved, or `null` while a field is invalid. */
    val draft: Catalog?
        get() {
            val base = saved ?: return null
            val price = parsedPrice ?: return null
            val leadTime = parsedLeadTime ?: return null
            return base.copy(price = price, leadTime = leadTime, shades = shades, sizes = sizes)
        }

    val hasChanges: Boolean
        get() = saved != null && (draft ?: return true) != saved

    val canSave: Boolean
        get() = hasChanges && draft != null && !isSaving
}
