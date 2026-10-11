package com.example.kitabu_madb372_sf1.domain

object RentalValidator {

    const val MAX_NAME_LENGTH = 40

    // Removes the extra spaces and limit the name length.
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
        return durationDays in listOf(7, 14, 21)
    }

    fun isValidDeadline(
        bookingDate: Long,
        returnDeadline: Long
    ): Boolean {
        return returnDeadline > bookingDate
    }

    // Treats the search symbols as normal text.
    fun sanitizeSearchQuery(raw: String): String {
        return raw
            .trim()
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }
}