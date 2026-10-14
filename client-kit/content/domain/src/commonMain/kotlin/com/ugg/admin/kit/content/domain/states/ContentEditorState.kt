package com.ugg.admin.kit.content.domain.states

import com.ugg.admin.kit.content.domain.entities.ContentDraft
import com.ugg.admin.kit.content.domain.entities.ContentSection

data class ContentEditorState(
    val section: ContentSection? = null,
    /** What the client app shows now. */
    val saved: ContentDraft? = null,
    /** The edits. */
    val draft: ContentDraft? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isJustSaved: Boolean = false,
    val error: Throwable? = null,
) {
    val hasChanges: Boolean
        get() = draft != null && draft != saved

    val canSave: Boolean
        get() = hasChanges && draft?.problem == null && !isSaving
}
