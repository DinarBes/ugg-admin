package com.ugg.admin.kit.content.presentation.external

import com.ugg.admin.infrastructure.api.core.TemplateCore
import com.ugg.admin.kit.content.data.useCases.MockContentUseCase
import com.ugg.admin.kit.content.data.useCases.SharedContentUseCase
import com.ugg.admin.kit.content.domain.useCases.ContentUseCase
import com.ugg.admin.kit.content.presentation.external.view.models.ContentEditorViewModel
import com.ugg.admin.kit.content.presentation.external.view.models.ContentViewModel
import com.ugg.admin.kit.core.domain.AppEnvironment
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.on

// One in-memory copy for the whole app, outside Kodein: a binding of the Mock type would also match ContentUseCase.
private val mockContent by lazy { MockContentUseCase() }

val contentModule: DI.Module
    get() = DI.Module("content.module") {
        bind<ContentViewModel>() with factory {
            ContentViewModel(instance())
        }

        bind<ContentEditorViewModel>() with factory {
            ContentEditorViewModel(instance())
        }

        bind<ContentUseCase>() with factory {
            // PRODUCTION edits `content/*` in Firestore (ContentService, scoped in core/data).
            val core: TemplateCore = instance()

            when (instance<AppEnvironment>()) {
                AppEnvironment.PRODUCTION -> SharedContentUseCase(on(core).instance())
                AppEnvironment.MOCK -> mockContent
            }
        }
    }
