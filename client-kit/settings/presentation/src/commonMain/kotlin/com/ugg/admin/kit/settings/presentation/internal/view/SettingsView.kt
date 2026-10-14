package com.ugg.admin.kit.settings.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggIconButton
import com.ugg.admin.kit.core.presentation.external.components.UggSection
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme
import com.ugg.admin.kit.settings.domain.states.SettingsState

@Composable
internal fun SettingsView(
    state: SettingsState,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    val colors = UggTheme.colors
    val account = state.account

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = UggTheme.spacing.l, vertical = UggTheme.spacing.s),
            horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UggIconButton(icon = UggIcons.ArrowLeft, contentDescription = "Назад", onClick = onBack)
            Text(
                text = "Настройки",
                style = UggTheme.typography.displayM.copy(fontWeight = FontWeight.SemiBold),
                color = colors.content,
            )
        }

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = UggTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            UggSection(title = "Аккаунт") {
                Row(horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(UggTheme.shapes.pill).background(colors.surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = account?.name?.firstOrNull()?.uppercase() ?: "М",
                            style = UggTheme.typography.heading,
                            color = colors.accentStrong,
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = account?.name ?: "Без имени",
                            style = UggTheme.typography.heading,
                            color = colors.content,
                        )
                        Text(
                            text = account?.phone.orEmpty(),
                            style = UggTheme.typography.bodySmall,
                            color = colors.contentSecondary,
                        )
                    }
                    Text(
                        text = "админ",
                        style = UggTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.info,
                        modifier = Modifier
                            .clip(UggTheme.shapes.pill)
                            .background(colors.infoContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
                Text(
                    text = "Имя берётся из профиля в клиентском приложении.",
                    style = UggTheme.typography.caption,
                    color = colors.contentSecondary,
                )
            }

            Spacer(Modifier.weight(1f))

            UggButton(
                text = if (state.isSigningOut) "Выходим…" else "Выйти из аккаунта",
                onClick = onSignOut,
                enabled = !state.isSigningOut,
                style = UggButtonStyle.Secondary,
                showArrow = false,
                leadingIcon = UggIcons.Logout,
                modifier = Modifier.fillMaxWidth().padding(bottom = UggTheme.spacing.m),
            )
        }
    }
}
