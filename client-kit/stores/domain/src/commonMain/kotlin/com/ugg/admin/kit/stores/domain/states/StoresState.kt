package com.ugg.admin.kit.stores.domain.states

import com.ugg.admin.kit.stores.domain.entities.Store

data class StoresState(
    val stores: List<Store> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
