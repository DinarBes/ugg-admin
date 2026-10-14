package com.ugg.admin.kit.content.presentation.internal.view.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.CheckoutContent
import com.ugg.admin.kit.content.domain.entities.CheckoutExtra
import com.ugg.admin.kit.content.presentation.internal.view.EditorCard
import com.ugg.admin.kit.content.presentation.internal.view.ItemsEditor
import com.ugg.admin.kit.content.presentation.internal.view.NumberInput
import com.ugg.admin.kit.content.presentation.internal.view.TextInput
import com.ugg.admin.kit.content.presentation.internal.view.newId
import com.ugg.admin.kit.core.presentation.external.components.UggSwitch
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/** «Корзина и доставка»: extras sold with the boots, delivery options, the gift wrap. */
@Composable
internal fun CheckoutEditor(content: CheckoutContent, onChange: (CheckoutContent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EditorCard(title = "Дополнения к паре") {
            ItemsEditor(
                items = content.extras,
                onChange = { onChange(content.copy(extras = it)) },
                itemLabel = { index, extra -> extra.title.ifBlank { "Дополнение ${index + 1}" } },
                addLabel = "Добавить дополнение",
                newItem = { extras -> CheckoutExtra(id = newId("extra", extras.map { it.id })) },
            ) { extra, onExtraChange ->
                TextInput("Название", extra.title, { onExtraChange(extra.copy(title = it)) })
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextInput("Подпись", extra.caption, { onExtraChange(extra.copy(caption = it)) }, Modifier.weight(0.6f), placeholder = "уход")
                    NumberInput("Цена, ₽", extra.price, { onExtraChange(extra.copy(price = it)) }, Modifier.weight(0.4f))
                }
            }
        }

        EditorCard(title = "Доставка", hint = "Способы заданы приложением: меняются тексты и цены") {
            ItemsEditor(
                items = content.deliveries,
                onChange = { onChange(content.copy(deliveries = it)) },
                itemLabel = { _, delivery -> delivery.title.ifBlank { delivery.id } },
                addLabel = "",
                newItem = { error("deliveries are fixed") },
                canAddRemove = false,
            ) { delivery, onDeliveryChange ->
                TextInput("Название", delivery.title, { onDeliveryChange(delivery.copy(title = it)) })
                TextInput("Описание", delivery.description, { onDeliveryChange(delivery.copy(description = it)) }, multiline = true)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    NumberInput("Цена, ₽", delivery.price, { onDeliveryChange(delivery.copy(price = it)) }, Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "«от»", style = UggTheme.typography.caption, color = UggTheme.colors.contentSecondary)
                        UggSwitch(checked = delivery.isFromPrice, onCheckedChange = { onDeliveryChange(delivery.copy(isFromPrice = it)) })
                    }
                }
            }
        }

        EditorCard(title = "Подарочная упаковка") {
            val wrap = content.giftWrap
            TextInput("Название", wrap.title, { onChange(content.copy(giftWrap = wrap.copy(title = it))) })
            TextInput("Описание", wrap.description, { onChange(content.copy(giftWrap = wrap.copy(description = it))) }, multiline = true)
            NumberInput("Цена, ₽", wrap.price, { onChange(content.copy(giftWrap = wrap.copy(price = it))) })
        }
    }
}
