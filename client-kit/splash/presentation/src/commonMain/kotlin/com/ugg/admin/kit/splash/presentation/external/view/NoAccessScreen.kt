package com.ugg.admin.kit.splash.presentation.external.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/**
 * Signed in, but not as an admin — or the check needs network. «Проверить ещё раз» picks up
 * access granted meanwhile; «Выйти» returns to the phone step to use another number.
 */
@Composable
fun NoAccessScreen(
    phone: String,
    isOffline: Boolean,
    isChecking: Boolean,
    onRecheck: () -> Unit,
    onSignOut: () -> Unit,
) {
    val colors = UggTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = UggTheme.spacing.xl, vertical = UggTheme.spacing.l),
        verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier.size(56.dp).clip(UggTheme.shapes.pill).background(colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = UggIcons.ShieldCheck, contentDescription = null, tint = colors.accentStrong, modifier = Modifier.size(26.dp))
        }
        Text(
            text = if (isOffline) "Нет соединения" else "Нет доступа",
            style = UggTheme.typography.displayL,
            color = colors.content,
        )
        Text(
            text = if (isOffline) {
                "Не получилось проверить, открыт ли доступ к админке для номера $phone. Проверьте интернет."
            } else {
                "Номер $phone вошёл, но доступа к админке у него нет. Попросите владельца мастерской выдать доступ и нажмите «Проверить ещё раз»."
            },
            style = UggTheme.typography.body,
            color = colors.contentSecondary,
        )
        Spacer(Modifier.weight(1f))
        UggButton(
            text = if (isChecking) "Проверяем…" else "Проверить ещё раз",
            onClick = onRecheck,
            enabled = !isChecking,
            showArrow = false,
            modifier = Modifier.fillMaxWidth(),
        )
        UggButton(
            text = "Войти с другим номером",
            onClick = onSignOut,
            enabled = !isChecking,
            style = UggButtonStyle.Secondary,
            showArrow = false,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
    }
}
