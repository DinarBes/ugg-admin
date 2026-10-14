package com.ugg.admin.kit.content.presentation.internal.view

import com.ugg.admin.kit.content.domain.entities.ContentSection

internal val ContentSection.title: String
    get() = when (this) {
        ContentSection.Home -> "Главная"
        ContentSection.Story -> "О мастерской"
        ContentSection.Care -> "Уход за уггами"
        ContentSection.Onboarding -> "Онбординг"
        ContentSection.Checkout -> "Корзина и доставка"
    }

internal val ContentSection.caption: String
    get() = when (this) {
        ContentSection.Home -> "Главный блок, цифры, «Как рождается пара», цитата мастера"
        ContentSection.Story -> "История и «Путь одной пары»"
        ContentSection.Care -> "Советы и «Чего не делать»"
        ContentSection.Onboarding -> "Слайды при первом запуске"
        ContentSection.Checkout -> "Дополнения, способы доставки, подарочная упаковка"
    }
