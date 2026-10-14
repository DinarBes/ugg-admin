package com.ugg.admin.kit.content.domain.entities

/** The client app's screen texts, one Firestore document each: `content/{id}`. */
enum class ContentSection(val id: String) {
    Home("home"),
    Story("story"),
    Care("care"),
    Onboarding("onboarding"),
    Checkout("checkout"),
}
