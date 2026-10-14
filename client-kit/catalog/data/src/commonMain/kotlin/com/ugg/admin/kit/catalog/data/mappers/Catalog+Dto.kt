package com.ugg.admin.kit.catalog.data.mappers

import com.ugg.admin.infrastructure.firebase.firestore.dto.ProductDto
import com.ugg.admin.kit.catalog.domain.entities.Catalog
import com.ugg.admin.kit.catalog.domain.entities.CatalogShade
import com.ugg.admin.kit.catalog.domain.entities.CatalogSize
import com.ugg.admin.kit.catalog.domain.entities.LeadTime
import com.ugg.admin.kit.core.data.formatting.toArgb

internal fun ProductDto.toDomain() = Catalog(
    productId = id,
    title = title,
    price = price,
    leadTime = LeadTime(min = leadTimeDays.min, max = leadTimeDays.max),
    shades = shades.map { CatalogShade(id = it.id, name = it.name, color = it.color.toArgb(), inStock = it.inStock) },
    sizes = sizes.map { CatalogSize(label = it.label, isAvailable = it.available) },
)

/** The edited options over the [product] as read; everything else is left as it was. */
internal fun Catalog.applyTo(product: ProductDto) = product.copy(
    price = price,
    leadTimeDays = ProductDto.LeadTimeDto(min = leadTime.min, max = leadTime.max),
    shades = shades.map { ProductDto.ShadeDto(id = it.id, name = it.name, color = it.color.toHex(), inStock = it.inStock) },
    sizes = sizes.map { ProductDto.SizeDto(label = it.label, available = it.isAvailable) },
)

/** `0xFFB98A5E` → `"#B98A5E"`, the form the client app reads. */
internal fun Long.toHex(): String = "#" + (this and 0xFFFFFF).toString(16).uppercase().padStart(6, '0')
