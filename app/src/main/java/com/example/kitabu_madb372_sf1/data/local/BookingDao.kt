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
        WHERE bookingId = :bookingId
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
        AND status = 'PENDING'
        """
    )
    suspend fun deletePendingBooking(
        bookingId: Int
    ): Int

    @Transaction
    suspend fun returnBook(
        bookingId: Int,
        bookId: Int
    ) {
        updateBookingStatus(
            bookingId = bookingId,
            status = BookingStatus.RETURNED
        )

        updateBookAvailability(
            bookId = bookId,
            isAvailable = true
        )
    }

    @Transaction
    suspend fun cancelPendingBooking(
        bookingId: Int,
        bookId: Int
    ) {
        val deletedRows = deletePendingBooking(bookingId)

        if (deletedRows > 0) {
            updateBookAvailability(
                bookId = bookId,
                isAvailable = true
            )
        }
    }

    @Transaction
    suspend fun reserveBook(
        booking: BookingEntity
    ): Long {

        updateBookAvailability(
            bookId = booking.bookOwnerId,
            isAvailable = false
        )

        return insertBooking(booking)
    }
}