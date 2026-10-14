package com.ugg.admin.kit.orders.domain.states

import com.ugg.admin.kit.orders.domain.entities.Order

data class OrderDetailState(
    val order: Order? = null,
    /** The deadline being edited; saved with «Сохранить». */
    val readyAt: Long? = null,
    /** The note being edited; saved with «Сохранить». */
    val note: String = "",
    val isLoading: Boolean = false,
    /** A stage change or a cancellation is on its way. */
    val isUpdating: Boolean = false,
    val isSaving: Boolean = false,
    val error: Throwable? = null,
) {
    val hasChanges: Boolean
        get() = order != null && (readyAt != order.readyAt || note != order.note)
}
