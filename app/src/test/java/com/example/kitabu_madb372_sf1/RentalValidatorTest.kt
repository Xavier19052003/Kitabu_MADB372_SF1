package com.example.kitabu_madb372_sf1

import com.example.kitabu_madb372_sf1.domain.RentalValidator
import org.junit.Assert.*
import org.junit.Test

class RentalValidatorTest {
    @Test
    fun onlyOfferedDurationsAreAccepted() {
        listOf(7, 14, 21).forEach { assertTrue(RentalValidator.isValidDuration(it)) }
        listOf(-7, 0, 1, 8, 30, Int.MAX_VALUE).forEach {
            assertFalse(RentalValidator.isValidDuration(it))
        }
    }

    @Test
    fun namesAreTrimmedAndLimited() {
        assertEquals("Ethan Peters", RentalValidator.sanitizeUserName("  Ethan   Peters  "))
        assertFalse(RentalValidator.isValidUserName("  "))
        assertFalse(RentalValidator.isValidUserName(" A "))
        assertEquals(40, RentalValidator.sanitizeUserName("A".repeat(60)).length)
    }

    @Test
    fun searchWildcardsAreEscapedAndDeadlinesMustIncrease() {
        assertEquals("100\\%\\_\\\\", RentalValidator.sanitizeSearchQuery(" 100%_\\ "))
        assertFalse(RentalValidator.isValidDeadline(100L, 99L))
        assertFalse(RentalValidator.isValidDeadline(100L, 100L))
        assertTrue(RentalValidator.isValidDeadline(100L, 101L))
    }
}
