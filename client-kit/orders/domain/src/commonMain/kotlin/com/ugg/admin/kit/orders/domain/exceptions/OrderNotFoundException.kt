package com.ugg.admin.kit.orders.domain.exceptions

/** The order was deleted or never existed — e.g. a stale link after a reinstall. */
class OrderNotFoundException : Exception("Заказ не найден")
