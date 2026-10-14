package com.ugg.admin.kit.catalog.data

import com.ugg.admin.kit.catalog.domain.entities.Catalog
import com.ugg.admin.kit.catalog.domain.entities.CatalogShade
import com.ugg.admin.kit.catalog.domain.entities.LeadTime
import com.ugg.admin.kit.catalog.domain.states.CatalogState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CatalogStateTest {
    private val saved = Catalog.mock
    private val state = CatalogState(saved = saved, price = "14900", leadTime = "5–7", shades = saved.shades, sizes = saved.sizes)

    @Test
    fun untouchedFormHasNoChanges() {
        assertFalse(state.hasChanges)
        assertFalse(state.canSave)
    }

    @Test
    fun priceWithSpacesIsRead() {
        val edited = state.copy(price = "15 900")

        assertEquals(15900, edited.draft?.price)
        assertTrue(edited.canSave)
    }

    @Test
    fun brokenLeadTimeBlocksSaving() {
        val edited = state.copy(leadTime = "7–5")

        assertNull(edited.draft)
        assertEquals("Например, 5–7", edited.leadTimeError)
        assertFalse(edited.canSave)
    }

    @Test
    fun leadTimeForms() {
        assertEquals(LeadTime(5, 7), LeadTime.parse("5-7"))
        assertEquals(LeadTime(7, 7), LeadTime.parse(" 7 "))
        assertNull(LeadTime.parse("0"))
        assertNull(LeadTime.parse("неделя"))
    }

    @Test
    fun shadeIdsAreLatinAndUnique() {
        assertEquals("molochnyj-shokolad", CatalogShade.idFor("Молочный шоколад", emptyList()))
        assertEquals("sand-2", CatalogShade.idFor("Sand", listOf("sand")))
    }
}
