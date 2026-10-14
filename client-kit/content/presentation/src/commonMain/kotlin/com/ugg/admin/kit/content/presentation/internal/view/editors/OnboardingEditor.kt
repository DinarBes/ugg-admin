package com.ugg.admin.kit.content.presentation.internal.view.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.OnboardingContent
import com.ugg.admin.kit.content.domain.entities.OnboardingSlide
import com.ugg.admin.kit.content.presentation.internal.view.EditorCard
import com.ugg.admin.kit.content.presentation.internal.view.ItemsEditor
import com.ugg.admin.kit.content.presentation.internal.view.TextInput
import com.ugg.admin.kit.content.presentation.internal.view.newId

/** «Онбординг»: the slides shown on the first launch, in order. */
@Composable
internal fun OnboardingEditor(content: OnboardingContent, onChange: (OnboardingContent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EditorCard(title = "Слайды", hint = "Показываются при первом запуске. Перенос строки в заголовке — Enter") {
            ItemsEditor(
                items = content.slides,
                onChange = { onChange(content.copy(slides = it)) },
                itemLabel = { index, slide -> "${index + 1}. ${slide.overline.ifBlank { "Слайд" }}" },
                addLabel = "Добавить слайд",
                newItem = { slides -> OnboardingSlide(id = newId("slide", slides.map { it.id })) },
            ) { slide, onSlideChange ->
                TextInput("Надзаголовок", slide.overline, { onSlideChange(slide.copy(overline = it)) })
                TextInput("Заголовок", slide.title, { onSlideChange(slide.copy(title = it)) }, multiline = true)
                TextInput("Текст", slide.description, { onSlideChange(slide.copy(description = it)) }, multiline = true)
            }
        }
    }
}
