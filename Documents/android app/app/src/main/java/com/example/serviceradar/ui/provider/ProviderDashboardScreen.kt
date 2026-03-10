package com.example.serviceradar.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.ui.components.*
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.ProviderViewModel
import com.example.serviceradar.viewmodel.ThemeViewModel
import com.example.serviceradar.utils.LocationUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    providerViewModel: ProviderViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onProfile: () -> Unit = {},
    onEarnings: () -> Unit = {},
    onSetLocation: () -> Unit = {},                        // ← NEW
    themeViewModel: ThemeViewModel = viewModel()
) {
    val providerProfile by providerViewModel.providerProfile.collectAsState()
    val incomingBookings by providerViewModel.incomingBookings.collectAsState()
    val isOnline by providerViewModel.isOnline.collectAsState()
    val uiMessage by providerViewModel.uiMessage.collectAsState()
    val isLoading by providerViewModel.isLoading.collectAsState()
    val analyticsData by providerViewModel.analyticsData.collectAsState()
    val providerLocation by providerViewModel.providerLocation.collectAsState()  // ← NEW
    val displayName by providerViewModel.displayName.collectAsState()  // ← NEW

    val showSetupDialogState = remember { mutableStateOf(false) }
    val showLogoutDialogState = remember { mutableStateOf(false) }
    val showEditProfileDialogState = remember { mutableStateOf(false) }
    var profileChecked by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Create delegate variables to avoid state update warnings
    var showSetupDialog by showSetupDialogState
    var showLogoutDialog by showLogoutDialogState
    var showEditProfileDialog by showEditProfileDialogState

    // Helper functions for state updates
    val closeSetupDialog = { showSetupDialogState.value = false }
    val closeLogoutDialog = { showLogoutDialogState.value = false }
    val openLogoutDialog = { showLogoutDialogState.value = true }
    val closeEditProfileDialog = { showEditProfileDialogState.value = false }

    LaunchedEffect(providerProfile) {
        if (profileChecked && providerProfile == null) showSetupDialog = true
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        profileChecked = true
        if (providerProfile == null) showSetupDialog = true
        providerViewModel.loadProviderLocation()           // ← NEW: restore saved location
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            providerViewModel.clearMessage()
        }
    }

    if (showSetupDialog) {
        ProviderSetupDialog(
            onSave = { category, price ->
                providerViewModel.createOrUpdateProvider(category, price)
                closeSetupDialog()
            },
            onDismiss = closeSetupDialog
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = closeLogoutDialog,
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = {
                    closeLogoutDialog()
                    onLogout()
                },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) {
                    Text("Logout", color = White)
                }
            },
            dismissButton = {
                Button(onClick = closeLogoutDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cancel", color = White, fontWeight = FontWeight.SemiBold)
                }
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
                    contentColor = White,
                    actionColor = White
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Welcome back $displayName 👋", fontSize = 13.sp, color = White.copy(alpha = 0.8f))
                                Text("Provider Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = White)
                            }
                            IconButton(
                                onClick = openLogoutDialog,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(White.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout",
                                    tint = White, modifier = Modifier.size(20.dp))
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                    Icon(Icons.Default.DarkMode, contentDescription = "Dark Mode",
                                        tint = White, modifier = Modifier.size(18.dp))
                                    Text("Theme", fontSize = 10.sp, color = White)
                                }
                            }

                            OutlinedButton(
                                onClick = onEarnings,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, White.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = White.copy(alpha = 0.15f), contentColor = White),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.AttachMoney, contentDescription = "Earnings",
                                        tint = White, modifier = Modifier.size(18.dp))
                                    Text("Earnings", fontSize = 10.sp, color = White)
                                }
                            }

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
                        }
                    }
                }
            }

            // ── Provider Profile Card ────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    providerProfile?.let { profile ->
                        ServiceCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // ── Left: provider info ──
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.weight(1f)) {
                                    Text(profile.category, fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp, color = TextDark)
                                    Text("₹${profile.price}/hr", color = NavyAccent,
                                        fontWeight = FontWeight.SemiBold)
                                    Text("⭐ ${profile.averageRating} Rating",
                                        color = TextLight, fontSize = 13.sp)

                                    // ── NEW: Location status label ──────────
                                    val hasLocation = LocationUtils.hasValidLocation(
                                        providerLocation?.first ?: 0.0,
                                        providerLocation?.second ?: 0.0
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (hasLocation) SuccessGreen else MediumGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (hasLocation) "Location set ✓"
                                            else "No location set",
                                            fontSize = 12.sp,
                                            color = if (hasLocation) SuccessGreen else MediumGray,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    // ────────────────────────────────────────
                                }

                                // ── Right: controls ──
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                if (isOnline) SuccessGreen.copy(alpha = 0.1f)
                                                else ErrorRed.copy(alpha = 0.1f)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (isOnline) "🟢 Online" else "🔴 Offline",
                                            fontSize = 13.sp,
                                            color = if (isOnline) SuccessGreen else ErrorRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Switch(
                                        checked = isOnline,
                                        onCheckedChange = { providerViewModel.toggleOnlineStatus() },
                                        enabled = !isLoading,
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = White,
                                            checkedTrackColor = NavyPrimary
                                        )
                                    )

                                    // Edit profile button
                                    IconButton(onClick = { showEditProfileDialog = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile",
                                            tint = NavyPrimary)
                                    }

                                    // ── NEW: Set Location button ────────────
                                    IconButton(onClick = onSetLocation) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = "Set Location",
                                            tint = if (LocationUtils.hasValidLocation(
                                                    providerLocation?.first ?: 0.0,
                                                    providerLocation?.second ?: 0.0
                                                )) SuccessGreen else NavyPrimary
                                        )
                                    }
                                    // ────────────────────────────────────────
                                }
                            }
                        }
                    }
                }
            }

            // ── Stats row ───────────────────────────────────────────────────
            item {
                Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProviderStatCard("Requests", incomingBookings.size.toString(), Modifier.weight(1f))
                    ProviderStatCard("Rating", "${providerProfile?.averageRating ?: 0.0}⭐", Modifier.weight(1f))
                    ProviderStatCard("Status", if (isOnline) "Active" else "Away", Modifier.weight(1f))
                }
            }

            // ── Analytics ───────────────────────────────────────────────────
            item { Column(modifier = Modifier.padding(horizontal = 24.dp)) { SectionTitle("Performance Analytics") } }

            item {
                Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AnalyticsCard("Total Earnings", "₹${analyticsData?.totalEarnings?.toInt() ?: 0}",
                        modifier = Modifier.weight(1f))
                    AnalyticsCard("Completed", "${analyticsData?.completedBookings ?: 0}",
                        subtitle = "bookings", modifier = Modifier.weight(1f))
                }
            }

            item {
                Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AnalyticsCard(
                        "Avg Rating",
                        "${String.format(Locale.getDefault(), "%.1f", analyticsData?.averageRating ?: 0.0)} ★",
                        subtitle = "${analyticsData?.ratingCount ?: 0} ratings",
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsCard("Total Bookings", "${analyticsData?.totalBookings ?: 0}",
                        subtitle = "all time", modifier = Modifier.weight(1f))
                }
            }

            // ── Incoming Requests ────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionTitle("Incoming Requests (${incomingBookings.size})")
                }
            }

            if (incomingBookings.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        ServiceCard {
                            EmptyStateIllustration(title = "No Incoming Requests",
                                description = "Check back soon for new service requests!")
                        }
                    }
                }
            } else {
                items(incomingBookings) { booking ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        PremiumBookingRequestCard(
                            booking = booking,
                            onAccept = { providerViewModel.acceptBooking(booking.id) },
                            onReject = { providerViewModel.rejectBooking(booking.id) },
                            onComplete = if (booking.status == "accepted") {
                                { providerViewModel.completeBooking(booking.id) }
                            } else null
                        )
                    }
                }
            }
        }
    }

    if (showEditProfileDialog && providerProfile != null) {
        EditProfileDialog(
            initialCategory = providerProfile!!.category,
            initialPrice = providerProfile!!.price,
            onSave = { category, price ->
                providerViewModel.updateProfile(category, price)
                closeEditProfileDialog()
            },
            onDismiss = closeEditProfileDialog
        )
    }
}

// ── Unchanged components below ────────────────────────────────────────────────

@Composable
fun ProviderStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
            Text(title, fontSize = 12.sp, color = TextLight)
        }
    }
}

@Composable
fun PremiumBookingRequestCard(
    booking: Booking,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onComplete: (() -> Unit)? = null
) {
    ServiceCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(booking.serviceCategory, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = TextDark)
                    Text("Customer: ${booking.customerId.take(12)}...", color = TextLight, fontSize = 12.sp)
                }
                StatusBadge(status = booking.status)
            }

            if (booking.scheduledDate.isNotEmpty() || booking.scheduledTime.isNotEmpty()) {
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (booking.status == "pending") {
                    Button(onClick = onAccept, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) { Text("✓ Accept", color = White, fontWeight = FontWeight.SemiBold) }

                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        border = BorderStroke(1.dp, ErrorRed)
                    ) { Text("✗ Reject", color = White, fontWeight = FontWeight.SemiBold) }

                } else if (booking.status == "accepted" && onComplete != null) {
                    Button(onClick = onComplete, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) { Text("✔ Mark Done", color = White, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSetupDialog(onSave: (String, Double) -> Unit, onDismiss: () -> Unit) {
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val categories = listOf("Plumber", "Electrician", "Tutor", "Cleaner", "Carpenter")
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Setup Your Profile", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = category, onValueChange = {}, readOnly = true,
                        label = { Text("Select Category") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary, unfocusedBorderColor = MediumGray)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
                        modifier = Modifier.background(White)) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = TextDark, fontWeight = FontWeight.Medium) },
                                onClick = { category = cat; expanded = false },
                                modifier = Modifier.background(
                                    if (cat == category) NavyPrimary.copy(alpha = 0.1f) else White)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Price per hour (₹)") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary, unfocusedBorderColor = MediumGray)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(category, price.toDoubleOrNull() ?: 0.0) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) { Text("Save", color = White) }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Skip", color = White, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    initialCategory: String,
    initialPrice: Double,
    onSave: (String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf(initialCategory) }
    var price by remember { mutableStateOf(initialPrice.toString()) }
    val categories = listOf("Plumber", "Electrician", "Tutor", "Cleaner", "Carpenter")
    var expanded by remember { mutableStateOf(false) }
    val isPriceValid = price.toDoubleOrNull() != null && price.toDouble() > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = category, onValueChange = {}, readOnly = true,
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary, unfocusedBorderColor = MediumGray),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { option ->
                            DropdownMenuItem(text = { Text(option) },
                                onClick = { category = option; expanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Price (per hour)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary, unfocusedBorderColor = MediumGray),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            GradientButton(
                text = "Save",
                onClick = { val v = price.toDoubleOrNull() ?: 0.0; if (category.isNotBlank() && isPriceValid) onSave(category, v) },
                enabled = category.isNotBlank() && isPriceValid,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = White, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}