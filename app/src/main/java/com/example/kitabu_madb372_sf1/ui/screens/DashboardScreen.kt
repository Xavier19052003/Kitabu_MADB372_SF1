package com.example.kitabu_madb372_sf1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu_madb372_sf1.R
import com.example.kitabu_madb372_sf1.data.local.BookingStatus
import com.example.kitabu_madb372_sf1.data.local.BookingWithBook
import com.example.kitabu_madb372_sf1.ui.theme.AvailableBackground
import com.example.kitabu_madb372_sf1.ui.theme.AvailableText
import com.example.kitabu_madb372_sf1.ui.theme.BorrowedText
import com.example.kitabu_madb372_sf1.ui.theme.KitabuCard
import com.example.kitabu_madb372_sf1.ui.theme.KitabuDarkText
import com.example.kitabu_madb372_sf1.ui.theme.KitabuLavender
import com.example.kitabu_madb372_sf1.ui.theme.KitabuMutedText
import com.example.kitabu_madb372_sf1.ui.theme.KitabuPurple
import com.example.kitabu_madb372_sf1.ui.theme.KitabuYellow
import com.example.kitabu_madb372_sf1.ui.viewmodel.DashboardViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    DashboardContent(
        bookings = bookings,
        onConfirmPickup = viewModel::confirmPickup,
        onRenew = viewModel::renewBooking,
        onReturn = viewModel::returnBook,
        onCancel = viewModel::cancelBooking,
        modifier = modifier
    )
}

@Composable
private fun DashboardContent(
    bookings: List<BookingWithBook>,
    onConfirmPickup: (BookingWithBook) -> Unit,
    onRenew: (BookingWithBook, Int) -> Unit,
    onReturn: (BookingWithBook) -> Unit,
    onCancel: (BookingWithBook) -> Unit,
    modifier: Modifier = Modifier
) {
    var bookingToRenew by remember {
        mutableStateOf<BookingWithBook?>(null)
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
            modifier = Modifier.fillMaxSize(),
            alpha = 0.32f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp)
        ) {

            Text(
                text = "My Reservations",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = KitabuPurple,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            if (bookings.isEmpty()) {

                EmptyReservations(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

            } else {

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(
                        items = bookings,
                        key = { booking ->
                            booking.bookingId
                        }
                    ) { booking ->

                        BookingCard(
                            booking = booking,
                            onConfirmPickup = {
                                onConfirmPickup(booking)
                            },
                            onRenew = {
                                bookingToRenew = booking
                            },
                            onReturn = {
                                onReturn(booking)
                            },
                            onCancel = {
                                onCancel(booking)
                            }
                        )
                    }
                }
            }
        }
    }

    bookingToRenew?.let { booking ->

        RenewBookingDialog(
            booking = booking,
            onDismiss = {
                bookingToRenew = null
            },
            onRenew = { extraDays ->

                onRenew(
                    booking,
                    extraDays
                )

                bookingToRenew = null
            }
        )
    }
}

@Composable
private fun EmptyReservations(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = KitabuCard
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 36.dp
                    )
            ) {

                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = KitabuLavender
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = KitabuPurple,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(34.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "No reservations yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = KitabuDarkText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Books you reserve will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KitabuMutedText
                )
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: BookingWithBook,
    onConfirmPickup: () -> Unit,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = KitabuCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = booking.bookTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = KitabuDarkText
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = booking.bookAuthor,
                style = MaterialTheme.typography.bodyLarge,
                color = KitabuDarkText
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = booking.bookCategory,
                style = MaterialTheme.typography.bodyMedium,
                color = KitabuMutedText
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            BookingStatusBadge(
                status = booking.status
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Reserved: ${formatDate(booking.bookingDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = KitabuDarkText
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Return deadline: ${formatDate(booking.returnDeadline)}",
                style = MaterialTheme.typography.bodyMedium,
                color = KitabuDarkText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = formatDaysRemaining(
                    calculateDaysRemaining(booking.returnDeadline)
                ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = KitabuPurple
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            when (booking.status) {

                BookingStatus.PENDING -> {

                    Button(
                        onClick = onConfirmPickup,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KitabuYellow,
                            contentColor = KitabuDarkText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Confirm Pickup",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancel Reservation",
                            color = BorrowedText
                        )
                    }
                }

                BookingStatus.ACTIVE -> {

                    Button(
                        onClick = onRenew,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KitabuYellow,
                            contentColor = KitabuDarkText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Renew",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    OutlinedButton(
                        onClick = onReturn,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = KitabuLavender
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = KitabuPurple
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Return",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                BookingStatus.RETURNED -> Unit
            }
        }
    }
}

@Composable
private fun BookingStatusBadge(
    status: BookingStatus
) {
    val backgroundColor: Color
    val textColor: Color

    when (status) {

        BookingStatus.PENDING -> {
            backgroundColor = KitabuYellow
            textColor = KitabuDarkText
        }

        BookingStatus.ACTIVE -> {
            backgroundColor = AvailableBackground
            textColor = AvailableText
        }

        BookingStatus.RETURNED -> {
            backgroundColor = Color(0xFFECE6F2)
            textColor = KitabuMutedText
        }
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {

        Text(
            text = status.name,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 7.dp
            )
        )
    }
}

@Composable
private fun RenewBookingDialog(
    booking: BookingWithBook,
    onDismiss: () -> Unit,
    onRenew: (Int) -> Unit
) {
    var selectedDays by remember {
        mutableIntStateOf(7)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = KitabuCard,
        title = {
            Text(
                text = "Renew Book",
                color = KitabuPurple,
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column {

                Text(
                    text = booking.bookTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = KitabuDarkText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Current deadline: ${formatDate(booking.returnDeadline)}",
                    color = KitabuMutedText
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Extend rental by",
                    style = MaterialTheme.typography.labelLarge,
                    color = KitabuDarkText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    listOf(7, 14, 21).forEach { days ->

                        FilterChip(
                            selected =
                                selectedDays == days,
                            onClick = {
                                selectedDays = days
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
                onClick = {
                    onRenew(selectedDays)
                }
            ) {

                Text(
                    text = "Renew",
                    color = KitabuPurple,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Cancel",
                    color = KitabuMutedText
                )
            }
        }
    )
}

private fun calculateDaysRemaining(
    returnDeadline: Long
): Long {

    val currentTime = System.currentTimeMillis()
    val difference = returnDeadline - currentTime
    val oneDay = TimeUnit.DAYS.toMillis(1)

    return when {
        difference > 0 -> {
            (difference + oneDay - 1) / oneDay
        }

        difference < 0 -> {
            difference / oneDay
        }

        else -> 0
    }
}

private fun formatDaysRemaining(
    daysRemaining: Long
): String {

    return when {
        daysRemaining > 1 ->
            "$daysRemaining days remaining"

        daysRemaining == 1L ->
            "1 day remaining"

        daysRemaining == 0L ->
            "Due today"

        daysRemaining == -1L ->
            "Overdue by 1 day"

        else ->
            "Overdue by ${-daysRemaining} days"
    }
}


private fun formatDate(
    timestamp: Long
): String {

    val formatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy"
        )

    return Instant
        .ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}