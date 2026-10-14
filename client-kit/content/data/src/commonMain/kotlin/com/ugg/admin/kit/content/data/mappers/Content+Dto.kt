package com.ugg.admin.kit.content.data.mappers

import com.ugg.admin.infrastructure.firebase.firestore.dto.CareContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.CheckoutContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.HomeContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.OnboardingContentDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.StepDto
import com.ugg.admin.infrastructure.firebase.firestore.dto.StoryContentDto
import com.ugg.admin.kit.content.domain.entities.CareContact
import com.ugg.admin.kit.content.domain.entities.CareContent
import com.ugg.admin.kit.content.domain.entities.CareTip
import com.ugg.admin.kit.content.domain.entities.CheckoutContent
import com.ugg.admin.kit.content.domain.entities.CheckoutDelivery
import com.ugg.admin.kit.content.domain.entities.CheckoutExtra
import com.ugg.admin.kit.content.domain.entities.CheckoutGiftWrap
import com.ugg.admin.kit.content.domain.entities.ContentStep
import com.ugg.admin.kit.content.domain.entities.HomeCareCard
import com.ugg.admin.kit.content.domain.entities.HomeContent
import com.ugg.admin.kit.content.domain.entities.HomeQuote
import com.ugg.admin.kit.content.domain.entities.HomeStat
import com.ugg.admin.kit.content.domain.entities.OnboardingContent
import com.ugg.admin.kit.content.domain.entities.OnboardingSlide
import com.ugg.admin.kit.content.domain.entities.StoryContent
import com.ugg.admin.kit.content.domain.entities.StoryHighlight

// Firestore → editor. One-to-one: the editor shows exactly what the client app reads.

internal fun StepDto.toDomain() = ContentStep(number = number, title = title, description = description)

internal fun ContentStep.toDto() = StepDto(number = number.trim(), title = title.trim(), description = description.trim())

internal fun HomeContentDto.toDomain() = HomeContent(
    heroKicker = heroKicker,
    heroDescription = heroDescription,
    stats = stats.map { HomeStat(it.value, it.caption) },
    craftSteps = craftSteps.map { it.toDomain() },
    quote = quote?.let { HomeQuote(it.text, it.author, it.role) },
    careCard = care?.let { HomeCareCard(it.kicker, it.title, it.readMinutes) },
)

internal fun HomeContent.toDto() = HomeContentDto(
    heroKicker = heroKicker.trim(),
    heroDescription = heroDescription.trim(),
    stats = stats.map { HomeContentDto.StatDto(it.value.trim(), it.caption.trim()) },
    craftSteps = craftSteps.map { it.toDto() },
    quote = quote?.let { HomeContentDto.QuoteDto(it.text.trim(), it.author.trim(), it.role.trim()) },
    care = careCard?.let { HomeContentDto.CareDto(it.kicker.trim(), it.title.trim(), it.readMinutes) },
)

internal fun StoryContentDto.toDomain() = StoryContent(
    kicker = kicker,
    title = title,
    intro = intro,
    stepsTitle = stepsTitle,
    steps = steps.map { it.toDomain() },
    highlight = highlight?.let { StoryHighlight(it.kicker, it.title, it.body) },
)

internal fun StoryContent.toDto() = StoryContentDto(
    kicker = kicker.trim(),
    title = title.trim(),
    intro = intro.trim(),
    stepsTitle = stepsTitle.trim(),
    steps = steps.map { it.toDto() },
    highlight = highlight?.let { StoryContentDto.HighlightDto(it.kicker.trim(), it.title.trim(), it.body.trim()) },
)

internal fun CareContentDto.toDomain() = CareContent(
    kicker = kicker,
    title = title,
    intro = intro,
    readMinutes = readMinutes,
    tips = tips.map { CareTip(CareTip.Kind.of(it.kind), it.title, it.body) },
    dontsTitle = dontsTitle,
    donts = donts,
    contact = contact?.let { CareContact(it.title, it.body, it.button) },
)

internal fun CareContent.toDto() = CareContentDto(
    kicker = kicker.trim(),
    title = title.trim(),
    intro = intro.trim(),
    readMinutes = readMinutes,
    tips = tips.map { CareContentDto.TipDto(it.kind.id, it.title.trim(), it.body.trim()) },
    dontsTitle = dontsTitle.trim(),
    donts = donts.map { it.trim() },
    contact = contact?.let { CareContentDto.ContactDto(it.title.trim(), it.body.trim(), it.button.trim()) },
)

internal fun OnboardingContentDto.toDomain() = OnboardingContent(
    slides = slides.map { OnboardingSlide(it.id, it.overline, it.title, it.description) },
)

internal fun OnboardingContent.toDto() = OnboardingContentDto(
    slides = slides.map { OnboardingContentDto.SlideDto(it.id, it.overline.trim(), it.title.trim(), it.description.trim()) },
)

internal fun CheckoutContentDto.toDomain() = CheckoutContent(
    extras = extras.map { CheckoutExtra(it.id, it.title, it.caption, it.price) },
    deliveries = deliveries.map { CheckoutDelivery(it.id, it.title, it.description, it.price, it.isFromPrice, it.storeId) },
    giftWrap = CheckoutGiftWrap(giftWrap.title, giftWrap.description, giftWrap.price),
)

internal fun CheckoutContent.toDto() = CheckoutContentDto(
    extras = extras.map { CheckoutContentDto.ExtraDto(it.id, it.title.trim(), it.caption.trim(), it.price) },
    deliveries = deliveries.map {
        CheckoutContentDto.DeliveryDto(it.id, it.title.trim(), it.description.trim(), it.price, it.isFromPrice, it.storeId)
    },
    giftWrap = CheckoutContentDto.GiftWrapDto(giftWrap.title.trim(), giftWrap.description.trim(), giftWrap.price),
)
