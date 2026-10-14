package com.ugg.admin.kit.content.presentation

import com.ugg.admin.kit.content.data.useCases.MockContentUseCase
import com.ugg.admin.kit.content.domain.entities.ContentSection
import com.ugg.admin.kit.content.domain.entities.StoryContent
import com.ugg.admin.kit.content.presentation.external.view.models.ContentEditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class ContentEditorViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun editAndSaveAStory() = runTest {
        val useCase = MockContentUseCase(latency = Duration.ZERO)
        val vm = ContentEditorViewModel(useCase)
        vm.load(ContentSection.Story)
        advanceUntilIdle()

        val story = vm.state.value.draft as StoryContent
        vm.onChange(story.copy(intro = "Новое вступление"))
        assertTrue(vm.state.value.canSave)

        vm.onSave()
        advanceUntilIdle()

        assertFalse(vm.state.value.hasChanges)
        assertEquals("Новое вступление", (useCase.fetch(ContentSection.Story) as StoryContent).intro)
        assertTrue(ContentSection.Story in useCase.observeUpdatedAt().first())
    }

    @Test
    fun aProblemBlocksSaving() = runTest {
        val vm = ContentEditorViewModel(MockContentUseCase(latency = Duration.ZERO))
        vm.load(ContentSection.Story)
        advanceUntilIdle()

        vm.onChange((vm.state.value.draft as StoryContent).copy(title = ""))

        assertTrue(vm.state.value.hasChanges)
        assertFalse(vm.state.value.canSave)
    }
}
