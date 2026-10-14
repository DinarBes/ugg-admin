package com.ugg.admin.infrastructure.firebase.firestore.services

import com.ugg.admin.infrastructure.firebase.firestore.FirestoreException
import com.ugg.admin.infrastructure.firebase.firestore.dto.CareContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.CheckoutContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.HomeContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OnboardingContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.StoryContentDto
import com.ugg.admin.infrastructure.firebase.firestore.internal.DocumentStore
import com.ugg.admin.infrastructure.firebase.firestore.internal.FirestoreJson
import com.ugg.admin.infrastructure.firebase.firestore.internal.RawQuery
import com.ugg.admin.infrastructure.firebase.firestore.internal.ServerTimestamp
import com.ugg.admin.infrastructure.firebase.firestore.internal.decode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer

/**
 * `content/{id}`: the client app's screen texts. Saving replaces the whole document — the DTOs
 * describe every field the client app reads — and stamps `updatedAt` for the «изменено …» labels.
 * Writes need `isAdmin: true` in the signed-in user's profile.
 */
interface ContentService {
    /** Document id → `updatedAt` (epoch milliseconds) for every content document that has one. */
    fun observeUpdatedAt(): Flow<Map<String, Long>>

    suspend fun home(): HomeContentDto
    suspend fun story(): StoryContentDto
    suspend fun care(): CareContentDto
    suspend fun onboarding(): OnboardingContentDto
    suspend fun checkout(): CheckoutContentDto

    suspend fun saveHome(content: HomeContentDto)
    suspend fun saveStory(content: StoryContentDto)
    suspend fun saveCare(content: CareContentDto)
    suspend fun saveOnboarding(content: OnboardingContentDto)
    suspend fun saveCheckout(content: CheckoutContentDto)
}

internal class FirestoreContentService(
    private val store: DocumentStore,
) : ContentService {

    override fun observeUpdatedAt(): Flow<Map<String, Long>> =
        store.observe(RawQuery(collection = CONTENT)).map { documents ->
            documents.mapNotNull { document -> (document.data[UPDATED_AT] as? Long)?.let { document.id to it } }.toMap()
        }

    override suspend fun home() = read("home", HomeContentDto.serializer())
    override suspend fun story() = read("story", StoryContentDto.serializer())
    override suspend fun care() = read("care", CareContentDto.serializer())
    override suspend fun onboarding() = read("onboarding", OnboardingContentDto.serializer())
    override suspend fun checkout() = read("checkout", CheckoutContentDto.serializer())

    override suspend fun saveHome(content: HomeContentDto) = write("home", HomeContentDto.serializer(), content)
    override suspend fun saveStory(content: StoryContentDto) = write("story", StoryContentDto.serializer(), content)
    override suspend fun saveCare(content: CareContentDto) = write("care", CareContentDto.serializer(), content)
    override suspend fun saveOnboarding(content: OnboardingContentDto) = write("onboarding", OnboardingContentDto.serializer(), content)
    override suspend fun saveCheckout(content: CheckoutContentDto) = write("checkout", CheckoutContentDto.serializer(), content)

    private suspend fun <T> read(id: String, serializer: KSerializer<T>): T =
        (store.get("$CONTENT/$id") ?: throw FirestoreException(FirestoreException.Reason.NotFound)).decode(serializer)

    private suspend fun <T> write(id: String, serializer: KSerializer<T>, value: T) =
        store.set("$CONTENT/$id", FirestoreJson.encode(serializer, value) + (UPDATED_AT to ServerTimestamp))

    private companion object {
        const val CONTENT = "content"
        const val UPDATED_AT = "updatedAt"
    }
}
