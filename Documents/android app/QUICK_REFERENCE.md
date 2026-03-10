# ServiceRadar - Quick Reference & Code Examples

## 🚀 Quick Start Examples

### 1. Using Booking Status Timeline

```kotlin
// In your booking card:
@Composable
fun MyBookingCard(booking: Booking) {
    ServiceCard {
        BookingStatusTimeline(
            status = booking.status,
            acceptedAt = booking.acceptedAt,
            completedAt = booking.completedAt
        )
    }
}
```

---

### 2. Searching Providers

```kotlin
// In your ViewModel or Screen:
@Composable
fun SearchScreen(customerViewModel: CustomerViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    
    SearchFilterBar(
        searchQuery = searchQuery,
        onSearchChange = { query ->
            searchQuery = query
            customerViewModel.searchProviders(query)
        },
        onFilterClick = { /* Open filter sheet */ }
    )
    
    val filteredProviders by customerViewModel.filteredProviders.collectAsState()
    // Display filteredProviders
}
```

---

### 3. Filtering Providers

```kotlin
// Example: Filter by price and rating
val filter = ProviderFilter(
    minPrice = 200.0,
    maxPrice = 1000.0,
    minRating = 3.5,
    searchQuery = "",
    categories = listOf("Plumber", "Electrician")
)

customerViewModel.filterProviders(filter)
```

---

### 4. Displaying Analytics

```kotlin
@Composable
fun AnalyticsSection(providerViewModel: ProviderViewModel) {
    val analyticsData by providerViewModel.analyticsData.collectAsState()
    
    LaunchedEffect(Unit) {
        providerViewModel.loadAnalytics()
    }
    
    analyticsData?.let { analytics ->
        Row {
            AnalyticsCard(
                title = "Total Earnings",
                value = "₹${analytics.totalEarnings.toInt()}"
            )
            AnalyticsCard(
                title = "Completed",
                value = "${analytics.completedBookings}",
                subtitle = "bookings"
            )
        }
    }
}
```

---

### 5. Comparing Two Providers

```kotlin
@Composable
fun ComparisonView(provider1: Provider, provider2: Provider) {
    ProviderComparisonCard(
        provider1 = provider1,
        provider2 = provider2
    )
}
```

---

### 6. Reporting a Provider

```kotlin
// Navigate to report screen:
navController.navigate("report_provider/${provider.id}")

// In ReportProviderScreen:
ReportProviderScreen(
    provider = provider,
    customerViewModel = customerViewModel,
    onBackClick = { navController.popBackStack() },
    onSuccess = {
        navController.popBackStack()
        // Show success message
    }
)
```

---

### 7. Viewing Booking History

```kotlin
// Navigate to history:
navController.navigate("booking_history")

// Filter bookings:
val filter = BookingFilter(
    status = "completed",
    startDate = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000), // Last 30 days
    endDate = System.currentTimeMillis()
)
customerViewModel.filterBookingHistory(filter)
```

---

### 8. Browsing Categories

```kotlin
// Navigate to categories:
navController.navigate("provider_categories")

// In ProviderCategoriesScreen, tap category:
ProviderCategoriesScreen(
    onBackClick = { navController.popBackStack() },
    onCategoryClick = { category ->
        // Load providers for category
        customerViewModel.loadProvidersByCategory(category)
        navController.navigate("providers/$category")
    }
)
```

---

### 9. Empty States

```kotlin
// Use throughout your app:
if (providers.isEmpty()) {
    EmptyStateIllustration(
        title = "No Providers Found",
        description = "Try adjusting your price range or rating filter"
    )
}
```

---

### 10. Pull to Refresh Implementation

```kotlin
@Composable
fun ProviderDashboard(providerViewModel: ProviderViewModel) {
    val isRefreshing by providerViewModel.isRefreshing.collectAsState()
    
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { providerViewModel.refreshDashboard() }
    ) {
        LazyColumn {
            // Dashboard content
        }
    }
}
```

---

### 11. Logout Implementation

```kotlin
// Already in ProviderDashboardScreen & CustomerHomeScreen:
IconButton(onClick = onLogout) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
        contentDescription = "Logout",
        tint = White
    )
}

// In Navigation:
onLogout = {
    authViewModel.logout()
    navController.navigate("login") {
        popUpTo(0) { inclusive = true }
    }
}
```

---

## 📚 Data Model Examples

### Booking with Timeline

```kotlin
val booking = Booking(
    id = "booking123",
    customerId = "cust456",
    providerId = "prov789",
    serviceCategory = "Plumbing",
    status = "accepted",
    timestamp = System.currentTimeMillis(),
    acceptedAt = System.currentTimeMillis() + 5000,
    completedAt = null,
    rejectedAt = null,
    price = 1200.0,
    rating = 0.0,
    isRated = false
)
```

### Provider with Analytics

```kotlin
val provider = Provider(
    id = "prov123",
    userId = "user456",
    category = "Plumber",
    price = 500.0,
    isOnline = true,
    averageRating = 4.5,
    totalBookings = 25,
    completedBookings = 23,
    totalEarnings = 11500.0,
    ratingCount = 18,
    reportsCount = 0,
    description = "Professional plumber with 5+ years experience",
    imageUrl = "https://example.com/photo.jpg"
)
```

### Analytics Data

```kotlin
val analytics = AnalyticsData(
    providerId = "prov123",
    totalBookings = 25,
    completedBookings = 23,
    totalEarnings = 11500.0,
    averageRating = 4.5,
    ratingCount = 18,
    weeklyEarnings = listOf(500.0, 800.0, 1200.0, 950.0, 1100.0, 1500.0, 1350.0),
    monthlyEarnings = listOf(2000.0, 2500.0, 3200.0),
    lastUpdated = System.currentTimeMillis()
)
```

### Report Data

```kotlin
val report = ProviderReport(
    id = "report123",
    providerId = "prov789",
    reporterId = "cust456",
    reason = "Unprofessional Behavior",
    description = "The provider was very rude during the service",
    timestamp = System.currentTimeMillis(),
    status = "pending"
)
```

---

## 🔗 Navigation Routes to Add

```kotlin
// Add to NavGraph.kt:

// Provider Categories
composable("provider_categories") {
    ProviderCategoriesScreen(
        onBackClick = { navController.popBackStack() },
        onCategoryClick = { category ->
            customerViewModel.loadProvidersByCategory(category)
        }
    )
}

// Booking History
composable("booking_history") {
    BookingHistoryScreen(
        onBackClick = { navController.popBackStack() }
    )
}

// Report Provider
composable("report_provider/{providerId}") { backStackEntry ->
    val providerId = backStackEntry.arguments?.getString("providerId") ?: return@composable
    val provider = /* Get provider from ViewModel or parameter */
    ReportProviderScreen(
        provider = provider,
        onBackClick = { navController.popBackStack() },
        onSuccess = { navController.popBackStack() }
    )
}
```

---

## 🎯 ViewModel Method Cheatsheet

### CustomerViewModel
```kotlin
// Search
customerViewModel.searchProviders("plumber")

// Filter
customerViewModel.filterProviders(ProviderFilter(...))

// Filter Bookings
customerViewModel.filterBookingHistory(BookingFilter(...))

// Report
customerViewModel.reportProvider(
    providerId = "prov123",
    reason = "Poor Quality Service",
    description = "Service was not as expected"
)

// Refresh
customerViewModel.refreshData()

// Load by category
customerViewModel.loadProvidersByCategory("Plumber")

// Load bookings
customerViewModel.loadMyBookings()

// Submit rating
customerViewModel.submitRating(bookingId, providerId, 4.5)
```

### ProviderViewModel
```kotlin
// Load analytics
providerViewModel.loadAnalytics()

// Load earnings
providerViewModel.loadEarningsHistory()

// Compare
val isWinner = providerViewModel.compareProviders(otherProvider)

// Refresh dashboard
providerViewModel.refreshDashboard()

// Toggle online
providerViewModel.toggleOnlineStatus()

// Accept booking
providerViewModel.acceptBooking(bookingId)

// Reject booking
providerViewModel.rejectBooking(bookingId)

// Complete booking
providerViewModel.completeBooking(bookingId)

// Load profile
providerViewModel.loadProviderProfile()

// Load bookings
providerViewModel.loadIncomingBookings()
```

---

## 🎨 UI Component Cheatsheet

```kotlin
// Timeline
BookingStatusTimeline(status, acceptedAt, completedAt)

// Comparison
ProviderComparisonCard(provider1, provider2)

// Empty state
EmptyStateIllustration(title, description)

// Search bar
SearchFilterBar(query, { newQuery }, { showFilter() })

// Rating filter
RatingFilterSlider(minRating, { updateRating() })

// Price filter
PriceRangeSlider(min, max, { updateMin() }, { updateMax() })

// Analytics card
AnalyticsCard(title, value, subtitle)

// Status badge
StatusBadge(status)

// Gradient button
GradientButton(text, { onClick() }, enabled = true)

// Service card
ServiceCard {
    // Content
}

// Section title
SectionTitle(text)
```

---

## 📊 StateFlow Reference

### CustomerViewModel StateFlows
```kotlin
providers: StateFlow<List<Provider>>
myBookings: StateFlow<List<Booking>>
uiMessage: StateFlow<String?>
isLoading: StateFlow<Boolean>
isNetworkAvailable: StateFlow<Boolean>
searchQuery: StateFlow<String>
filteredProviders: StateFlow<List<Provider>>
bookingFilters: StateFlow<BookingFilter>
providerFilter: StateFlow<ProviderFilter>
isRefreshing: StateFlow<Boolean>
```

### ProviderViewModel StateFlows
```kotlin
providerProfile: StateFlow<Provider?>
incomingBookings: StateFlow<List<Booking>>
isOnline: StateFlow<Boolean>
uiMessage: StateFlow<String?>
isLoading: StateFlow<Boolean>
analyticsData: StateFlow<AnalyticsData?>
earningsHistory: StateFlow<List<Pair<Long, Double>>>
isRefreshing: StateFlow<Boolean>
```

---

## 🔄 Common Flows

### User Search Flow
1. User types in search bar
2. `SearchFilterBar` calls `onSearchChange`
3. `customerViewModel.searchProviders(query)` executes
4. `CustomerRepository.searchProviders()` queries Firestore
5. Results update `_filteredProviders` StateFlow
6. UI re-renders with new results
7. Empty state shown if no results

### Booking Timeline Flow
1. Booking created with status = "pending"
2. User accepts → status = "accepted", acceptedAt = timestamp
3. User completes → status = "completed", completedAt = timestamp
4. `BookingStatusTimeline` re-renders
5. Visual progress updates: ✓ → ✓ → ✓

### Report Provider Flow
1. User navigates to ReportProviderScreen
2. Selects reason and writes description
3. Clicks "Submit Report"
4. `customerViewModel.reportProvider()` called
5. Document added to "provider_reports" collection
6. Success message shown
7. Navigate back

---

## ✅ Testing Scenarios

### Test Search
```
1. Type "plumber" in search
2. Verify providers matching "plumber" appear
3. Type "electrician"
4. Verify new results load
5. Clear search
6. Verify all providers shown again
```

### Test Timeline
```
1. Create a booking
2. Verify pending ✓ is filled
3. Accept booking
4. Verify accepted ✓ is filled
5. Complete booking
6. Verify completed ✓ is filled
```

### Test Report
```
1. Navigate to provider
2. Click report button
3. Select reason
4. Write description
5. Click submit
6. Verify success message
```

### Test Analytics
```
1. Open provider dashboard
2. Verify analytics loaded
3. Check earnings value > 0
4. Check completed bookings > 0
5. Check rating displayed correctly
```

---

## 💾 Storage & Offline

### Room Database
- Automatic caching of providers
- Automatic caching of bookings
- Optional: analytics caching
- Automatic: report caching

### Firestore Offline
- Enabled by default
- Queries work offline (cached data)
- Mutations queue and sync when online
- Status shown in UI

---

This quick reference covers all major use cases and code patterns for the implemented features.


