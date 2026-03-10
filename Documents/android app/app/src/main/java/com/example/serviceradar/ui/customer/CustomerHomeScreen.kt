package com.example.serviceradar.ui.customer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.ui.components.*
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.utils.NetworkMonitor
import com.example.serviceradar.viewmodel.CustomerViewModel
import com.example.serviceradar.viewmodel.ThemeViewModel
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Grass
import java.util.Calendar
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Date & Time Picker Dialog (unchanged)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DateTimePickerDialog(
    providerName: String,
    serviceCategory: String,
    onConfirm: (date: String, time: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var datePickerShown by remember { mutableStateOf(false) }
    var timePickerShown by remember { mutableStateOf(false) }

    if (datePickerShown) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = "%04d-%02d-%02d".format(year, month + 1, day)
                datePickerShown = false
                timePickerShown = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).also { it.datePicker.minDate = calendar.timeInMillis }.show()
        datePickerShown = false
    }

    if (timePickerShown) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                selectedTime = "%d:%02d %s".format(displayHour, minute, amPm)
                timePickerShown = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
        timePickerShown = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("📅 Schedule Booking", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 18.sp)
                Text(serviceCategory, fontSize = 13.sp, color = TextLight)
                if (providerName.isNotEmpty()) {
                    Text("with $providerName", fontSize = 13.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { datePickerShown = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, if (selectedDate.isEmpty()) MediumGray else NavyPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (selectedDate.isEmpty()) TextDark else NavyPrimary
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (selectedDate.isEmpty()) "Choose Date" else "📅  $selectedDate",
                            fontSize = 14.sp,
                            fontWeight = if (selectedDate.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                        )
                    }
                }

                OutlinedButton(
                    onClick = { timePickerShown = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, if (selectedTime.isEmpty()) MediumGray else NavyPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (selectedTime.isEmpty()) TextLight else NavyPrimary
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (selectedTime.isEmpty()) "Choose Time" else "🕐  $selectedTime",
                            fontSize = 14.sp,
                            fontWeight = if (selectedTime.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                        )
                    }
                }

                if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = NavyPrimary.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("✅ Booking Summary", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                            Text("Service: $serviceCategory", fontSize = 12.sp, color = TextDark)
                            Text("Date: $selectedDate", fontSize = 12.sp, color = TextDark)
                            Text("Time: $selectedTime", fontSize = 12.sp, color = TextDark)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDate, selectedTime) },
                enabled = selectedDate.isNotEmpty() && selectedTime.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Confirm Booking", color = White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextLight)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// CustomerHomeScreen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    customerViewModel: CustomerViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onProfile: () -> Unit = {},
    onNearbyMap: () -> Unit = {},                          // ← NEW
    themeViewModel: ThemeViewModel = viewModel()
) {
    val providers by customerViewModel.providers.collectAsState()
    val myBookings by customerViewModel.myBookings.collectAsState()
    val uiMessage by customerViewModel.uiMessage.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()
    val isNetworkAvailable by customerViewModel.isNetworkAvailable.collectAsState()
    val isRefreshing by customerViewModel.isRefreshing.collectAsState()
    val categories = customerViewModel.categories
    val favourites by customerViewModel.favourites.collectAsState()
    val isLocating by customerViewModel.isLocating.collectAsState()    // ← NEW
    val displayName by customerViewModel.displayName.collectAsState()   // ← NEW

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedBookingStatus by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var pendingBookingProvider by remember { mutableStateOf<Provider?>(null) }
    var pendingBookingCategory by remember { mutableStateOf("") }

    val filteredProviders = if (selectedCategory != null) {
        val categoryProviders = providers.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        if (searchQuery.isNotEmpty()) categoryProviders.filter {
            it.category.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)
        } else categoryProviders
    } else {
        if (searchQuery.isNotEmpty()) providers.filter {
            it.category.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)
        } else providers
    }

    val filteredBookings = if (selectedBookingStatus != null)
        myBookings.filter { it.status.equals(selectedBookingStatus, ignoreCase = true) }
    else myBookings

    DisposableEffect(Unit) {
        val monitor = NetworkMonitor(context) { isConnected ->
            customerViewModel.setNetworkAvailable(isConnected)
        }
        monitor.start()
        onDispose { monitor.stop() }
    }

    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable) snackbarHostState.showSnackbar("📡 No Internet — Showing cached data")
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            customerViewModel.clearMessage()
        }
    }

    LaunchedEffect(Unit) {
        customerViewModel.loadCategoriesFromApi()
        customerViewModel.loadFavourites()
    }

    pendingBookingProvider?.let { provider ->
        DateTimePickerDialog(
            providerName = provider.name,
            serviceCategory = pendingBookingCategory,
            onConfirm = { date, time ->
                customerViewModel.createBooking(
                    providerId = provider.id,
                    serviceCategory = pendingBookingCategory,
                    scheduledDate = date,
                    scheduledTime = time
                )
                pendingBookingProvider = null
                pendingBookingCategory = ""
            },
            onDismiss = {
                pendingBookingProvider = null
                pendingBookingCategory = ""
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = { showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Logout", color = White) }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) { Text("Cancel", color = White) }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState, snackbar = { snackbarData ->
                Snackbar(
                    snackbarData,
                    modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(8.dp)),
                    containerColor = Color.Black,
                    contentColor = White
                )
            })
        },
        containerColor = LightGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Header ──────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd)))
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        // ── Title row ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Hello $displayName 👋", fontSize = 13.sp, color = White.copy(alpha = 0.8f))
                                Text("Find Services", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isNetworkAvailable) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(ErrorRed)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) { Text("📡 Offline", color = White, fontSize = 11.sp) }
                                }
                                IconButton(
                                    onClick = { showLogoutDialog = true },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(White.copy(alpha = 0.15f))
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout",
                                        tint = White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        // ── Action buttons row ── (Theme / Profile / Refresh / Nearby)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Theme
                            OutlinedButton(
                                onClick = { themeViewModel.toggleDarkMode() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, White.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = White.copy(alpha = 0.15f), contentColor = White),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.DarkMode, contentDescription = "Theme",
                                        tint = White, modifier = Modifier.size(18.dp))
                                    Text("Theme", fontSize = 10.sp, color = White)
                                }
                            }

                            // Profile
                            OutlinedButton(
                                onClick = onProfile,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, White.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = White.copy(alpha = 0.15f), contentColor = White),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.Person, contentDescription = "Profile",
                                        tint = White, modifier = Modifier.size(18.dp))
                                    Text("Profile", fontSize = 10.sp, color = White)
                                }
                            }

                            // Refresh
                            OutlinedButton(
                                onClick = { customerViewModel.refreshData() },
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, White.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = White.copy(alpha = 0.15f), contentColor = White),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh",
                                        tint = White,
                                        modifier = if (isRefreshing) Modifier.size(18.dp).rotate(360f)
                                        else Modifier.size(18.dp))
                                    Text("Refresh", fontSize = 10.sp, color = White)
                                }
                            }

                            // ── NEW: Nearby Map button ──────────────────────
                            OutlinedButton(
                                onClick = {
                                    customerViewModel.fetchCustomerLocation()
                                    onNearbyMap()
                                },
                                enabled = !isLocating,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, White.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = White.copy(alpha = 0.15f), contentColor = White),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    if (isLocating) {
                                        CircularProgressIndicator(
                                            color = White,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Icon(Icons.Default.LocationOn, contentDescription = "Nearby",
                                            tint = White, modifier = Modifier.size(18.dp))
                                    }
                                    Text("Nearby", fontSize = 10.sp, color = White)
                                }
                            }
                            // ────────────────────────────────────────────────
                        }

                        // ── Search bar ──
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search services...", color = White.copy(alpha = 0.6f)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = White) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = White.copy(alpha = 0.5f),
                                unfocusedBorderColor = White.copy(alpha = 0.3f),
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                cursorColor = White,
                                focusedLeadingIconColor = White,
                                unfocusedLeadingIconColor = White
                            )
                        )
                    }
                }
            }

            // ── Categories ──────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Service Categories")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategory = category
                                    customerViewModel.loadProvidersByCategory(category)
                                },
                                label = {
                                    Text(category,
                                        color = if (isSelected) White else TextDark,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                },
                                leadingIcon = {
                                    Icon(getCategoryIcon(category), contentDescription = null,
                                        tint = if (isSelected) White else TextDark)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = White, selectedContainerColor = NavyPrimary),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(40.dp)
                            )
                        }
                    }
                }
            }

            // ── Favourites ──────────────────────────────────────────────────
            if (favourites.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        SectionTitle("My Favourites ❤️")
                    }
                }
                items(favourites) { fav ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        PremiumProviderCard(
                            provider = Provider(id = fav.providerId, category = fav.category,
                                price = fav.price, isOnline = false, averageRating = 0.0),
                            onBookClick = {
                                pendingBookingProvider = Provider(id = fav.providerId,
                                    category = fav.category, price = fav.price,
                                    isOnline = false, averageRating = 0.0)
                                pendingBookingCategory = fav.category
                            },
                            alreadyBooked = myBookings.any {
                                it.providerId == fav.providerId && it.status in listOf("pending", "accepted")
                            },
                            isFavourite = true,
                            onFavouriteClick = { customerViewModel.removeFavourite(fav.providerId) }
                        )
                    }
                }
            }

            // ── Providers ───────────────────────────────────────────────────
            if (selectedCategory != null) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        SectionTitle("Available — $selectedCategory")
                    }
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NavyPrimary)
                        }
                    }
                } else if (filteredProviders.isEmpty()) {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            ServiceCard {
                                EmptyStateIllustration(
                                    title = "No Online Providers",
                                    description = if (searchQuery.isNotEmpty()) "No providers match your search."
                                    else "No $selectedCategory providers available right now."
                                )
                            }
                        }
                    }
                } else {
                    items(filteredProviders) { provider ->
                        val alreadyBooked = myBookings.any {
                            it.providerId == provider.id && it.status in listOf("pending", "accepted")
                        }
                        val isFav = favourites.any { it.providerId == provider.id }
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            PremiumProviderCard(
                                provider = provider,
                                onBookClick = {
                                    pendingBookingProvider = provider
                                    pendingBookingCategory = provider.category
                                },
                                alreadyBooked = alreadyBooked,
                                isFavourite = isFav,
                                onFavouriteClick = { customerViewModel.toggleFavourite(provider) }
                            )
                        }
                    }
                }
            }

            // ── My Bookings ─────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionTitle("My Bookings (${filteredBookings.size})")
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Filter by Status:", fontSize = 12.sp, color = TextLight, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedBookingStatus == null,
                                onClick = { selectedBookingStatus = null },
                                label = { Text("All", fontSize = 12.sp,
                                    color = if (selectedBookingStatus == null) White else TextDark) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = White, selectedContainerColor = NavyPrimary),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(32.dp)
                            )
                        }
                        items(listOf("pending", "accepted", "completed")) { status ->
                            FilterChip(
                                selected = selectedBookingStatus == status,
                                onClick = { selectedBookingStatus = status },
                                label = {
                                    Text(status.replaceFirstChar { it.uppercase() }, fontSize = 12.sp,
                                        color = if (selectedBookingStatus == status) White else TextDark)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = White, selectedContainerColor = NavyPrimary),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            }

            if (filteredBookings.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        ServiceCard {
                            EmptyStateIllustration(title = "No Bookings Yet",
                                description = "Browse services and book a provider to get started!")
                        }
                    }
                }
            } else {
                items(filteredBookings) { booking ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        PremiumBookingCard(
                            booking = booking,
                            onRatingAndReviewSubmit = { bookingId, providerId, rating, review ->
                                customerViewModel.submitRatingAndReview(bookingId, providerId, rating, review)
                            },
                            onCancel = { bookingId -> customerViewModel.cancelBooking(bookingId) }
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PremiumProviderCard (unchanged)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun PremiumProviderCard(
    provider: Provider,
    onBookClick: () -> Unit,
    alreadyBooked: Boolean = false,
    isFavourite: Boolean? = null,
    onFavouriteClick: (() -> Unit)? = null
) {
    ServiceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (provider.name.isNotEmpty()) {
                    Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text(provider.category, fontSize = 13.sp, color = TextLight)
                } else {
                    Text(provider.category, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                }
                Text("₹${provider.price}/hr", color = NavyAccent, fontWeight = FontWeight.SemiBold)
                Text("⭐ ${provider.averageRating}", color = TextLight, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (provider.isOnline) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (provider.isOnline) "🟢 Online" else "🔴 Offline",
                        fontSize = 12.sp,
                        color = if (provider.isOnline) SuccessGreen else ErrorRed
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onBookClick,
                        enabled = !alreadyBooked,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary, disabledContainerColor = MediumGray),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(if (alreadyBooked) "Booked" else "Book Now", fontSize = 13.sp, color = White)
                    }
                    if (isFavourite != null && onFavouriteClick != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onFavouriteClick) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavourite) ErrorRed else NavyPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PremiumBookingCard (unchanged)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumBookingCard(
    booking: Booking,
    onRatingSubmit: (String, String, Double) -> Unit = { _, _, _ -> },
    onRatingAndReviewSubmit: (String, String, Double, String) -> Unit = { _, _, _, _ -> },
    onCancel: ((String) -> Unit)? = null
) {
    var selectedRating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var showReviewField by remember { mutableStateOf(false) }

    ServiceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Text(booking.serviceCategory, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Text("Provider: ${booking.providerId.take(12)}...", color = TextLight, fontSize = 12.sp)
            }
            StatusBadge(status = booking.status)
        }

        if (booking.scheduledDate.isNotEmpty() || booking.scheduledTime.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NavyPrimary.copy(alpha = 0.07f), shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (booking.scheduledDate.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.DateRange, contentDescription = null,
                            tint = NavyPrimary, modifier = Modifier.size(15.dp))
                        Text(booking.scheduledDate, fontSize = 12.sp, color = NavyPrimary,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
                if (booking.scheduledTime.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null,
                            tint = NavyPrimary, modifier = Modifier.size(15.dp))
                        Text(booking.scheduledTime, fontSize = 12.sp, color = NavyPrimary,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = LightGray)
        Spacer(modifier = Modifier.height(12.dp))

        BookingStatusTimeline(status = booking.status,
            acceptedAt = booking.acceptedAt, completedAt = booking.completedAt)

        if (booking.status == "pending" && onCancel != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onCancel(booking.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border = BorderStroke(1.5.dp, ErrorRed)
            ) { Text("Cancel Booking", color = ErrorRed, fontWeight = FontWeight.SemiBold) }
        }

        if (booking.status == "completed" && !booking.isRated) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LightGray)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Rate this service:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (1..5).forEach { star ->
                    TextButton(onClick = { selectedRating = star; showReviewField = true },
                        contentPadding = PaddingValues(4.dp)) {
                        Text(
                            text = if (star <= selectedRating) "⭐" else "☆",
                            fontSize = 28.sp,
                            color = if (star <= selectedRating) Color(0xFFFDD835) else Color(0xFFBDBDBD)
                        )
                    }
                }
            }
            if (showReviewField) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reviewText, onValueChange = { reviewText = it },
                    placeholder = { Text("Write a review (optional)...", color = TextLight, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary, unfocusedBorderColor = MediumGray),
                    maxLines = 3, minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { if (selectedRating > 0) onRatingAndReviewSubmit(booking.id, booking.providerId, selectedRating.toDouble(), reviewText) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    enabled = selectedRating > 0
                ) { Text("Submit ⭐ $selectedRating stars", color = White, fontWeight = FontWeight.SemiBold) }
            }
        }

        if (booking.isRated) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your Rating:", fontSize = 13.sp, color = TextLight)
                Text("⭐".repeat(booking.rating.toInt()), fontSize = 14.sp)
                Text("(${booking.rating}/5)", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            if (!booking.review.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("\"${booking.review}\"", color = TextLight, fontSize = 13.sp, fontStyle = FontStyle.Italic)
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "plumbing" -> Icons.Default.Plumbing
        "electrical" -> Icons.Default.ElectricBolt
        "cleaning" -> Icons.Default.CleaningServices
        "gardening" -> Icons.Default.Grass
        else -> Icons.Default.Build
    }
}