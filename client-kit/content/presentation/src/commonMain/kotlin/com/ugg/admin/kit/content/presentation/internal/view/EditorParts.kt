package com.ugg.admin.kit.content.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.components.UggSwitch
import com.ugg.admin.kit.core.presentation.external.components.UggTextField
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/** A text field of the editors. [multiline] for descriptions and titles that may break lines. */
@Composable
internal fun TextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    multiline: Boolean = false,
    placeholder: String = "",
    hint: String? = null,
) {
    UggTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        hint = hint,
        singleLine = !multiline,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = modifier,
    )
}

/** Whole numbers only: rubles, minutes. An empty field means 0. */
@Composable
internal fun NumberInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    UggTextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = { text -> onValueChange(text.filter { it.isDigit() }.take(7).toIntOrNull() ?: 0) },
        label = label,
        placeholder = "0",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}

/** A white card with a kicker; with [isShown] it gets a switch and hides its content when off. */
@Composable
internal fun EditorCard(
    title: String,
    modifier: Modifier = Modifier,
    isShown: Boolean? = null,
    onShownChange: (Boolean) -> Unit = {},
    hint: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.medium

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.outline, shape)
            .padding(UggTheme.spacing.m),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title.uppercase(),
                style = UggTheme.typography.overline,
                color = colors.accentStrong,
                modifier = Modifier.weight(1f),
            )
            if (isShown != null) {
                Text(
                    text = if (isShown) "показывать" else "скрыто",
                    style = UggTheme.typography.caption,
                    color = colors.contentSecondary,
                    modifier = Modifier.padding(end = UggTheme.spacing.xs),
                )
                UggSwitch(checked = isShown, onCheckedChange = onShownChange)
            }
        }
        if (hint != null) {
            Text(text = hint, style = UggTheme.typography.caption, color = colors.contentSecondary)
        }
        if (isShown != false) content()
    }
}

/**
 * A list the master can reorder and grow: each item in its own block with «↑ ↓ ✕», and an
 * «+ Добавить …» link under the last one. [canAddRemove] `false` keeps the count fixed.
 */
@Composable
internal fun <T> ItemsEditor(
    items: List<T>,
    onChange: (List<T>) -> Unit,
    itemLabel: (index: Int, item: T) -> String,
    addLabel: String,
    newItem: (List<T>) -> T,
    canAddRemove: Boolean = true,
    canReorder: Boolean = true,
    itemContent: @Composable (item: T, onItemChange: (T) -> Unit) -> Unit,
) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.small

    items.forEachIndexed { index, item ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.background)
                .border(1.dp, colors.outline, shape)
                .padding(horizontal = UggTheme.spacing.s, vertical = UggTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.xs),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = itemLabel(index, item),
                    style = UggTheme.typography.label,
                    color = colors.content,
                    modifier = Modifier.weight(1f),
                )
                if (canReorder) {
                    SmallIcon(UggIcons.ChevronUp, "Выше", enabled = index > 0) { onChange(items.move(index, index - 1)) }
                    SmallIcon(UggIcons.ChevronDown, "Ниже", enabled = index < items.lastIndex) { onChange(items.move(index, index + 1)) }
                }
                if (canAddRemove) {
                    SmallIcon(UggIcons.Close, "Удалить") { onChange(items.filterIndexed { i, _ -> i != index }) }
                }
            }
            itemContent(item) { changed -> onChange(items.mapIndexed { i, old -> if (i == index) changed else old }) }
            Spacer(Modifier.size(2.dp))
        }
    }

    if (canAddRemove) {
        Text(
            text = "+ $addLabel",
            style = UggTheme.typography.label,
            color = colors.accentStrong,
            modifier = Modifier
                .clip(UggTheme.shapes.pill)
                .clickable(role = Role.Button) { onChange(items + newItem(items)) }
                .padding(vertical = UggTheme.spacing.xs),
        )
    }
}

@Composable
private fun SmallIcon(icon: ImageVector, description: String, enabled: Boolean = true, onClick: () -> Unit) {
    IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(36.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) UggTheme.colors.content else UggTheme.colors.outline,
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun <T> List<T>.move(from: Int, to: Int): List<T> {
    if (to !in indices) return this
    return toMutableList().apply { add(to, removeAt(from)) }
}

/** A fresh id like `extra-4` that none of [taken] uses. */
internal fun newId(prefix: String, taken: Collection<String>): String =
    generateSequence(taken.size + 1) { it + 1 }.map { "$prefix-$it" }.first { it !in taken }

/** «01», «02» … for a new step after [count] existing ones. */
internal fun stepNumber(count: Int): String = (count + 1).toString().padStart(2, '0')
