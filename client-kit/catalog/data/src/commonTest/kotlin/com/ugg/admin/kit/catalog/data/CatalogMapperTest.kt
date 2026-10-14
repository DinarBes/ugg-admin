package com.ugg.admin.kit.catalog.data

import com.ugg.admin.infrastructure.firebase.firestore.dto.ProductDto
import com.ugg.admin.kit.catalog.data.mappers.applyTo
import com.ugg.admin.kit.catalog.data.mappers.toDomain
import com.ugg.admin.kit.catalog.data.mappers.toHex
import kotlin.test.Test
import kotlin.test.assertEquals

class CatalogMapperTest {
    @Test
    fun colorsSurviveTheRoundTrip() {
        assertEquals("#B98A5E", 0xFFB98A5EL.toHex())
        assertEquals("#05A0FF", 0xFF05A0FFL.toHex())
    }

    @Test
    fun editsKeepTheRestOfTheProduct() {
        val product = ProductDto(
            id = "classic",
            title = "Классика",
            description = "Высокие угги",
            price = 14900,
            shades = listOf(ProductDto.ShadeDto("milk", "Молоко", "#EDE3D5", inStock = false)),
        )

        val catalog = product.toDomain()
        val edited = catalog.copy(price = 15900, shades = catalog.shades.map { it.copy(inStock = true) }).applyTo(product)

        assertEquals("Высокие угги", edited.description)
        assertEquals(15900, edited.price)
        assertEquals(ProductDto.ShadeDto("milk", "Молоко", "#EDE3D5", inStock = true), edited.shades.single())
    }
}
