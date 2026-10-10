package com.example.kitabu_madb372_sf1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu_madb372_sf1.data.local.BookEntity
import com.example.kitabu_madb372_sf1.ui.viewmodel.CatalogViewModel
import com.example.kitabu_madb372_sf1.ui.theme.KitabuLavender
import com.example.kitabu_madb372_sf1.ui.theme.KitabuPurple
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.kitabu_madb372_sf1.R
import com.example.kitabu_madb372_sf1.ui.theme.KitabuBackground
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.kitabu_madb372_sf1.domain.RentalValidator

@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    modifier: Modifier = Modifier
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    CatalogContent(
        books = books,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onReserveBook = viewModel::reserveBook,
        modifier = modifier
    )
}

@Composable
private fun CatalogContent(
    books: List<BookEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onReserveBook: (BookEntity, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBook by remember {
        mutableStateOf<BookEntity?>(null)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.books_pattern_bg
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.32f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp)
        ) {

            Text(
                text = "Kitabu",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = KitabuPurple,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text("Search books")
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors =
                    androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KitabuLavender,
                        unfocusedBorderColor = KitabuLavender,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (books.isEmpty()) {

                Text(
                    text = if (searchQuery.isBlank()) {
                        "No books available."
                    } else {
                        "No books found."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 20.dp
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(
                        items = books,
                        key = { book ->
                            book.bookId
                        }
                    ) { book ->

                        BookCard(
                            book = book,
                            onClick = {
                                if (book.isAvailable) {
                                    selectedBook = book
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    selectedBook?.let { book ->

        ReserveBookDialog(
            book = book,
            onDismiss = {
                selectedBook = null
            },
            onReserve = { userName, durationDays ->

                onReserveBook(
                    book,
                    userName,
                    durationDays
                )

                selectedBook = null
            }
        )
    }
}
@Composable
private fun BookCard(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = book.isAvailable,
                onClick = onClick
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = book.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            AvailabilityBadge(
                isAvailable = book.isAvailable
            )
        }
    }
}

@Composable
private fun AvailabilityBadge(
    isAvailable: Boolean
) {
    val backgroundColor = if (isAvailable) {
        Color(0xFFD4EED8)
    } else {
        Color(0xFFFFD6B8)
    }

    val textColor = if (isAvailable) {
        Color(0xFF245B32)
    } else {
        Color(0xFFA64B16)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = if (isAvailable) {
                "Available"
            } else {
                "Borrowed"
            },
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
        )
    }
}

@Composable
private fun ReserveBookDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onReserve: (
        userName: String,
        durationDays: Int
    ) -> Unit
) {
    var userName by remember {
        mutableStateOf("")
    }

    var selectedDuration by remember {
        mutableIntStateOf(7)
    }

    val validUserName =
        RentalValidator.isValidUserName(userName)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reserve Book")
        },
        text = {
            Column {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = {
                        userName = it.take(
                            RentalValidator.MAX_NAME_LENGTH
                        )
                    },
                    label = {
                        Text("Your name")
                    },
                    singleLine = true,
                    isError = userName.isNotEmpty() && !validUserName,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Rental duration",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(7, 14, 21).forEach { days ->
                        FilterChip(
                            selected = selectedDuration == days,
                            onClick = {
                                selectedDuration = days
                            },
                            label = {
                                Text("$days days")
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = validUserName,
                onClick = {
                    onReserve(
                        RentalValidator.sanitizeUserName(userName),
                        selectedDuration
                    )
                }
            ) {
                Text("Reserve")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}