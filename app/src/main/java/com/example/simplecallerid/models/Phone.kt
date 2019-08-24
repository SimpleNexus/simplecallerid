package com.example.simplecallerid.models

import android.telephony.PhoneNumberUtils

class Phone(
    var original: String,
    var type: PhoneType
) {
    var formatted: String = PhoneNumberUtils.formatNumber(original, "US")

    fun match(phoneNumber: String): Boolean {
        return PhoneNumberUtils.compare(phoneNumber, original)
                || PhoneNumberUtils.compare(phoneNumber, formatted)
    }
}

enum class PhoneType(var label: String) {
    HOME_PHONE("Home"),
    CELL_PHONE("Cell"),
    WORK_PHONE("Work");

    companion object {
        fun parse(type: String) = values().firstOrNull { it.label == type }
            ?: HOME_PHONE
    }
}