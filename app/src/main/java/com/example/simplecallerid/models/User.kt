package com.example.simplecallerid.models

import androidx.room.*
import android.telephony.PhoneNumberUtils

@Entity(tableName = "user_table")
data class User(
    @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "last_name") var lastName: String,
    @PrimaryKey @ColumnInfo(name = "phone_number") var phoneNumber: String,
    @ColumnInfo(name = "phone_type") var phoneType: PhoneType
) {
    var fullName: String = "$firstName $lastName"
    var prettyPrint: String = "${phoneType.label}: $phoneNumber"
    var phoneLabel: String = "Simple Caller ID App | ${phoneType.label}"

    fun hasPhone(phoneNumber: String): Boolean = PhoneNumberUtils.compare(this.phoneNumber, phoneNumber)

    override fun equals(other: Any?): Boolean {
        val that = other as? User ?: return false
        return this.firstName == that.firstName
                && this.lastName == that.lastName
                && this.phoneNumber == that.phoneNumber
                && this.phoneType == that.phoneType
    }

    override fun hashCode(): Int = super.hashCode()

    class PhoneTypeConverter {
        @TypeConverter
        fun toString(type: PhoneType) = type.label

        @TypeConverter
        fun toType(type: String) = PhoneType.parse(type)
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