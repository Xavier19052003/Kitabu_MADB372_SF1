package com.example.kitabu_madb372_sf1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    // Gets the current bookings with book details.
    @Query(
        """
        SELECT
            bookings.bookingId AS bookingId,
            bookings.bookOwnerId AS bookOwnerId,
            bookings.userName AS userName,
            bookings.bookingDate AS bookingDate,
            bookings.returnDeadline AS returnDeadline,
            bookings.status AS status,
            books.title AS bookTitle,
            books.author AS bookAuthor,
            books.category AS bookCategory,
            books.isAvailable AS isAvailable
        FROM bookings
        INNER JOIN books
            ON bookings.bookOwnerId = books.bookId
        WHERE bookings.status = 'PENDING'
           OR bookings.status = 'ACTIVE'
        ORDER BY bookings.returnDeadline ASC
        """
    )
    fun getCurrentBookingsWithBook(): Flow<List<BookingWithBook>>

    @Query(
        """
        UPDATE bookings
        SET returnDeadline = :newDeadline
        WHERE bookingId = :bookingId AND status = 'ACTIVE'
        """
    )
    suspend fun updateReturnDeadline(
        bookingId: Int,
        newDeadline: Long
    )

    @Query(
        """
        UPDATE bookings
        SET status = :status
        WHERE bookingId = :bookingId
          AND ((status = 'PENDING' AND :status = 'ACTIVE')
            OR (status = 'ACTIVE' AND :status = 'RETURNED'))
        """
    )
    suspend fun updateBookingStatus(
        bookingId: Int,
        status: BookingStatus
    )

    @Query(
        """
        UPDATE books
        SET isAvailable = :isAvailable
        WHERE bookId = :bookId
        """
    )
    suspend fun updateBookAvailability(
        bookId: Int,
        isAvailable: Boolean
    )

    @Query(
        """
        DELETE FROM bookings
        WHERE bookingId = :bookingId
        AND bookOwnerId = :bookId AND status = 'PENDING'
        """
    )
    suspend fun deletePendingBooking(
        bookingId: Int,
        bookId: Int
    ): Int

    @Query("UPDATE books SET isAvailable = 0 WHERE bookId = :bookId AND isAvailable = 1")
    suspend fun claimAvailableBook(bookId: Int): Int

    @Query("UPDATE bookings SET status = 'RETURNED' WHERE bookingId = :bookingId AND bookOwnerId = :bookId AND status = 'ACTIVE'")
    suspend fun markActiveBookingReturned(bookingId: Int, bookId: Int): Int

    // Updates the booking and book together.
    @Transaction
    suspend fun returnBook(
        bookingId: Int,
        bookId: Int
    ) {
        // Only returns an active booking.
        if (markActiveBookingReturned(bookingId, bookId) > 0) {
            updateBookAvailability(
                bookId = bookId,
                isAvailable = true
            )
        }
    }

    // Makes sure the book is available after cancellation.
    @Transaction
    suspend fun cancelPendingBooking(
        bookingId: Int,
        bookId: Int
    ) {
        val deletedRows = deletePendingBooking(bookingId, bookId)

        if (deletedRows > 0) {
            updateBookAvailability(
                bookId = bookId,
                isAvailable = true
            )
        }
    }

    // Reserves the book in one single transaction.
    @Transaction
    suspend fun reserveBook(
        booking: BookingEntity
    ): Long {

        // Checks that the book is still available.
        if (claimAvailableBook(booking.bookOwnerId) == 0) return 0L

        return insertBooking(booking)
    }
}