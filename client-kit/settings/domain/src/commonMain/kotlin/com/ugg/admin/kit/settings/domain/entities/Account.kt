package com.ugg.admin.kit.settings.domain.entities

/** Who is signed in to the admin app. */
data class Account(
    /** `null` until the name is filled in (in the client app's profile or the console). */
    val name: String? = null,
    /** «+7 900 000-00-00». */
    val phone: String = "",
) {
    companion object {
        val mock get() = Account(name = "Мастер", phone = "+7 900 000-00-00")
    }
}
