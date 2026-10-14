package com.ugg.admin.kit.content.data.useCases

import com.ugg.admin.infrastructure.firebase.firestore.services.ContentService
import com.ugg.admin.kit.content.data.mappers.toDomain
import com.ugg.admin.kit.content.data.mappers.toDto
import com.ugg.admin.kit.content.domain.entities.CareContent
import com.ugg.admin.kit.content.domain.entities.CheckoutContent
import com.ugg.admin.kit.content.domain.entities.ContentDraft
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.domain.entities.HomeContent
import com.ugg.admin.kit.content.domain.entities.OnboardingContent
import com.ugg.admin.kit.content.domain.entities.StoryContent
import com.ugg.admin.kit.content.domain.useCases.ContentUseCase
import com.ugg.admin.kit.core.data.content.translatingContentErrors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** `content/{home, story, care, onboarding, checkout}` in Firestore — what the client app's screens read. */
class SharedContentUseCase(
    private val content: ContentService,
) : ContentUseCase {

    override fun observeUpdatedAt(): Flow<Map<ContentSection, Long>> =
        content.observeUpdatedAt()
            .map { byId -> ContentSection.entries.mapNotNull { section -> byId[section.id]?.let { section to it } }.toMap() }
            .translatingContentErrors()

    override suspend fun fetch(section: ContentSection): ContentDraft = translatingContentErrors {
        when (section) {
            ContentSection.Home -> content.home().toDomain()
            ContentSection.Story -> content.story().toDomain()
            ContentSection.Care -> content.care().toDomain()
            ContentSection.Onboarding -> content.onboarding().toDomain()
            ContentSection.Checkout -> content.checkout().toDomain()
        }
    }

    override suspend fun save(draft: ContentDraft) = translatingContentErrors {
        when (draft) {
            is HomeContent -> content.saveHome(draft.toDto())
            is StoryContent -> content.saveStory(draft.toDto())
            is CareContent -> content.saveCare(draft.toDto())
            is OnboardingContent -> content.saveOnboarding(draft.toDto())
            is CheckoutContent -> content.saveCheckout(draft.toDto())
        }
    }
}
