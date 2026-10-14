package com.ugg.admin.kit.content.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.domain.states.ContentState
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

private data class MenuRow(val title: String, val caption: String, val edited: String?, val onClick: () -> Unit)

/** Tab «Контент»: the sections of the client app's texts plus «Точки». */
@Composable
internal fun ContentView(
    state: ContentState,
    contentPadding: PaddingValues,
    onOpenSection: (ContentSection) -> Unit,
    onOpenStores: () -> Unit,
) {
    val colors = UggTheme.colors
    val shape = UggTheme.shapes.medium
    val rows = ContentSection.entries.map { section ->
        MenuRow(section.title, section.caption, state.edited[section]) { onOpenSection(section) }
    } + MenuRow("Точки", "Иркутск, Листвянка — адреса, телефоны, графики", null, onOpenStores)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = UggTheme.spacing.l, vertical = UggTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "Тексты приложения".uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)
            Text(text = "Контент", style = UggTheme.typography.displayL.copy(fontWeight = FontWeight.SemiBold), color = colors.content)
            Text(text = "Клиенты увидят правки при следующем открытии экрана", style = UggTheme.typography.bodySmall, color = colors.contentSecondary)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.surfaceElevated)
                .border(1.dp, colors.outline, shape)
                .padding(horizontal = UggTheme.spacing.m, vertical = 6.dp),
        ) {
            rows.forEachIndexed { index, row ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.outline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button, onClick = row.onClick)
                        .padding(vertical = UggTheme.spacing.s),
                    horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = row.title, style = UggTheme.typography.body.copy(fontWeight = FontWeight.SemiBold), color = colors.content)
                        Text(text = row.caption, style = UggTheme.typography.caption, color = colors.contentSecondary)
                        if (row.edited != null) {
                            Text(
                                text = row.edited,
                                style = UggTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.accentStrong,
                            )
                        }
                    }
                    Icon(imageVector = UggIcons.ChevronRight, contentDescription = null, tint = colors.contentSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
