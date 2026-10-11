package com.example.kitabu_madb372_sf1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitabu_madb372_sf1.data.local.BookingStatus
import com.example.kitabu_madb372_sf1.data.local.BookingWithBook
import com.example.kitabu_madb372_sf1.data.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.example.kitabu_madb372_sf1.domain.RentalValidator

class DashboardViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    val bookings: StateFlow<List<BookingWithBook>> =
        repository.getCurrentBookings()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Only confirms the pending bookings.
    fun confirmPickup(booking: BookingWithBook) {
        if (booking.status != BookingStatus.PENDING) {
            return
        }

        viewModelScope.launch {
            repository.updateBookingStatus(
                bookingId = booking.bookingId,
                status = BookingStatus.ACTIVE
            )
        }
    }

    fun renewBooking(
        booking: BookingWithBook,
        extraDays: Int
    ) {
        if (booking.status != BookingStatus.ACTIVE) {
            return
        }

        if (!RentalValidator.isValidDuration(extraDays)) {
            return
        }

        // Uses today as if the deadline has passed.
        val baseDeadline = maxOf(
            booking.returnDeadline,
            System.currentTimeMillis()
        )

        val newDeadline =
            baseDeadline + TimeUnit.DAYS.toMillis(
                extraDays.toLong()
            )

        if (!RentalValidator.isValidDeadline(
                bookingDate = baseDeadline,
                returnDeadline = newDeadline
            )
        ) {
            return
        }

        viewModelScope.launch {
            repository.renewBooking(
                bookingId = booking.bookingId,
                newDeadline = newDeadline
            )
        }
    }

    // Returns the book and makes it available.
    fun returnBook(booking: BookingWithBook) {
        if (booking.status != BookingStatus.ACTIVE) {
            return
        }

        viewModelScope.launch {
            repository.returnBook(
                bookingId = booking.bookingId,
                bookId = booking.bookOwnerId
            )
        }
    }

    // Cancels a booking before pickup.
    fun cancelBooking(booking: BookingWithBook) {
        if (booking.status != BookingStatus.PENDING) {
            return
        }

        viewModelScope.launch {
            repository.cancelPendingBooking(
                bookingId = booking.bookingId,
                bookId = booking.bookOwnerId
            )
        }
    }
}
