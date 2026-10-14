package com.ugg.admin.infrastructure.firebase.firestore.services

import com.ugg.admin.infrastructure.firebase.firestore.dto.UserDto
import com.ugg.admin.infrastructure.firebase.firestore.internal.DocumentStore
import com.ugg.admin.infrastructure.firebase.firestore.internal.RawQuery
import com.ugg.admin.infrastructure.firebase.firestore.internal.decode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** `users/{uid}` of every customer, read-only. Needs `isAdmin: true` in the signed-in user's profile. */
interface UserService {
    /**
     * Everyone, newest first. Not ordered by Firestore: a query ordered by `createdAt` would
     * skip documents written before the field existed.
     */
    fun observeUsers(): Flow<List<UserDto>>

    fun observeUser(uid: String): Flow<UserDto?>

    /** One read, from the server when online — for the admin check at sign-in. */
    suspend fun getUser(uid: String): UserDto?
}

internal class FirestoreUserService(
    private val store: DocumentStore,
) : UserService {

    override fun observeUsers(): Flow<List<UserDto>> =
        store.observe(RawQuery(collection = USERS))
            .map { documents -> documents.map { it.decode(UserDto.serializer()) }.sortedByDescending { it.createdAt ?: 0 } }

    override fun observeUser(uid: String): Flow<UserDto?> =
        store.observe("$USERS/$uid").map { it?.decode(UserDto.serializer()) }

    override suspend fun getUser(uid: String): UserDto? = store.get("$USERS/$uid")?.decode(UserDto.serializer())

    private companion object {
        const val USERS = "users"
    }
}
