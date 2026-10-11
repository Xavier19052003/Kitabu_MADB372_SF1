package com.example.kitabu_madb372_sf1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitabu_madb372_sf1.data.local.BookEntity
import com.example.kitabu_madb372_sf1.data.local.BookingEntity
import com.example.kitabu_madb372_sf1.data.local.BookingStatus
import com.example.kitabu_madb372_sf1.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.kitabu_madb372_sf1.domain.RentalValidator


@OptIn(ExperimentalCoroutinesApi::class)

class CatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    val books: StateFlow<List<BookEntity>> =
        _searchQuery
            // Uses the latest search text.
            .flatMapLatest { query ->
                val cleanQuery =
                    RentalValidator.sanitizeSearchQuery(query)

                if (cleanQuery.isBlank()) {
                    repository.getAllBooks()
                } else {
                    repository.searchBooks(cleanQuery)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun reserveBook(
        book: BookEntity,
        userName: String,
        durationDays: Int
    ) {
        val cleanUserName =
            RentalValidator.sanitizeUserName(userName)

        if (!RentalValidator.isValidUserName(cleanUserName)) return
        if (!RentalValidator.isValidDuration(durationDays)) return
        if (!book.isAvailable) return

        val bookingDate = System.currentTimeMillis()

        val returnDeadline =
            bookingDate + TimeUnit.DAYS.toMillis(durationDays.toLong())

        if (!RentalValidator.isValidDeadline(
                bookingDate = bookingDate,
                returnDeadline = returnDeadline
            )
        ) {
            return
        }

        // Starts the booking as pending.
        val booking = BookingEntity(
            bookOwnerId = book.bookId,
            userName = cleanUserName,
            bookingDate = bookingDate,
            returnDeadline = returnDeadline,
            status = BookingStatus.PENDING
        )

        // Saves the booking without blocking the screen.
        viewModelScope.launch {
            repository.reserveBook(booking)
        }
    }
}