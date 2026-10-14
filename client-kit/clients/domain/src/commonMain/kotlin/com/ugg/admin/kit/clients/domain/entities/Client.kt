package com.ugg.admin.kit.clients.domain.entities

/**
 * A customer: `users/{uid}` with their orders counted. Everyone who signed in with a phone is
 * here, with or without an order.
 */
data class Client(
    val id: String = "",
    /** `null` until they fill in the name after the first sign-in. */
    val name: String? = null,
    /** «+7 900 123-45-67». */
    val phone: String = "",
    /** `+79001234567`, for search by digits. */
    val phoneDial: String = "",
    /** «38 стандарт»; `null` when not given. */
    val size: String? = null,
    val ordersCount: Int = 0,
    /** The order the workshop is busy with now, if any. */
    val activeOrder: ClientOrder? = null,
    /** The latest order of any status, to open from the list. */
    val lastOrderId: String? = null,
    /** «вчера», «12 сентября». */
    val registeredAt: String = "",
) {
    companion object {
        /** The same people as in the orders mocks, so a tap leads to their order. */
        val mocks: List<Client>
            get() = listOf(
                Client("u1", "Анна Королёва", "+7 900 123-45-67", "+79001234567", "38 стандарт", 1, ClientOrder("o1027", 1027, "новый"), "o1027", "сегодня"),
                Client("u2", "Ольга Смирнова", "+7 914 555-12-30", "+79145551230", "37 высокие", 1, ClientOrder("o1026", 1026, "новый"), "o1026", "сегодня"),
                Client("u3", null, "+7 950 101-20-30", "+79501012030", null, 1, ClientOrder("o1025", 1025, "новый"), "o1025", "вчера"),
                Client("u4", "Ирина Морозова", "+7 902 765-00-11", "+79027650011", "39 стандарт", 2, ClientOrder("o1024", 1024, "пошив"), "o1024", "28 сентября"),
                Client("u5", "Мария Белова", "+7 924 300-40-50", "+79243004050", "36 стандарт", 1, ClientOrder("o1023", 1023, "раскрой"), "o1023", "10 октября"),
                Client("u6", "Елена Котова", "+7 908 222-33-44", "+79082223344", "40 высокие", 1, ClientOrder("o1022", 1022, "пошив"), "o1022", "9 октября"),
                Client("u7", "Светлана Ким", "+7 983 410-77-88", "+79834107788", "38 стандарт", 1, ClientOrder("o1021", 1021, "раскрой"), "o1021", "9 октября"),
                Client("u8", "Татьяна Попова", "+7 914 600-10-20", "+79146001020", "37 стандарт", 1, ClientOrder("o1020", 1020, "пошив"), "o1020", "8 октября"),
                Client("u9", "Наталья Орлова", "+7 902 111-22-33", "+79021112233", "39 высокие", 1, ClientOrder("o1019", 1019, "готов"), "o1019", "6 октября"),
                Client("u10", "Ксения Волкова", "+7 950 777-66-55", "+79507776655", "36 стандарт", 1, ClientOrder("o1018", 1018, "готов"), "o1018", "5 октября"),
                Client("u11", "Анастасия Лебедева", "+7 924 909-08-07", "+79249090807", "38 стандарт", 1, null, "o1016", "30 сентября"),
                Client("u12", "Дарья Соколова", "+7 914 321-00-99", "+79143210099", "37 стандарт", 0, null, null, "вчера"),
                Client("u13", null, "+7 950 222-33-44", "+79502223344", null, 0, null, null, "сегодня"),
            )
    }
}

/** «№ 1027 · новый». */
data class ClientOrder(
    val orderId: String = "",
    val number: Long = 0,
    /** Lowercase stage: «новый», «раскрой», «пошив», «готов». */
    val status: String = "",
)
