package com.ugg.admin.kit.splash.presentation.external.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

@Composable
fun SplashScreen(
    error: Throwable? = null,
    onRetry: () -> Unit = {},
) {
    val colors = UggTheme.colors

    Column(
        modifier = Modifier.fillMaxSize().background(colors.background).padding(horizontal = UggTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m, Alignment.CenterVertically),
    ) {
        Text(text = "Сарма", style = UggTheme.typography.displayXL, color = colors.content)
        Text(text = "Мастерская".uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)

        if (error == null) {
            CircularProgressIndicator(color = colors.accent)
        } else {
            Text(
                text = error.message ?: "Что-то пошло не так",
                style = UggTheme.typography.body,
                color = colors.contentSecondary,
                textAlign = TextAlign.Center,
            )
            UggButton(text = "Повторить", onClick = onRetry, style = UggButtonStyle.Secondary, showArrow = false)
        }
    }
}
