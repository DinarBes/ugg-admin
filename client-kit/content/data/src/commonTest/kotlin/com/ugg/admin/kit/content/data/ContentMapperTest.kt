package com.ugg.admin.kit.content.data

import com.ugg.admin.kit.content.data.mappers.toDomain
import com.ugg.admin.kit.content.data.mappers.toDto
import com.ugg.admin.kit.content.domain.entities.CareContent
import com.ugg.admin.kit.content.domain.entities.CareTip
import com.ugg.admin.kit.content.domain.entities.CheckoutContent
import com.ugg.admin.kit.content.domain.entities.HomeContent
import com.ugg.admin.kit.content.domain.entities.StoryContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentMapperTest {
    @Test
    fun sectionsSurviveTheRoundTrip() {
        assertEquals(HomeContent.mock, HomeContent.mock.toDto().toDomain())
        assertEquals(StoryContent.mock, StoryContent.mock.toDto().toDomain())
        assertEquals(CareContent.mock, CareContent.mock.toDto().toDomain())
        assertEquals(CheckoutContent.mock, CheckoutContent.mock.toDto().toDomain())
    }

    @Test
    fun hiddenBlocksAreNotWritten() {
        assertNull(HomeContent.mock.copy(quote = null).toDto().quote)
        assertNull(CareContent.mock.copy(contact = null).toDto().contact)
    }

    @Test
    fun unknownTipKindFallsBackToOther() {
        val tip = CareContent.mock.toDto().let { it.copy(tips = listOf(it.tips.first().copy(kind = "rain"))) }.toDomain().tips.single()
        assertEquals(CareTip.Kind.Other, tip.kind)
    }

    @Test
    fun problemsBlockSaving() {
        assertEquals("Нужен заголовок экрана", StoryContent.mock.copy(title = " ").problem)
        assertNull(StoryContent.mock.problem)
    }
}
