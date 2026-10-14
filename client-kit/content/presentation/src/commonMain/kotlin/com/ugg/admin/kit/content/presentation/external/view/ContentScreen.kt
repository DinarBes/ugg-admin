package com.ugg.admin.kit.content.presentation.external.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.presentation.external.view.models.ContentViewModel
import com.ugg.admin.kit.content.presentation.internal.view.ContentView
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * Tab «Контент»: the client app's texts, section by section, plus «Точки».
 *
 * @param contentPadding the tab bar and system bars, from the host's Scaffold.
 */
@Composable
fun ContentScreen(
    contentPadding: PaddingValues,
    onOpenSection: (ContentSection) -> Unit,
    onOpenStores: () -> Unit,
) {
    val viewModel: ContentViewModel by rememberViewModel()
    val state by viewModel.state.collectAsState()

    ContentView(
        state = state,
        contentPadding = contentPadding,
        onOpenSection = onOpenSection,
        onOpenStores = onOpenStores,
    )
}
