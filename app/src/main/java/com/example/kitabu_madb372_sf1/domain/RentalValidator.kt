package com.example.kitabu_madb372_sf1.domain

object RentalValidator {

    const val MAX_NAME_LENGTH = 40

    fun sanitizeUserName(raw: String): String {
        return raw
            .trim()
            .replace(Regex("\\s+"), " ")
            .take(MAX_NAME_LENGTH)
    }

    fun isValidUserName(userName: String): Boolean {
        return sanitizeUserName(userName).length >= 2
    }

    fun isValidDuration(durationDays: Int): Boolean {
        return durationDays > 0
    }

    fun isValidDeadline(
        bookingDate: Long,
        returnDeadline: Long
    ): Boolean {
        return returnDeadline > bookingDate
    }

    fun sanitizeSearchQuery(raw: String): String {
        return raw
            .trim()
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }
}