package com.template.client.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.template.client.kit.navigation.presentation.external.view.NavigationScreen
import org.kodein.di.compose.withDI

@Composable
fun ContentView(app: TemplateApp) = withDI(app.di) {
    MaterialTheme {
        NavigationScreen()
    }
}
