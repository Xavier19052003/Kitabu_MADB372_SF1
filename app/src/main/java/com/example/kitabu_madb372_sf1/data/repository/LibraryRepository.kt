package com.example.kitabu_madb372_sf1.data.repository

import com.example.kitabu_madb372_sf1.data.local.BookDao
import com.example.kitabu_madb372_sf1.data.local.BookEntity
import com.example.kitabu_madb372_sf1.data.local.BookingDao
import com.example.kitabu_madb372_sf1.data.local.BookingEntity
import com.example.kitabu_madb372_sf1.data.local.BookingStatus
import com.example.kitabu_madb372_sf1.data.local.BookingWithBook
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) {

    fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }

    fun getAvailableBooks(): Flow<List<BookEntity>> {
        return bookDao.getAvailableBooks()
    }

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.searchBooks(query)
    }

    fun getCurrentBookings(): Flow<List<BookingWithBook>> {
        return bookingDao.getCurrentBookingsWithBook()
    }

    suspend fun insertBook(book: BookEntity): Long {
        return bookDao.insertBook(book)
    }

    suspend fun updateBook(book: BookEntity) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun updateBookAvailability(
        bookId: Int,
        isAvailable: Boolean
    ) {
        bookDao.updateAvailability(
            bookId = bookId,
            isAvailable = isAvailable
        )
    }

    suspend fun reserveBook(booking: BookingEntity): Long {
        return bookingDao.reserveBook(booking)
    }

    suspend fun renewBooking(
        bookingId: Int,
        newDeadline: Long
    ) {
        bookingDao.updateReturnDeadline(
            bookingId = bookingId,
            newDeadline = newDeadline
        )
    }

    suspend fun returnBook(
        bookingId: Int,
        bookId: Int
    ) {
        bookingDao.returnBook(
            bookingId = bookingId,
            bookId = bookId
        )
    }

    suspend fun cancelPendingBooking(
        bookingId: Int,
        bookId: Int
    ) {
        bookingDao.cancelPendingBooking(
            bookingId = bookingId,
            bookId = bookId
        )
    }

    suspend fun updateBookingStatus(
        bookingId: Int,
        status: BookingStatus
    ) {
        bookingDao.updateBookingStatus(
            bookingId = bookingId,
            status = status
        )
    }
}