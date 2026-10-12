package com.example.kitabu_madb372_sf1

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kitabu_madb372_sf1.data.local.*
import com.example.kitabu_madb372_sf1.domain.RentalValidator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookingLifecycleTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        // Use a separate database for these tests.
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun addBook(title: String = "Test Book", author: String = "Test Author"): Int {
        return database.bookDao().insertBook(
            BookEntity(title = title, author = author, category = "Test")
        ).toInt()
    }

    private fun pending(bookId: Int) = BookingEntity(
        bookOwnerId = bookId,
        userName = "Ethan Peters",
        bookingDate = 100L,
        returnDeadline = 700L,
        status = BookingStatus.PENDING
    )

    @Test
    fun reserveConfirmRenewReturnAndCancel() = runBlocking {
        val bookId = addBook()
        val dao = database.bookingDao()
        val bookingId = dao.reserveBook(pending(bookId)).toInt()
        assertFalse(database.bookDao().getAllBooks().first().single().isAvailable)
        assertTrue(database.bookDao().getAvailableBooks().first().isEmpty())
        assertEquals(BookingStatus.PENDING, dao.getCurrentBookingsWithBook().first().single().status)
        dao.updateReturnDeadline(bookingId, 900L)
        assertEquals(700L, dao.getCurrentBookingsWithBook().first().single().returnDeadline)
        dao.updateBookingStatus(bookingId, BookingStatus.ACTIVE)
        dao.cancelPendingBooking(bookingId, bookId)
        assertEquals(BookingStatus.ACTIVE, dao.getCurrentBookingsWithBook().first().single().status)
        dao.updateReturnDeadline(bookingId, 900L)
        assertEquals(900L, dao.getCurrentBookingsWithBook().first().single().returnDeadline)
        dao.returnBook(bookingId, bookId)
        assertTrue(dao.getCurrentBookingsWithBook().first().isEmpty())
        assertTrue(database.bookDao().getAllBooks().first().single().isAvailable)
        database.openHelper.readableDatabase.query(
            "SELECT status FROM bookings WHERE bookingId = ?", arrayOf(bookingId)
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("RETURNED", cursor.getString(0))
        }
        val nextId = dao.reserveBook(pending(bookId)).toInt()
        // Check that an old return cannot undo a new booking.
        dao.returnBook(bookingId, bookId)
        assertFalse(database.bookDao().getAllBooks().first().single().isAvailable)
        dao.cancelPendingBooking(nextId, bookId)
        assertTrue(dao.getCurrentBookingsWithBook().first().isEmpty())
        assertTrue(database.bookDao().getAllBooks().first().single().isAvailable)
    }

    @Test
    fun simultaneousReservationsOnlyClaimOneBook() = runBlocking {
        val bookId = addBook()
        val results = coroutineScope {
            listOf(
                async { database.bookingDao().reserveBook(pending(bookId)) },
                async { database.bookingDao().reserveBook(pending(bookId)) }
            ).awaitAll()
        }
        assertEquals(1, results.count { it > 0L })
        assertEquals(1, results.count { it == 0L })
        assertEquals(1, database.bookingDao().getCurrentBookingsWithBook().first().size)
    }

    @Test
    fun searchTreatsWildcardsLiterallyAndDeletingBookCascades() = runBlocking {
        val id = addBook("100%_Book", "Special Author")
        addBook("Other Book", "Other Author")
        val books = database.bookDao()
        assertEquals(1, books.searchBooks(RentalValidator.sanitizeSearchQuery("%_")).first().size)
        assertEquals(id, books.searchBooks("Special").first().single().bookId)
        assertEquals(id, books.searchBooks("100").first().single().bookId)
        database.bookingDao().reserveBook(pending(id))
        books.deleteBook(books.getAllBooks().first().first { it.bookId == id })
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM bookings").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }
    }
}
