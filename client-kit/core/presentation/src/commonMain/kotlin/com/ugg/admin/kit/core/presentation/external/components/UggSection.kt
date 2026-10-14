package com.ugg.admin.kit.core.presentation.external.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/**
 * A white card with an uppercase kicker — the building block of the admin forms («Этап»,
 * «Цена и срок», «Цвета»). [trailing] sits at the end of the kicker row, e.g. «в наличии».
 */
@Composable
fun UggSection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
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
            if (trailing != null) {
                Text(text = trailing, style = UggTheme.typography.caption, color = colors.contentSecondary)
            }
        }
        content()
    }
}
