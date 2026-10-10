package com.example.kitabu_madb372_sf1.data.local

data class BookingWithBook(
    val bookingId: Int,
    val bookOwnerId: Int,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus,
    val bookTitle: String,
    val bookAuthor: String,
    val bookCategory: String,
    val isAvailable: Boolean
)