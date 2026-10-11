package com.example.kitabu_madb372_sf1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun bookingDao(): BookingDao

    companion object {

        // Use one database instance.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(DatabaseCallback())
                    .build()
                    .also { database ->
                        INSTANCE = database
                    }
            }
        }
    }

    private class DatabaseCallback : Callback() {

        // Adds sample books on first setup.
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->

                // Adds the books in the background.
                CoroutineScope(Dispatchers.IO).launch {

                    database.bookDao().insertBooks(
                        listOf(
                            BookEntity(
                                title = "The Hobbit",
                                author = "J.R.R. Tolkien",
                                category = "Fantasy"
                            ),
                            BookEntity(
                                title = "1984",
                                author = "George Orwell",
                                category = "Fiction"
                            ),
                            BookEntity(
                                title = "Pride and Prejudice",
                                author = "Jane Austen",
                                category = "Fiction"
                            ),
                            BookEntity(
                                title = "Clean Code",
                                author = "Robert C. Martin",
                                category = "Technology"
                            ),
                            BookEntity(
                                title = "The Great Gatsby",
                                author = "F. Scott Fitzgerald",
                                category = "Fiction"
                            ),
                            BookEntity(
                                title = "A Brief History of Time",
                                author = "Stephen Hawking",
                                category = "Science"
                            ),
                            BookEntity(
                                title = "The Psychology of Money",
                                author = "Morgan Housel",
                                category = "Finance"
                            ),
                            BookEntity(
                                title = "Atomic Habits",
                                author = "James Clear",
                                category = "Self Development"
                            )
                        )
                    )
                }
            }
        }
    }
}