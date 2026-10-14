package com.ugg.admin.kit.catalog.domain.entities

/**
 * What the workshop edits about the only model, «Классика» (`products/classic`): price, sewing
 * time, shades in stock and sizes on offer. Texts and photos of the model live in «Контент».
 */
data class Catalog(
    val productId: String = "",
    val title: String = "",
    /** Rubles. */
    val price: Int = 0,
    val leadTime: LeadTime = LeadTime(),
    val shades: List<CatalogShade> = emptyList(),
    val sizes: List<CatalogSize> = emptyList(),
) {
    companion object {
        val mock
            get() = Catalog(
                productId = "classic",
                title = "Классика",
                price = 14900,
                leadTime = LeadTime(min = 5, max = 7),
                shades = listOf(
                    CatalogShade("chestnut", "Каштан", 0xFFB98A5E, inStock = true),
                    CatalogShade("sand", "Песок", 0xFFD9C3A2, inStock = true),
                    CatalogShade("milk", "Молоко", 0xFFEDE3D5, inStock = false),
                    CatalogShade("chocolate", "Шоколад", 0xFF5A3E2E, inStock = true),
                    CatalogShade("graphite", "Графит", 0xFF5E5B57, inStock = false),
                ),
                sizes = (35..45).map { CatalogSize(label = it.toString(), isAvailable = it != 44) },
            )
    }
}

/** Sewing time in days: «5–7», or «7» when [min] equals [max]. */
data class LeadTime(val min: Int = 0, val max: Int = 0) {
    val label: String
        get() = if (min == max) "$min" else "$min–$max"

    companion object {
        private val pattern = Regex("""^\s*(\d{1,3})\s*(?:[-–—]\s*(\d{1,3}))?\s*$""")

        /** «5–7», «5-7» or «7»; `null` for anything else, zero days or a reversed range. */
        fun parse(text: String): LeadTime? {
            val match = pattern.matchEntire(text) ?: return null
            val min = match.groupValues[1].toInt()
            val max = match.groupValues[2].ifEmpty { null }?.toInt() ?: min
            return LeadTime(min, max).takeIf { min in 1..max }
        }
    }
}

data class CatalogShade(
    /** Stays fixed once orders reference it; the name may change. */
    val id: String = "",
    val name: String = "",
    /** ARGB. */
    val color: Long = 0xFFB98A5E,
    /** Out of stock shades stay in the app but can't be ordered. */
    val inStock: Boolean = true,
) {
    companion object {
        /** A latin id for a new shade: «Молочный шоколад» → `molochnyj-shokolad`, made unique among [taken]. */
        fun idFor(name: String, taken: Collection<String>): String {
            val slug = name.lowercase()
                .map { translit[it] ?: if (it in 'a'..'z' || it in '0'..'9') it.toString() else "-" }
                .joinToString("")
                .replace(Regex("-+"), "-")
                .trim('-')
                .ifEmpty { "shade" }

            if (slug !in taken) return slug
            return generateSequence(2) { it + 1 }.map { "$slug-$it" }.first { it !in taken }
        }

        private val translit = mapOf(
            'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d", 'е' to "e", 'ё' to "e", 'ж' to "zh",
            'з' to "z", 'и' to "i", 'й' to "j", 'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n", 'о' to "o",
            'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t", 'у' to "u", 'ф' to "f", 'х' to "h", 'ц' to "c",
            'ч' to "ch", 'ш' to "sh", 'щ' to "sch", 'ъ' to "", 'ы' to "y", 'ь' to "", 'э' to "e", 'ю' to "yu",
            'я' to "ya",
        )
    }
}

data class CatalogSize(
    val label: String = "",
    /** Hidden sizes disappear from the size picker — e.g. while there is no pattern for them. */
    val isAvailable: Boolean = true,
)
