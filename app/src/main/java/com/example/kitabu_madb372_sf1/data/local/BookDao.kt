package com.example.kitabu_madb372_sf1.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query(
        """
        SELECT * FROM books
        WHERE isAvailable = 1
        ORDER BY title ASC
        """
    )
    fun getAvailableBooks(): Flow<List<BookEntity>>

    @Query(
        """
    SELECT * FROM books
    WHERE title LIKE '%' || :query || '%' ESCAPE '\'
       OR author LIKE '%' || :query || '%' ESCAPE '\'
    ORDER BY title ASC
    """
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query(
        """
        UPDATE books
        SET isAvailable = :isAvailable
        WHERE bookId = :bookId
        """
    )
    suspend fun updateAvailability(
        bookId: Int,
        isAvailable: Boolean
    )

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)
}