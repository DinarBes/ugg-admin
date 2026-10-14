package com.ugg.admin.kit.content.domain.states

import com.ugg.admin.kit.content.domain.entities.ContentSection

/** The «Контент» tab. */
data class ContentState(
    /** «изменено сегодня», «изменено 12 октября»; missing — never saved from here. */
    val edited: Map<ContentSection, String> = emptyMap(),
    val error: Throwable? = null,
)
