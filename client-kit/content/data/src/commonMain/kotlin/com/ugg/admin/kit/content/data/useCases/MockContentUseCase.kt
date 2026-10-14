package com.ugg.admin.kit.content.data.useCases

import com.ugg.admin.kit.content.domain.entities.CareContent
import com.ugg.admin.kit.content.domain.entities.CheckoutContent
import com.ugg.admin.kit.content.domain.entities.ContentDraft
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.domain.entities.HomeContent
import com.ugg.admin.kit.content.domain.entities.OnboardingContent
import com.ugg.admin.kit.content.domain.entities.StoryContent
import com.ugg.admin.kit.content.domain.useCases.ContentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** The texts kept in memory until a restart. Bind one instance for the whole app. */
class MockContentUseCase(
    private val latency: Duration = 300.milliseconds,
    private val clock: Clock = Clock.System,
) : ContentUseCase {
    private val drafts = mutableMapOf<ContentSection, ContentDraft>(
        ContentSection.Home to HomeContent.mock,
        ContentSection.Story to StoryContent.mock,
        ContentSection.Care to CareContent.mock,
        ContentSection.Onboarding to OnboardingContent.mock,
        ContentSection.Checkout to CheckoutContent.mock,
    )
    private val updatedAt = MutableStateFlow<Map<ContentSection, Long>>(emptyMap())

    override fun observeUpdatedAt(): Flow<Map<ContentSection, Long>> = updatedAt

    override suspend fun fetch(section: ContentSection): ContentDraft {
        delay(latency)
        return drafts.getValue(section)
    }

    override suspend fun save(draft: ContentDraft) {
        delay(latency)
        drafts[draft.section] = draft
        updatedAt.update { it + (draft.section to clock.now().toEpochMilliseconds()) }
    }
}
