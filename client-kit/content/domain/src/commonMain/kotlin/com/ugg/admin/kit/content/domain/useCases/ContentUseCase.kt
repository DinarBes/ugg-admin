package com.ugg.admin.kit.content.domain.useCases

import com.ugg.admin.kit.content.domain.entities.ContentDraft
import com.ugg.admin.kit.content.domain.entities.ContentSection
import kotlinx.coroutines.flow.Flow

interface ContentUseCase {
    /** When each section was last saved, epoch milliseconds; missing — never from the admin app. Live. */
    fun observeUpdatedAt(): Flow<Map<ContentSection, Long>>

    suspend fun fetch(section: ContentSection): ContentDraft

    /** The client app shows it the next time the screen opens. */
    suspend fun save(draft: ContentDraft)
}
