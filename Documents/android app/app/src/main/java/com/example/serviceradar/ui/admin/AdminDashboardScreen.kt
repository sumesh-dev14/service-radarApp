package com.example.serviceradar.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceradar.data.model.Booking
import com.example.serviceradar.data.model.Provider
import com.example.serviceradar.data.model.User
import com.example.serviceradar.ui.components.*
import com.example.serviceradar.ui.theme.*
import com.example.serviceradar.viewmodel.AdminViewModel
import com.example.serviceradar.viewmodel.ThemeViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel = viewModel(),
    onLogout: () -> Unit = {},
    themeViewModel: ThemeViewModel = viewModel()
) {
    val users by adminViewModel.users.collectAsState()
    val providers by adminViewModel.providers.collectAsState()
    val bookings by adminViewModel.bookings.collectAsState()
    val uiMessage by adminViewModel.uiMessage.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Users", "Providers", "Bookings")

    // Handle logout with confirmation
    val handleLogout = {
        showLogoutDialog = true
    }

    // Confirm logout
    val confirmLogout = {
        showLogoutDialog = false
        onLogout()
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            adminViewModel.clearMessage()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = confirmLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Logout", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData,
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        containerColor = Color.Black,
                        contentColor = White,
                        actionColor = White
                    )
                }
            )
        },
        containerColor = LightGray
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                        )
                        .padding(24.dp)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Admin Panel 🛡️",
                                fontSize = 14.sp,
                                color = White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Dashboard",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                        Row {
                            IconButton(onClick = { themeViewModel.toggleDarkMode() }) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = "Dark Mode",
                                    tint = White
                                )
                            }
                            IconButton(onClick = handleLogout) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = White
                                )
                            }
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        title = "Users",
                        count = users.size,
                        emoji = "👥",
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Providers",
                        count = providers.size,
                        emoji = "🔧",
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Bookings",
                        count = bookings.size,
                        emoji = "📋",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tab row
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = White,
                        contentColor = NavyPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = NavyPrimary
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedTab == index)
                                            FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == index)
                                            NavyPrimary else TextLight
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Loading
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NavyPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Tab content
            when (selectedTab) {
                0 -> {
                    if (users.isEmpty()) {
                        item {
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                ServiceCard {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No users found", color = TextLight)
                                    }
                                }
                            }
                        }
                    } else {
                        items(users) { user ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                PremiumUserCard(user = user)
                            }
                        }
                    }
                }
                1 -> {
                    if (providers.isEmpty()) {
                        item {
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                ServiceCard {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No providers found", color = TextLight)
                                    }
                                }
                            }
                        }
                    } else {
                        items(providers) { provider ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                PremiumAdminProviderCard(
                                    provider = provider,
                                    onRemove = { adminViewModel.removeProvider(provider.id) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    if (bookings.isEmpty()) {
                        item {
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                ServiceCard {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No bookings found", color = TextLight)
                                    }
                                }
                            }
                        }
                    } else {
                        items(bookings) { booking ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                PremiumAdminBookingCard(booking = booking)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    count: Int,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = NavyPrimary
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextLight
            )
        }
    }
}

@Composable
fun PremiumUserCard(user: User) {
    ServiceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyUltraLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.email.first().uppercaseChar().toString(),
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 18.sp
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = user.email,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextDark
                    )
                    Text(
                        text = user.role,
                        fontSize = 12.sp,
                        color = TextLight
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(NavyUltraLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = user.role,
                    fontSize = 11.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PremiumAdminProviderCard(
    provider: Provider,
    onRemove: () -> Unit
) {
    ServiceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = provider.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                Text("₹${provider.price}/hr", color = NavyAccent, fontSize = 13.sp)
                Text(
                    text = if (provider.isOnline) "🟢 Online" else "🔴 Offline",
                    fontSize = 12.sp
                )
                Text("⭐ ${provider.averageRating}", color = TextLight, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = onRemove,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text("Remove", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PremiumAdminBookingCard(booking: Booking) {
    ServiceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = booking.serviceCategory,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
                Text(
                    text = "Customer: ${booking.customerId.take(12)}...",
                    color = TextLight,
                    fontSize = 12.sp
                )
                Text(
                    text = "Provider: ${booking.providerId.take(12)}...",
                    color = TextLight,
                    fontSize = 12.sp
                )
            }
            StatusBadge(status = booking.status)
        }
    }
}