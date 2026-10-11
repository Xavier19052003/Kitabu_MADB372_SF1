package com.example.kitabu_madb372_sf1.data.local

import androidx.room.TypeConverter

// Converts the booking states to and from text.
class Converters {

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus {
        return BookingStatus.valueOf(value)
    }
}