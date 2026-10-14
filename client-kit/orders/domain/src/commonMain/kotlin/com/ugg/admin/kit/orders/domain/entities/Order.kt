package com.ugg.admin.kit.orders.domain.entities

/**
 * An order the way the workshop sees it: the list card and the order screen read the same entity.
 * Money, phones and dates arrive formatted; only [readyAt] stays raw because the master edits it.
 */
data class Order(
    val id: String = "",
    /** «№ 1027». */
    val number: Long = 0,
    val status: OrderStatus = OrderStatus.Accepted,
    /** «сегодня, 11:40». */
    val createdAt: String = "",
    val customer: OrderCustomer = OrderCustomer(),
    /** The pair of uggs; `null` for an order of extras only. */
    val boots: OrderBoots? = null,
    /** Cream, insoles, gift wrap — everything besides the boots. */
    val extras: List<OrderLine> = emptyList(),
    /** One line for the list: «Каштан · 38 · Стандарт + крем для овчины». */
    val itemsSummary: String = "",
    val delivery: OrderDelivery = OrderDelivery(),
    /** «15 790 ₽». */
    val total: String = "",
    /** When the pair will be ready, epoch milliseconds (UTC midnight of the day); `null` until set. */
    val readyAt: Long? = null,
    /** Visible to the workshop only. */
    val note: String = "",
    /** Process photos shown to the customer. */
    val photos: List<String> = emptyList(),
) {
    companion object {
        val mock get() = mocks.first()

        /** 3 new, 5 in work, 2 ready, plus closed ones — the numbers from the design. */
        val mocks: List<Order>
            get() = listOf(
                Order(
                    id = "o1027", number = 1027, status = OrderStatus.Accepted, createdAt = "сегодня, 11:40",
                    customer = OrderCustomer("Анна Королёва", "+7 900 123-45-67", "+79001234567", "размер 38 стандарт · стопа 24,5 см"),
                    boots = OrderBoots("Классика", 1, "Каштан · 38 · Стандарт", 0xFFB98A5E, "14 900 ₽"),
                    extras = listOf(OrderLine("Крем для овчины × 1", "890 ₽"), OrderLine("Подарочная упаковка", "0 ₽")),
                    itemsSummary = "Каштан · 38 · Стандарт + крем для овчины",
                    delivery = OrderDelivery("Самовывоз, Иркутск", "Самовывоз · Иркутск, ул. Примерная, 12"),
                    total = "15 790 ₽",
                    note = "Мех погуще на пятку, клиентка мёрзнет",
                ),
                Order(
                    id = "o1026", number = 1026, status = OrderStatus.Accepted, createdAt = "сегодня, 09:12",
                    customer = OrderCustomer("Ольга Смирнова", "+7 914 555-12-30", "+79145551230", "размер 37 высокие · стопа 23,5 см"),
                    boots = OrderBoots("Классика", 1, "Песок · 37 · Высокие", 0xFFD9C3A2, "15 900 ₽"),
                    itemsSummary = "Песок · 37 · Высокие",
                    delivery = OrderDelivery("СДЭК, Новосибирск", "СДЭК · Новосибирск, ул. Ленина, 40, кв. 12"),
                    total = "15 350 ₽",
                ),
                Order(
                    id = "o1025", number = 1025, status = OrderStatus.Accepted, createdAt = "вчера, 21:05",
                    customer = OrderCustomer("Без имени", "+7 950 101-20-30", "+79501012030", "размер не указан"),
                    boots = OrderBoots("Классика", 1, "Графит · 41 · Стандарт", 0xFF5B5550, "14 900 ₽"),
                    itemsSummary = "Графит · 41 · Стандарт",
                    delivery = OrderDelivery("Самовывоз, Листвянка", "Самовывоз · Листвянка, ул. Береговая, 3"),
                    total = "14 900 ₽",
                ),
                Order(
                    id = "o1024", number = 1024, status = OrderStatus.Sewing, createdAt = "10 октября, 15:20",
                    customer = OrderCustomer("Ирина Морозова", "+7 902 765-00-11", "+79027650011", "размер 39 стандарт · стопа 25 см"),
                    boots = OrderBoots("Классика", 1, "Шоколад · 39 · Стандарт", 0xFF5C3D2E, "14 900 ₽"),
                    itemsSummary = "Шоколад · 39 · Стандарт",
                    delivery = OrderDelivery("Курьер по Иркутску", "Курьер · Иркутск, ул. Карла Маркса, 8, кв. 3"),
                    total = "14 900 ₽",
                    readyAt = 1792281600000, // 18 октября
                ),
                Order(
                    id = "o1023", number = 1023, status = OrderStatus.Cutting, createdAt = "10 октября, 11:02",
                    customer = OrderCustomer("Мария Белова", "+7 924 300-40-50", "+79243004050", "размер 36 стандарт"),
                    boots = OrderBoots("Классика", 1, "Каштан · 36 · Стандарт", 0xFFB98A5E, "14 900 ₽"),
                    extras = listOf(OrderLine("Стельки из овчины × 1", "650 ₽")),
                    itemsSummary = "Каштан · 36 · Стандарт + стельки",
                    delivery = OrderDelivery("Самовывоз, Иркутск", "Самовывоз · Иркутск, ул. Примерная, 12"),
                    total = "15 550 ₽",
                    readyAt = 1792540800000, // 21 октября
                ),
                Order(
                    id = "o1022", number = 1022, status = OrderStatus.Sewing, createdAt = "9 октября, 18:45",
                    customer = OrderCustomer("Елена Котова", "+7 908 222-33-44", "+79082223344", "размер 40 высокие"),
                    boots = OrderBoots("Классика", 1, "Графит · 40 · Высокие", 0xFF5B5550, "15 900 ₽"),
                    itemsSummary = "Графит · 40 · Высокие",
                    delivery = OrderDelivery("СДЭК, Красноярск", "СДЭК · Красноярск, пр. Мира, 15"),
                    total = "16 350 ₽",
                    readyAt = 1792195200000, // 17 октября
                ),
                Order(
                    id = "o1021", number = 1021, status = OrderStatus.Cutting, createdAt = "9 октября, 10:30",
                    customer = OrderCustomer("Светлана Ким", "+7 983 410-77-88", "+79834107788", "размер 38 стандарт"),
                    boots = OrderBoots("Классика", 2, "Песок · 38 · Стандарт", 0xFFD9C3A2, "29 800 ₽"),
                    itemsSummary = "Песок · 38 · Стандарт × 2",
                    delivery = OrderDelivery("Курьер по Иркутску", "Курьер · Иркутск, ул. Ленина, 5"),
                    total = "29 800 ₽",
                    readyAt = 1792627200000, // 22 октября
                ),
                Order(
                    id = "o1020", number = 1020, status = OrderStatus.Sewing, createdAt = "8 октября, 14:10",
                    customer = OrderCustomer("Татьяна Попова", "+7 914 600-10-20", "+79146001020", "размер 37 стандарт"),
                    boots = OrderBoots("Классика", 1, "Шоколад · 37 · Стандарт", 0xFF5C3D2E, "14 900 ₽"),
                    itemsSummary = "Шоколад · 37 · Стандарт",
                    delivery = OrderDelivery("Самовывоз, Листвянка", "Самовывоз · Листвянка, ул. Береговая, 3"),
                    total = "14 900 ₽",
                    readyAt = 1792108800000, // 16 октября
                ),
                Order(
                    id = "o1019", number = 1019, status = OrderStatus.Ready, createdAt = "6 октября, 12:00",
                    customer = OrderCustomer("Наталья Орлова", "+7 902 111-22-33", "+79021112233", "размер 39 высокие"),
                    boots = OrderBoots("Классика", 1, "Каштан · 39 · Высокие", 0xFFB98A5E, "15 900 ₽"),
                    itemsSummary = "Каштан · 39 · Высокие",
                    delivery = OrderDelivery("Самовывоз, Иркутск", "Самовывоз · Иркутск, ул. Примерная, 12"),
                    total = "15 900 ₽",
                    readyAt = 1792022400000, // 15 октября
                ),
                Order(
                    id = "o1018", number = 1018, status = OrderStatus.Ready, createdAt = "5 октября, 17:25",
                    customer = OrderCustomer("Ксения Волкова", "+7 950 777-66-55", "+79507776655", "размер 36 стандарт"),
                    boots = OrderBoots("Классика", 1, "Графит · 36 · Стандарт", 0xFF5B5550, "14 900 ₽"),
                    extras = listOf(OrderLine("Крем для овчины × 1", "890 ₽")),
                    itemsSummary = "Графит · 36 · Стандарт + крем для овчины",
                    delivery = OrderDelivery("Курьер по Иркутску", "Курьер · Иркутск, ул. Байкальская, 105"),
                    total = "16 090 ₽",
                    readyAt = 1791936000000, // 14 октября
                ),
                Order(
                    id = "o1017", number = 1017, status = OrderStatus.Completed, createdAt = "1 октября, 13:40",
                    customer = OrderCustomer("Ирина Морозова", "+7 902 765-00-11", "+79027650011", "размер 39 стандарт · стопа 25 см"),
                    boots = OrderBoots("Классика", 1, "Песок · 39 · Стандарт", 0xFFD9C3A2, "14 900 ₽"),
                    itemsSummary = "Песок · 39 · Стандарт",
                    delivery = OrderDelivery("Самовывоз, Иркутск", "Самовывоз · Иркутск, ул. Примерная, 12"),
                    total = "14 900 ₽",
                ),
                Order(
                    id = "o1016", number = 1016, status = OrderStatus.Cancelled, createdAt = "30 сентября, 09:50",
                    customer = OrderCustomer("Анастасия Лебедева", "+7 924 909-08-07", "+79249090807", "размер 38 стандарт"),
                    boots = OrderBoots("Классика", 1, "Каштан · 38 · Стандарт", 0xFFB98A5E, "14 900 ₽"),
                    itemsSummary = "Каштан · 38 · Стандарт",
                    delivery = OrderDelivery("СДЭК, Улан-Удэ", "СДЭК · Улан-Удэ, ул. Ленина, 20"),
                    total = "15 350 ₽",
                ),
            )
    }
}

data class OrderCustomer(
    val name: String = "",
    /** «+7 900 123-45-67». */
    val phone: String = "",
    /** `+79001234567`, for «Позвонить» and «Написать». */
    val phoneDial: String = "",
    /** «размер 38 стандарт · стопа 24,5 см». */
    val size: String = "",
)

data class OrderBoots(
    val title: String = "",
    val quantity: Int = 1,
    /** «Каштан · 38 · Стандарт». */
    val options: String = "",
    /** ARGB of the shade, for the swatch. */
    val color: Long = 0xFFB98A5E,
    val price: String = "",
)

/** «Крем для овчины × 1» — «890 ₽». */
data class OrderLine(
    val title: String = "",
    val price: String = "",
)

data class OrderDelivery(
    /** For the list: «Самовывоз, Иркутск». */
    val short: String = "",
    /** For the order: «Самовывоз · Иркутск, ул. Примерная, 12». */
    val full: String = "",
)
