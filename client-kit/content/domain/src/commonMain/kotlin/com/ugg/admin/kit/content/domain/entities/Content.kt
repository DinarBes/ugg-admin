package com.ugg.admin.kit.content.domain.entities

/**
 * One section of texts as the editor holds it. Optional blocks (a quote, a highlight, a contact
 * card) are `null` when hidden in the client app. Titles may contain `\n` — the client app breaks
 * the line there.
 */
sealed interface ContentDraft {
    val section: ContentSection

    /** The first thing that stops saving, phrased for the master; `null` when all is fine. */
    val problem: String?
}

/** A numbered step: «01 · Раскрой». */
data class ContentStep(val number: String = "", val title: String = "", val description: String = "")

/** `content/home`. */
data class HomeContent(
    val heroKicker: String = "",
    val heroDescription: String = "",
    val stats: List<HomeStat> = emptyList(),
    val craftSteps: List<ContentStep> = emptyList(),
    val quote: HomeQuote? = null,
    val careCard: HomeCareCard? = null,
) : ContentDraft {
    override val section get() = ContentSection.Home

    override val problem: String?
        get() = when {
            stats.any { it.value.isBlank() } -> "У каждой цифры должно быть значение"
            craftSteps.any { it.title.isBlank() } -> "У каждого шага «Как рождается пара» должно быть название"
            quote != null && quote.text.isBlank() -> "Цитата включена, но пустая"
            careCard != null && careCard.title.isBlank() -> "Карточка ухода включена, но без заголовка"
            else -> null
        }

    companion object {
        val mock
            get() = HomeContent(
                heroKicker = "Единственная модель",
                heroDescription = "Овчина из хозяйств Прибайкалья, ручной шов и около шести часов работы мастера над каждой парой.",
                stats = listOf(
                    HomeStat("100%", "натуральная\nовчина"),
                    HomeStat("6 ч", "ручной работы\nнад парой"),
                    HomeStat("2", "точки\nна Байкале"),
                ),
                craftSteps = listOf(
                    ContentStep("01", "Выделка овчины", "Берём шкуры у хозяйств Прибайкалья и бережно выделываем мех."),
                    ContentStep("02", "Раскрой", "Каждую деталь кроим вручную, подбирая мех по густоте."),
                    ContentStep("03", "Ручной шов", "Прошиваем вощёной нитью — так шов служит годами."),
                    ContentStep("04", "Проверка", "Каждую пару примеряем на колодку и проверяем шов."),
                ),
                quote = HomeQuote(
                    text = "Каждую пару шьём так, будто для своей семьи — без спешки и без конвейера.",
                    author = "Имя мастера",
                    role = "основательница мастерской",
                ),
                careCard = HomeCareCard(kicker = "Уход", title = "Как сохранить угги тёплыми на годы", readMinutes = 3),
            )
    }
}

data class HomeStat(val value: String = "", val caption: String = "")

data class HomeQuote(val text: String = "", val author: String = "", val role: String = "")

/** The card on the home screen that leads to «Уход за уггами». */
data class HomeCareCard(val kicker: String = "", val title: String = "", val readMinutes: Int = 0)

/** `content/story`: «О мастерской». */
data class StoryContent(
    val kicker: String = "",
    val title: String = "",
    val intro: String = "",
    val stepsTitle: String = "",
    val steps: List<ContentStep> = emptyList(),
    val highlight: StoryHighlight? = null,
) : ContentDraft {
    override val section get() = ContentSection.Story

    override val problem: String?
        get() = when {
            title.isBlank() -> "Нужен заголовок экрана"
            steps.any { it.title.isBlank() } -> "У каждого шага должно быть название"
            highlight != null && highlight.title.isBlank() -> "Блок «Почему Байкал» включён, но без заголовка"
            else -> null
        }

    companion object {
        val mock
            get() = StoryContent(
                kicker = "О мастерской",
                title = "Сшито\nна Байкале",
                intro = "Мы не делаем коллекции. Мы делаем одну модель — и каждый год делаем её чуть лучше.",
                stepsTitle = "Путь одной пары",
                steps = listOf(
                    ContentStep("01", "Овчина", "Выбираем шкуры у хозяйств Прибайкалья."),
                    ContentStep("02", "Раскрой", "Мастер кроит детали вручную."),
                    ContentStep("03", "Пошив", "Около шести часов ручной работы."),
                    ContentStep("04", "Проверка", "Каждую пару надеваем на колодку и проверяем шов."),
                ),
                highlight = StoryHighlight(
                    kicker = "Почему Байкал",
                    title = "Здесь зима длится полгода — и мы знаем о тепле всё",
                    body = "Мы носим эти угги сами: на льду озера, в −30° и в весеннюю слякоть.",
                ),
            )
    }
}

data class StoryHighlight(val kicker: String = "", val title: String = "", val body: String = "")

/** `content/care`: «Уход за уггами». */
data class CareContent(
    val kicker: String = "",
    val title: String = "",
    val intro: String = "",
    val readMinutes: Int = 0,
    val tips: List<CareTip> = emptyList(),
    val dontsTitle: String = "",
    val donts: List<String> = emptyList(),
    val contact: CareContact? = null,
) : ContentDraft {
    override val section get() = ContentSection.Care

    override val problem: String?
        get() = when {
            title.isBlank() -> "Нужен заголовок экрана"
            tips.any { it.title.isBlank() } -> "У каждого совета должно быть название"
            donts.any { it.isBlank() } -> "Уберите пустые пункты из «Чего не делать»"
            contact != null && contact.title.isBlank() -> "Карточка «Написать мастеру» включена, но без заголовка"
            else -> null
        }

    companion object {
        val mock
            get() = CareContent(
                kicker = "Уход",
                title = "Как сохранить угги\nтёплыми на годы",
                intro = "Овчина — живой материал: она греет, дышит и служит много зим, если о ней немного позаботиться.",
                readMinutes = 3,
                tips = listOf(
                    CareTip(CareTip.Kind.Snow, "После снега", "Стряхните снег, как только зашли в дом."),
                    CareTip(CareTip.Kind.Drying, "Сушка", "Сушите при комнатной температуре, набив носок бумагой."),
                    CareTip(CareTip.Kind.Storage, "Хранение летом", "Почистите, просушите и уберите в льняной мешок."),
                ),
                dontsTitle = "Чего не делать",
                donts = listOf("Стирать в машине и замачивать", "Сушить у батареи, феном или на солнце"),
                contact = CareContact(
                    title = "Разошёлся шов или нужен совет?",
                    body = "Напишите мастеру — подскажем по уходу, а шов в течение 12 месяцев починим бесплатно.",
                    button = "Написать в мастерскую",
                ),
            )
    }
}

/** A tip; the client app picks the icon from [kind]. */
data class CareTip(val kind: Kind = Kind.Other, val title: String = "", val body: String = "") {
    enum class Kind(val id: String) {
        Snow("snow"),
        Drying("drying"),
        Salt("salt"),
        Cleaning("cleaning"),
        Protection("protection"),
        Storage("storage"),
        Other("other"),
        ;

        companion object {
            fun of(id: String) = entries.firstOrNull { it.id == id } ?: Other
        }
    }
}

data class CareContact(val title: String = "", val body: String = "", val button: String = "")

/** `content/onboarding`: the slides on first launch. */
data class OnboardingContent(
    val slides: List<OnboardingSlide> = emptyList(),
) : ContentDraft {
    override val section get() = ContentSection.Onboarding

    override val problem: String?
        get() = when {
            slides.isEmpty() -> "Нужен хотя бы один слайд"
            slides.any { it.title.isBlank() } -> "У каждого слайда должен быть заголовок"
            else -> null
        }

    companion object {
        val mock
            get() = OnboardingContent(
                slides = listOf(
                    OnboardingSlide("workshop", "Мастерская на Байкале", "Тепло, сшитое\nруками", "Одна модель, доведённая до совершенства."),
                    OnboardingSlide("made-to-order", "Под заказ", "Ваша пара —\nот мерки до шва", "Мастер сошьёт пару за 5–7 дней."),
                    OnboardingSlide("stores", "Две точки на Байкале", "Приходите\nпримерить", "В Иркутске и Листвянке можно примерить угги."),
                ),
            )
    }
}

/** [id] stays fixed: the client app remembers slides by it. */
data class OnboardingSlide(val id: String = "", val overline: String = "", val title: String = "", val description: String = "")

/** `content/checkout`: extras, deliveries and the gift wrap offered in the cart. */
data class CheckoutContent(
    val extras: List<CheckoutExtra> = emptyList(),
    val deliveries: List<CheckoutDelivery> = emptyList(),
    val giftWrap: CheckoutGiftWrap = CheckoutGiftWrap(),
) : ContentDraft {
    override val section get() = ContentSection.Checkout

    override val problem: String?
        get() = when {
            extras.any { it.title.isBlank() } -> "У каждого дополнения должно быть название"
            deliveries.any { it.title.isBlank() } -> "У каждого способа доставки должно быть название"
            giftWrap.title.isBlank() -> "Нужно название подарочной упаковки"
            else -> null
        }

    companion object {
        val mock
            get() = CheckoutContent(
                extras = listOf(
                    CheckoutExtra("care-cream", "Крем для овчины", "уход", 890),
                    CheckoutExtra("felt-insoles", "Стельки из войлока", "пара", 690),
                    CheckoutExtra("linen-bag", "Льняной мешок", "для хранения", 490),
                ),
                deliveries = listOf(
                    CheckoutDelivery("pickup", "Самовывоз из мастерской", "Иркутск, ул. Примерная, 12 · когда пара будет готова", 0, false, "irkutsk"),
                    CheckoutDelivery("cdek", "Доставка СДЭК", "3–5 дней после пошива", 450, true, null),
                    CheckoutDelivery("courier", "Курьер по Иркутску", "в удобный вам день", 350, false, null),
                ),
                giftWrap = CheckoutGiftWrap("Подарочная упаковка", "льняной мешок и открытка от мастера", 0),
            )
    }
}

/** [id] is written into orders (`extraId`), so it never changes once created. */
data class CheckoutExtra(val id: String = "", val title: String = "", val caption: String = "", val price: Int = 0)

/**
 * The client app's checkout logic keys on [id] (`pickup`, `cdek`, `courier`) and [storeId], so the
 * editor changes texts and prices only — no adding or removing.
 */
data class CheckoutDelivery(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int = 0,
    /** «от 450 ₽». */
    val isFromPrice: Boolean = false,
    val storeId: String? = null,
)

data class CheckoutGiftWrap(val title: String = "", val description: String = "", val price: Int = 0)
