package com.example.simplecallerid.models

import androidx.room.*
import com.google.gson.Gson

@Entity(tableName = "user_table", primaryKeys = ["first_name", "last_name"])
data class User(
    @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "last_name") var lastName: String,
    @ColumnInfo(name = "phone_number") var phoneNumber: Phone
) {

    var fullName: String = "$firstName $lastName"

    fun prettyPrintPhone(): String {
        return "${phoneNumber.type.label}: ${phoneNumber.formatted}"
    }

    fun hasPhone(phoneNumber: String): Boolean {
        return this.phoneNumber.match(phoneNumber)
    }

    override fun equals(other: Any?): Boolean {
        val that = other as? User ?: return false
        return this.firstName == that.firstName
                && this.lastName == that.lastName
                && this.phoneNumber == that.phoneNumber
    }

    override fun hashCode(): Int = super.hashCode()

    class PhoneListConverter {
        @TypeConverter
        fun phoneToJson(phone: Phone): String = Gson().toJson(phone)

        @TypeConverter
        fun phoneFromJson(phone: String): Phone = Gson().fromJson(phone, Phone::class.java)
    }
}