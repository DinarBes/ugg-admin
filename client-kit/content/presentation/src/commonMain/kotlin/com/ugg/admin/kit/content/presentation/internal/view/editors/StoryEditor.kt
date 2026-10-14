package com.ugg.admin.kit.content.presentation.internal.view.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.StoryContent
import com.ugg.admin.kit.content.domain.entities.StoryHighlight
import com.ugg.admin.kit.content.presentation.internal.view.EditorCard
import com.ugg.admin.kit.content.presentation.internal.view.TextInput

/** «О мастерской»: the story, «Путь одной пары» and the «Почему Байкал» block. */
@Composable
internal fun StoryEditor(content: StoryContent, onChange: (StoryContent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EditorCard(title = "Начало", hint = "Перенос строки в заголовке — Enter") {
            TextInput("Надзаголовок", content.kicker, { onChange(content.copy(kicker = it)) })
            TextInput("Заголовок", content.title, { onChange(content.copy(title = it)) }, multiline = true)
            TextInput("Вступление", content.intro, { onChange(content.copy(intro = it)) }, multiline = true)
        }

        EditorCard(title = "Шаги") {
            TextInput("Заголовок блока", content.stepsTitle, { onChange(content.copy(stepsTitle = it)) }, placeholder = "Путь одной пары")
            StepsEditor(steps = content.steps, onChange = { onChange(content.copy(steps = it)) })
        }

        EditorCard(
            title = "Выделенный блок",
            isShown = content.highlight != null,
            onShownChange = { onChange(content.copy(highlight = if (it) content.highlight ?: StoryHighlight() else null)) },
        ) {
            val highlight = content.highlight ?: StoryHighlight()
            TextInput("Надзаголовок", highlight.kicker, { onChange(content.copy(highlight = highlight.copy(kicker = it))) }, placeholder = "Почему Байкал")
            TextInput("Заголовок", highlight.title, { onChange(content.copy(highlight = highlight.copy(title = it))) }, multiline = true)
            TextInput("Текст", highlight.body, { onChange(content.copy(highlight = highlight.copy(body = it))) }, multiline = true)
        }
    }
}
