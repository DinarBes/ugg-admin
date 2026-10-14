package com.ugg.admin.kit.core.presentation.external.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

/** On/off toggle from the admin design: tobacco track when on, sand when off, white thumb either way. */
@Composable
fun UggSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = UggTheme.colors

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = colors.accentStrong,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = colors.surfaceVariant,
            uncheckedBorderColor = Color.Transparent,
        ),
    )
}
