package com.ugg.admin.kit.clients.data.useCases

import com.ugg.admin.kit.clients.domain.entities.Client
import com.ugg.admin.kit.clients.domain.useCases.ClientsUseCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockClientsUseCase(
    private val seed: List<Client> = Client.mocks,
    private val latency: Duration = 300.milliseconds,
) : ClientsUseCase {
    override fun observeClients(): Flow<List<Client>> = flow {
        delay(latency)
        emit(seed)
    }
}
