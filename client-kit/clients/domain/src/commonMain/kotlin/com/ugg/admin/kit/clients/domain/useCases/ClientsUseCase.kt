package com.ugg.admin.kit.clients.domain.useCases

import com.ugg.admin.kit.clients.domain.entities.Client
import kotlinx.coroutines.flow.Flow

interface ClientsUseCase {
    /** Everyone, live: a new sign-in or a new order shows up without a reload. */
    fun observeClients(): Flow<List<Client>>
}
