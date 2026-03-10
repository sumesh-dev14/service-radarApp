# 🚀 Service Radar - Developer Quick Reference

**Quick Start Guide for Developers**  
**Last Updated**: March 9, 2026

---

## ⚡ Quick Links

- 📖 [Full Documentation](PROJECT_DOCUMENTATION.md)
- ✅ [Production Ready Checklist](PRODUCTION_READY_CHECKLIST.md)
- 🐛 [Bug Fixes Summary](BUG_FIXES_SUMMARY.md)
- 📊 [Project Overview](ServiceRadar_Complete_Project_Context.md)

---

## 🎯 Project at a Glance

```
Service Radar = Booking App for Services
├── Customers: Book services (Plumbing, Electrical, etc.)
├── Providers: Manage bookings & track earnings
├── Admins: Oversee platform
└── All: Ratings, reviews, offline support, dark mode
```

---

## 🏗️ Tech Stack (TL;DR)

| Layer | Technology |
|-------|-----------|
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Backend** | Firebase (Auth + Firestore) |
| **Local DB** | Room Database |
| **Language** | Kotlin |
| **Async** | Coroutines + StateFlow |

---

## 📁 Key Files to Know

### Entry Points
- `MainActivity.kt` - App starts here
- `NavGraph.kt` - All screen routes

### Features
- **Customer** → `ui/customer/CustomerHomeScreen.kt`
- **Provider** → `ui/provider/ProviderDashboardScreen.kt`
- **Admin** → `ui/admin/AdminDashboardScreen.kt`

### Data Access
- **Customer Logic** → `data/repository/CustomerRepository.kt`
- **Provider Logic** → `data/repository/ProviderRepository.kt`

### UI Components
- **Reusable Components** → `ui/components/UiComponents.kt`
- **Theme/Colors** → `ui/theme/Color.kt`

---

## 🚀 Build Commands

```bash
# Clean build
./gradlew clean build

# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Release Bundle (Play Store)
./gradlew bundleRelease

# Check for issues
./gradlew lint

# Run tests
./gradlew test
```

---

## 📱 Screen Routes

```kotlin
// In NavGraph.kt
Routes.SPLASH              // App startup
Routes.LOGIN               // Login screen
Routes.REGISTER            // Registration
Routes.CUSTOMER_HOME       // Customer main screen
Routes.CUSTOMER_PROFILE    // Customer profile
Routes.PROVIDER_HOME       // Provider dashboard
Routes.PROVIDER_EARNINGS   // Earnings tracker
Routes.ADMIN_HOME          // Admin dashboard
```

---

## 🔐 Authentication Flow

```
1. User enters email & password
2. Firebase Auth creates account
3. User document saved in Firestore
4. Role (Customer/Provider/Admin) stored
5. App navigates to appropriate dashboard
```

### Login Test Credentials
- **Email**: `test@example.com`
- **Password**: `password123`

---

## 💾 Firestore Collections

```
users/
  {userId}
    - name
    - email
    - role

providers/
  {providerId}
    - userId
    - category
    - price
    - averageRating
    - totalBookings

bookings/
  {bookingId}
    - customerId
    - providerId
    - status (pending/accepted/completed)
    - price
    - rating
```

---

## 🎨 Color Palette

```kotlin
// Navy Theme
NavyPrimary = #1A237E      (Main brand color)
NavyAccent = #3949AB       (Highlights)

// Status Colors
SuccessGreen = #43A047     (Online, completed)
ErrorRed = #E53935         (Offline, errors)
WarningOrange = #FB8C00    (Pending, alerts)

// Neutral
White = #FFFFFF
DarkGray = #424242
```

---

## 📱 10 Features Implemented

1. ✅ **User Authentication** - Login/Register
2. ✅ **Browse Services** - 10 service categories
3. ✅ **Book Services** - Create booking request
4. ✅ **Track Bookings** - Real-time status timeline
5. ✅ **Rate Services** - Submit ratings & reviews
6. ✅ **Provider Analytics** - Earnings & performance
7. ✅ **Search & Filter** - Advanced provider search
8. ✅ **Dark Mode** - Light/dark theme toggle
9. ✅ **Offline Support** - Works without internet
10. ✅ **Favorites System** - Save providers

---

## 🔧 Common Development Tasks

### Add a New Screen

```kotlin
// 1. Create screen in ui/[feature]/NewScreen.kt
@Composable
fun NewScreen(
    onBack: () -> Unit = {},
    viewModel: SomeViewModel = viewModel()
) {
    // UI code here
}

// 2. Add route to NavGraph.kt
composable(Routes.NEW_SCREEN) {
    NewScreen(onBack = { navController.popBackStack() })
}

// 3. Navigate to it
navController.navigate(Routes.NEW_SCREEN)
```

### Add a New ViewModel Method

```kotlin
// In SomeViewModel.kt
fun doSomething(param: String) {
    _isLoading.value = true
    repository.doOperation(
        param = param,
        onSuccess = {
            _uiMessage.value = "Success!"
            _isLoading.value = false
        },
        onError = { error ->
            _uiMessage.value = error
            _isLoading.value = false
        }
    )
}
```

### Query Firestore Data

```kotlin
// In Repository
firestore.collection("providers")
    .whereEqualTo("category", category)
    .whereEqualTo("isOnline", true)
    .get()
    .addOnSuccessListener { snapshot ->
        val providers = snapshot.documents.map { doc ->
            Provider(
                id = doc.id,
                category = doc.getString("category") ?: "",
                price = doc.getDouble("price") ?: 0.0
            )
        }
        onResult(providers)
    }
    .addOnFailureListener { 
        onResult(emptyList())  // Error handling!
    }
```

---

## ⚠️ Important Patterns

### ✅ DO - Proper Error Handling
```kotlin
val userId = getCurrentUserId()
if (userId == null) {
    onError("User not authenticated")
    return
}
```

### ❌ DON'T - Silent Failures
```kotlin
val userId = getCurrentUserId() ?: return  // Bad!
```

### ✅ DO - Handle Listener Errors
```kotlin
.addSnapshotListener { snapshot, error ->
    if (error != null) {
        onResult(emptyList())
        return@addSnapshotListener
    }
    // Process data
}
```

### ✅ DO - Safe Type Casting
```kotlin
val data = (doc.get("field") as? List<*>)
    ?.mapNotNull { it as? String }
    ?: emptyList()
```

---

## 🧪 Testing Checklist

- [ ] App starts successfully
- [ ] Login/Register works
- [ ] Can book a service
- [ ] Can accept booking (as provider)
- [ ] Can rate service
- [ ] Search & filter work
- [ ] Dark mode toggles
- [ ] Offline mode works (toggle network)
- [ ] Profile updates save
- [ ] Earnings display correctly

---

## 📊 Data Models

### User
```kotlin
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String  // "Customer", "Provider", "Admin"
)
```

### Provider
```kotlin
data class Provider(
    val id: String,
    val userId: String,
    val name: String,
    val category: String,
    val price: Double,
    val isOnline: Boolean,
    val averageRating: Double,
    val totalBookings: Int,
    val completedBookings: Int,
    val totalEarnings: Double
)
```

### Booking
```kotlin
data class Booking(
    val id: String,
    val customerId: String,
    val providerId: String,
    val serviceCategory: String,
    val status: String,  // pending, accepted, completed
    val timestamp: Long,
    val rating: Double,
    val isRated: Boolean,
    val price: Double
)
```

---

## 🎯 Common Issues & Solutions

### Issue: App crashes on startup
**Solution**: Check Firebase google-services.json is in app/ folder

### Issue: Firestore queries return empty
**Solution**: 
1. Check if data exists in Firestore Console
2. Verify collection names match exactly
3. Check security rules allow read access

### Issue: Dark mode not working
**Solution**: Ensure ThemeViewModel is provided to NavGraph

### Issue: Offline mode shows no data
**Solution**: Make sure Room database is initialized and has cached data

### Issue: Build fails with error
**Solution**: Run `./gradlew clean build` to clear cache

---

## 📚 Learning Resources

- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Firebase Docs](https://firebase.google.com/docs)
- [Kotlin Docs](https://kotlinlang.org/docs)
- [Android Architecture](https://developer.android.com/topic/architecture)
- [MVVM Pattern](https://developer.android.com/jetpack/guide)

---

## 💡 Pro Tips

1. **Use Chrome DevTools** to debug Firestore queries
2. **Enable Firebase Emulator** for local testing
3. **Use Android Profiler** to check performance
4. **Keep components small** (< 200 lines)
5. **Test offline mode** frequently
6. **Monitor Firestore costs** in Firebase Console

---

## 🚀 Deployment Checklist

- [ ] Version number updated (versionCode + versionName)
- [ ] Firebase security rules configured
- [ ] All features tested on device
- [ ] Dark mode tested
- [ ] Offline mode verified
- [ ] App signing certificate ready
- [ ] Release notes prepared
- [ ] Screenshots captured
- [ ] Privacy policy ready
- [ ] Play Store listing complete

---

## 📞 Need Help?

1. **Check the full docs**: `PROJECT_DOCUMENTATION.md`
2. **Review code examples**: Check similar screens/ViewModels
3. **Look at error logs**: Logcat in Android Studio
4. **Check Firestore Console**: Verify data is there
5. **Test with emulator**: Faster than physical device

---

## 📝 Useful Commands

```bash
# View logs
./gradlew -Dorg.gradle.java.home=... clean build

# Install on device
./gradlew installDebug

# Uninstall from device
./gradlew uninstallDebug

# Format code
./gradlew spotlessApply

# Check dependencies
./gradlew dependencies

# Update dependencies
./gradlew wrapper --gradle-version=LATEST
```

---

## 🎓 Learning Path

1. **Day 1**: Understand MVVM + read this quick ref
2. **Day 2**: Study Jetpack Compose basics
3. **Day 3**: Explore Firebase integration
4. **Day 4**: Review existing screens + models
5. **Day 5**: Make your first feature modification
6. **Day 6-7**: Add a new feature from scratch

---

## ✅ You're Ready!

You now have everything to:
- ✅ Build & run the app
- ✅ Understand the codebase
- ✅ Add new features
- ✅ Deploy to Play Store
- ✅ Maintain the project

**Questions?** Check PROJECT_DOCUMENTATION.md for detailed information!

---

**Last Updated**: March 9, 2026  
**Status**: ✅ Production Ready

