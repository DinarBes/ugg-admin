package com.ugg.admin.kit.core.presentation.external.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw brand colors, shared with the client app «Сарма» plus the admin-only status tones.
 *
 * Do not use these directly in screens — go through [UggTheme.colors], which gives each color a
 * role. The palette exists so the roles have one place to point at.
 */
object UggPalette {
    /** Фон приложения. */
    val Milk = Color(0xFFFBF8F3)

    /** Карточки, поверхности. */
    val Cream = Color(0xFFF3ECE1)

    /** Плашки фото, разделители. */
    val Sand = Color(0xFFE7DACA)

    /** Акцент: цвет овчины. */
    val Camel = Color(0xFFB98A5E)

    /** Тёмный акцент, цены. */
    val Tobacco = Color(0xFF7A4F33)

    /** Основной текст, кнопки. */
    val Espresso = Color(0xFF2A211C)

    /** Вторичный текст. */
    val Taupe = Color(0xFF6E635A)

    /** Акцент «вода Байкала»: статусы «в наличии», «открыто». */
    val Baikal = Color(0xFF6F8C99)

    val White = Color(0xFFFFFFFF)

    /** Обводки и разделители. */
    val Line = Color(0xFFE6DDD1)

    /** «Новый» заказ: цифра в сводке. */
    val Terracotta = Color(0xFFB4533C)

    /** Плашка «Новый». */
    val Blush = Color(0xFFF6E3DE)

    /** Текст на [Blush], «Отменить заказ», ошибки. */
    val Rust = Color(0xFF9A3F2B)

    /** Плашка этапов «Раскрой», «Пошив». */
    val Linen = Color(0xFFF1E6DA)

    /** «К выдаче», «Готов». Темнее клиентского Baikal — админка читается при любом свете. */
    val BaikalDeep = Color(0xFF4F6B78)

    /** Плашка «Готов». */
    val Mist = Color(0xFFE3EAED)
}
