package com.ugg.admin.kit.clients.domain.states

import com.ugg.admin.kit.clients.domain.entities.Client

data class ClientsState(
    val clients: List<Client> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null,
) {
    val activeCount: Int
        get() = clients.count { it.activeOrder != null }

    /**
     * People with an order in work first, newest order on top; then everyone else as they came.
     * The search looks at the name and the phone digits.
     */
    val visibleClients: List<Client>
        get() {
            val needle = query.trim()
            val digits = needle.filter { it.isDigit() }
            val found = if (needle.isEmpty()) clients else clients.filter { client ->
                client.name?.contains(needle, ignoreCase = true) == true ||
                    (digits.length >= 3 && client.phoneDial.contains(digits))
            }
            return found.sortedWith(
                compareByDescending<Client> { it.activeOrder != null }.thenByDescending { it.activeOrder?.number ?: 0 },
            )
        }
}
