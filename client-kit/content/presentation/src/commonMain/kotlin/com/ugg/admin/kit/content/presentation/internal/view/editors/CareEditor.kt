package com.ugg.admin.kit.content.presentation.internal.view.editors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.CareContact
import com.ugg.admin.kit.content.domain.entities.CareContent
import com.ugg.admin.kit.content.domain.entities.CareTip
import com.ugg.admin.kit.content.presentation.internal.view.EditorCard
import com.ugg.admin.kit.content.presentation.internal.view.ItemsEditor
import com.ugg.admin.kit.content.presentation.internal.view.NumberInput
import com.ugg.admin.kit.content.presentation.internal.view.TextInput
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/** «Уход за уггами»: the intro, the tips, «Чего не делать» and the card asking to write the master. */
@Composable
internal fun CareEditor(content: CareContent, onChange: (CareContent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EditorCard(title = "Начало", hint = "Перенос строки в заголовке — Enter") {
            TextInput("Надзаголовок", content.kicker, { onChange(content.copy(kicker = it)) })
            TextInput("Заголовок", content.title, { onChange(content.copy(title = it)) }, multiline = true)
            TextInput("Вступление", content.intro, { onChange(content.copy(intro = it)) }, multiline = true)
            NumberInput("Минут чтения", content.readMinutes, { onChange(content.copy(readMinutes = it)) })
        }

        EditorCard(title = "Советы", hint = "Значок у совета выбирается по теме") {
            ItemsEditor(
                items = content.tips,
                onChange = { onChange(content.copy(tips = it)) },
                itemLabel = { index, tip -> tip.title.ifBlank { "Совет ${index + 1}" } },
                addLabel = "Добавить совет",
                newItem = { CareTip() },
            ) { tip, onTipChange ->
                KindPicker(selected = tip.kind, onSelect = { onTipChange(tip.copy(kind = it)) })
                TextInput("Название", tip.title, { onTipChange(tip.copy(title = it)) })
                TextInput("Текст", tip.body, { onTipChange(tip.copy(body = it)) }, multiline = true)
            }
        }

        EditorCard(title = "Чего не делать") {
            TextInput("Заголовок блока", content.dontsTitle, { onChange(content.copy(dontsTitle = it)) })
            ItemsEditor(
                items = content.donts,
                onChange = { onChange(content.copy(donts = it)) },
                itemLabel = { index, _ -> "Пункт ${index + 1}" },
                addLabel = "Добавить пункт",
                newItem = { "" },
            ) { item, onItemChange ->
                TextInput("Текст", item, onItemChange)
            }
        }

        EditorCard(
            title = "Написать мастеру",
            isShown = content.contact != null,
            onShownChange = { onChange(content.copy(contact = if (it) content.contact ?: CareContact() else null)) },
        ) {
            val contact = content.contact ?: CareContact()
            TextInput("Заголовок", contact.title, { onChange(content.copy(contact = contact.copy(title = it))) })
            TextInput("Текст", contact.body, { onChange(content.copy(contact = contact.copy(body = it))) }, multiline = true)
            TextInput("Кнопка", contact.button, { onChange(content.copy(contact = contact.copy(button = it))) }, placeholder = "Написать в мастерскую")
        }
    }
}

@Composable
private fun KindPicker(selected: CareTip.Kind, onSelect: (CareTip.Kind) -> Unit) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.pill

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CareTip.Kind.entries.forEach { kind ->
            val isSelected = kind == selected
            Text(
                text = kind.label,
                style = UggTheme.typography.caption,
                color = if (isSelected) colors.contentInverse else colors.content,
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) colors.surfaceInverse else Color.Transparent)
                    .border(1.dp, if (isSelected) colors.surfaceInverse else colors.outline, shape)
                    .clickable(role = Role.RadioButton) { onSelect(kind) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

private val CareTip.Kind.label: String
    get() = when (this) {
        CareTip.Kind.Snow -> "Снег"
        CareTip.Kind.Drying -> "Сушка"
        CareTip.Kind.Salt -> "Соль"
        CareTip.Kind.Cleaning -> "Чистка"
        CareTip.Kind.Protection -> "Защита"
        CareTip.Kind.Storage -> "Хранение"
        CareTip.Kind.Other -> "Другое"
    }
