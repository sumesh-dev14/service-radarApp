package com.example.serviceradar.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.BookingFilter
import com.example.serviceradar.ui.components.*
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.CustomerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    customerViewModel: CustomerViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val myBookings by customerViewModel.myBookings.collectAsState()

    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking History", color = TextDark, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf("All", "Pending", "Accepted", "Completed", "Rejected")
                    statuses.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = {
                                selectedStatus = status
                                val filter = if (status == "All") BookingFilter() else BookingFilter(status = status)
                                customerViewModel.filterBookingHistory(filter)
                            },
                            label = { Text(status) }
                        )
                    }
                }
            }

            if (myBookings.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(vertical = 32.dp)) {
                        EmptyStateIllustration(title = "No Bookings", description = "You haven't made any bookings matching the filter yet")
                    }
                }
            } else {
                items(myBookings) { booking ->
                    BookingHistoryCard(booking = booking, customerViewModel = customerViewModel)
                }
            }
        }
    }
}

@Composable
fun BookingHistoryCard(booking: Booking, customerViewModel: CustomerViewModel) {
    var selectedRating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    ServiceCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // ── Header ──
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Text(booking.serviceCategory, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Provider: ${booking.providerId.take(12)}...", color = TextLight, fontSize = 12.sp)
                    Text("Booked: ${formatDate(booking.timestamp)}", color = TextLight, fontSize = 11.sp)
                }
                StatusBadge(status = booking.status)
            }

            // ── Scheduled date/time badge ──
            if (booking.scheduledDate.isNotEmpty() || booking.scheduledTime.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = NavyPrimary.copy(alpha = 0.07f), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Scheduled:", fontSize = 12.sp, color = TextLight, fontWeight = FontWeight.SemiBold)
                    if (booking.scheduledDate.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(14.dp))
                            Text(booking.scheduledDate, fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (booking.scheduledTime.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(14.dp))
                            Text(booking.scheduledTime, fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            HorizontalDivider(color = LightGray)

            BookingStatusTimeline(status = booking.status, acceptedAt = booking.acceptedAt, completedAt = booking.completedAt)

            if (booking.price > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Amount", color = TextLight, fontSize = 13.sp)
                    Text("₹${booking.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GradientStart)
                }
            }

            if (booking.status == "completed" && !booking.isRated) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = LightGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Rate & Review this service:", fontSize = 13.sp, color = TextLight)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { star ->
                        TextButton(onClick = { selectedRating = star }, contentPadding = PaddingValues(4.dp)) {
                            Text(text = if (star <= selectedRating) "⭐" else "☆", fontSize = 22.sp)
                        }
                    }
                }
                OutlinedTextField(
                    value = reviewText, onValueChange = { reviewText = it },
                    label = { Text("Write a review...") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                if (selectedRating > 0) {
                    Button(
                        onClick = { customerViewModel.submitRatingAndReview(booking.id, booking.providerId, selectedRating.toDouble(), reviewText) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) { Text("Submit ⭐ $selectedRating", color = White) }
                }
            }

            if (booking.isRated) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your Rating: ${"⭐".repeat(booking.rating.toInt())}", color = NavyPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                if (!booking.review.isNullOrEmpty()) {
                    Text("Your Review: ${booking.review}", color = TextDark, fontSize = 13.sp)
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return if (timestamp == 0L) "Unknown"
    else SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
}