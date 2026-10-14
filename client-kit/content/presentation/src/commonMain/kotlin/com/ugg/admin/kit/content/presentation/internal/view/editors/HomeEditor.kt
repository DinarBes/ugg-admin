package com.ugg.admin.kit.content.presentation.internal.view.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.ContentStep
import com.ugg.admin.kit.content.domain.entities.HomeCareCard
import com.ugg.admin.kit.content.domain.entities.HomeContent
import com.ugg.admin.kit.content.domain.entities.HomeQuote
import com.ugg.admin.kit.content.domain.entities.HomeStat
import com.ugg.admin.kit.content.presentation.internal.view.EditorCard
import com.ugg.admin.kit.content.presentation.internal.view.ItemsEditor
import com.ugg.admin.kit.content.presentation.internal.view.NumberInput
import com.ugg.admin.kit.content.presentation.internal.view.TextInput
import com.ugg.admin.kit.content.presentation.internal.view.stepNumber

/** «Главная»: the hero, the numbers, «Как рождается пара», the quote and the care card. */
@Composable
internal fun HomeEditor(content: HomeContent, onChange: (HomeContent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EditorCard(title = "Главный блок", hint = "Название модели, цена и срок берутся из «Каталога»") {
            TextInput("Надзаголовок", content.heroKicker, { onChange(content.copy(heroKicker = it)) })
            TextInput("Описание", content.heroDescription, { onChange(content.copy(heroDescription = it)) }, multiline = true)
        }

        EditorCard(title = "Цифры", hint = "Перенос строки в подписи — Enter") {
            ItemsEditor(
                items = content.stats,
                onChange = { onChange(content.copy(stats = it)) },
                itemLabel = { index, stat -> stat.value.ifBlank { "Цифра ${index + 1}" } },
                addLabel = "Добавить цифру",
                newItem = { HomeStat() },
            ) { stat, onStatChange ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextInput("Значение", stat.value, { onStatChange(stat.copy(value = it)) }, Modifier.weight(0.4f), placeholder = "6 ч")
                    TextInput("Подпись", stat.caption, { onStatChange(stat.copy(caption = it)) }, Modifier.weight(0.6f), multiline = true)
                }
            }
        }

        EditorCard(title = "Как рождается пара") {
            StepsEditor(steps = content.craftSteps, onChange = { onChange(content.copy(craftSteps = it)) })
        }

        EditorCard(
            title = "Цитата мастера",
            isShown = content.quote != null,
            onShownChange = { onChange(content.copy(quote = if (it) content.quote ?: HomeQuote() else null)) },
        ) {
            val quote = content.quote ?: HomeQuote()
            TextInput("Цитата", quote.text, { onChange(content.copy(quote = quote.copy(text = it))) }, multiline = true)
            TextInput("Автор", quote.author, { onChange(content.copy(quote = quote.copy(author = it))) })
            TextInput("Кто это", quote.role, { onChange(content.copy(quote = quote.copy(role = it))) }, placeholder = "основательница мастерской")
        }

        EditorCard(
            title = "Карточка «Уход»",
            isShown = content.careCard != null,
            onShownChange = { onChange(content.copy(careCard = if (it) content.careCard ?: HomeCareCard() else null)) },
            hint = "Ведёт на экран «Уход за уггами»",
        ) {
            val card = content.careCard ?: HomeCareCard()
            TextInput("Надзаголовок", card.kicker, { onChange(content.copy(careCard = card.copy(kicker = it))) })
            TextInput("Заголовок", card.title, { onChange(content.copy(careCard = card.copy(title = it))) }, multiline = true)
            NumberInput("Минут чтения", card.readMinutes, { onChange(content.copy(careCard = card.copy(readMinutes = it))) })
        }
    }
}

/** Numbered steps, shared by «Главная» and «О мастерской». */
@Composable
internal fun StepsEditor(steps: List<ContentStep>, onChange: (List<ContentStep>) -> Unit) {
    ItemsEditor(
        items = steps,
        onChange = onChange,
        itemLabel = { index, step -> "${step.number.ifBlank { stepNumber(index) }} · ${step.title.ifBlank { "Новый шаг" }}" },
        addLabel = "Добавить шаг",
        newItem = { ContentStep(number = stepNumber(it.size)) },
    ) { step, onStepChange ->
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextInput("№", step.number, { onStepChange(step.copy(number = it.take(3))) }, Modifier.weight(0.25f))
            TextInput("Название", step.title, { onStepChange(step.copy(title = it)) }, Modifier.weight(0.75f))
        }
        TextInput("Описание", step.description, { onStepChange(step.copy(description = it)) }, multiline = true)
    }
}
