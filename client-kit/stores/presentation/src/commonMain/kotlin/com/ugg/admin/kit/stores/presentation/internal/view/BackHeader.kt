package com.ugg.admin.kit.stores.presentation.internal.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.components.UggIconButton
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/** Back button, kicker and title — the top of pushed admin screens. */
@Composable
internal fun BackHeader(kicker: String, title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = UggTheme.spacing.l, vertical = UggTheme.spacing.s),
        horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UggIconButton(icon = UggIcons.ArrowLeft, contentDescription = "Назад", onClick = onBack)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = kicker.uppercase(), style = UggTheme.typography.overline, color = UggTheme.colors.accentStrong)
            Text(
                text = title,
                style = UggTheme.typography.displayM.copy(fontWeight = FontWeight.SemiBold),
                color = UggTheme.colors.content,
            )
        }
    }
}
