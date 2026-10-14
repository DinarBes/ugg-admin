package com.ugg.admin.kit.content.presentation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ugg.admin.kit.content.domain.entities.CareContent
import com.ugg.admin.kit.content.domain.entities.CheckoutContent
import com.ugg.admin.kit.content.domain.entities.ContentDraft
import com.ugg.admin.kit.content.domain.entities.HomeContent
import com.ugg.admin.kit.content.domain.entities.OnboardingContent
import com.ugg.admin.kit.content.domain.entities.StoryContent
import com.ugg.admin.kit.content.domain.states.ContentEditorState
import com.ugg.admin.kit.content.presentation.internal.view.editors.CareEditor
import com.ugg.admin.kit.content.presentation.internal.view.editors.CheckoutEditor
import com.ugg.admin.kit.content.presentation.internal.view.editors.HomeEditor
import com.ugg.admin.kit.content.presentation.internal.view.editors.OnboardingEditor
import com.ugg.admin.kit.content.presentation.internal.view.editors.StoryEditor
import com.ugg.admin.kit.core.presentation.external.components.UggButton
import com.ugg.admin.kit.core.presentation.external.components.UggButtonStyle
import com.ugg.admin.kit.core.presentation.external.components.UggIconButton
import com.ugg.admin.kit.core.presentation.external.icons.UggIcons
import com.ugg.admin.kit.core.presentation.external.theme.UggTheme

@Composable
internal fun ContentEditorView(
    state: ContentEditorState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onChange: (ContentDraft) -> Unit,
    onSave: () -> Unit,
) {
    val colors = UggTheme.colors
    val draft = state.draft

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = UggTheme.spacing.l, vertical = UggTheme.spacing.s),
            horizontalArrangement = Arrangement.spacedBy(UggTheme.spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UggIconButton(icon = UggIcons.ArrowLeft, contentDescription = "Назад к контенту", onClick = onBack)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Контент".uppercase(), style = UggTheme.typography.overline, color = colors.accentStrong)
                Text(
                    text = state.section?.title.orEmpty(),
                    style = UggTheme.typography.displayM.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.content,
                )
            }
        }

        when {
            draft != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                when (draft) {
                    is HomeContent -> HomeEditor(draft, onChange)
                    is StoryContent -> StoryEditor(draft, onChange)
                    is CareContent -> CareEditor(draft, onChange)
                    is OnboardingContent -> OnboardingEditor(draft, onChange)
                    is CheckoutContent -> CheckoutEditor(draft, onChange)
                }

                val message = state.error?.let { it.message ?: "Не получилось сохранить, попробуйте ещё раз" }
                    ?: draft.problem.takeIf { state.hasChanges }
                if (message != null) {
                    Text(
                        text = message,
                        style = UggTheme.typography.caption,
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                UggButton(
                    text = when {
                        state.isSaving -> "Сохраняем…"
                        state.isJustSaved && !state.hasChanges -> "Сохранено"
                        else -> "Сохранить"
                    },
                    onClick = onSave,
                    enabled = state.canSave,
                    showArrow = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = UggTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(UggTheme.spacing.m, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.error?.message ?: "Не удалось загрузить тексты",
                    style = UggTheme.typography.body,
                    color = colors.contentSecondary,
                    textAlign = TextAlign.Center,
                )
                UggButton(text = "Повторить", onClick = onRetry, style = UggButtonStyle.Secondary, showArrow = false)
            }

            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.accent)
            }
        }
    }
}
