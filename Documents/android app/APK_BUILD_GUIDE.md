# 🚀 ServiceRadar - APK Building Guide

**Date**: March 10, 2026  
**Status**: Ready to Build  
**Version**: 1.0

---

## 📋 Quick Commands (Copy & Paste)

### Option 1: Build Debug APK (Fastest - for Testing)
```bash
cd /Users/sumeshvel/Documents/android\ app
./gradlew clean assembleDebug
```
**Time**: ~2-3 minutes  
**Output**: `app/build/outputs/apk/debug/app-debug.apk`  
**Use**: Testing on devices/emulator

### Option 2: Build Release APK (Recommended - for Play Store)
```bash
cd /Users/sumeshvel/Documents/android\ app
./gradlew clean assembleRelease
```
**Time**: ~3-5 minutes  
**Output**: `app/build/outputs/apk/release/app-release.apk`  
**Use**: Uploading to Google Play Store

### Option 3: Build Bundle AAB (Best for Play Store)
```bash
cd /Users/sumeshvel/Documents/android\ app
./gradlew clean bundleRelease
```
**Time**: ~3-5 minutes  
**Output**: `app/build/outputs/bundle/release/app-release.aab`  
**Use**: Recommended for Play Store submission

### Option 4: Everything at Once (Full Build)
```bash
cd /Users/sumeshvel/Documents/android\ app
./gradlew clean build
```
**Time**: ~5-10 minutes  
**Output**: All APK, AAB, and tests

---

## 🎯 Step-by-Step Build Guide

### Prerequisites Checklist
- ✅ Java 11 installed
- ✅ Android SDK installed
- ✅ gradle wrapper available
- ✅ google-services.json in app/ directory
- ✅ All dependencies downloaded

### Step 1: Navigate to Project Directory
```bash
cd /Users/sumeshvel/Documents/android\ app
```

### Step 2: Clean Previous Builds
```bash
./gradlew clean
```
This removes old build artifacts. Takes ~30 seconds.

### Step 3: Build the APK/AAB

#### For Testing (Debug APK):
```bash
./gradlew assembleDebug
```

#### For Play Store (Release APK):
```bash
./gradlew assembleRelease
```

#### For Play Store (Bundle AAB - RECOMMENDED):
```bash
./gradlew bundleRelease
```

### Step 4: Wait for Build to Complete
You'll see output like:
```
> Task :app:bundleRelease
...
BUILD SUCCESSFUL in 4m 32s
```

### Step 5: Find Your Built Files

**Debug APK**: 
```
app/build/outputs/apk/debug/app-debug.apk
```

**Release APK**: 
```
app/build/outputs/apk/release/app-release.apk
```

**Bundle (AAB)**: 
```
app/build/outputs/bundle/release/app-release.aab
```

---

## 🔐 Signing Your APK (Required for Play Store)

### What You Need:
- Keystore file (.jks)
- Keystore password
- Key alias
- Key password

### Option 1: Automatic Signing (Recommended)

Create `local.properties` file:
```properties
# Path to your keystore
KEYSTORE_PATH=/path/to/your/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

Then modify `build.gradle.kts` to use signing config:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file(System.getProperty("KEYSTORE_PATH") ?: "")
        storePassword = System.getProperty("KEYSTORE_PASSWORD")
        keyAlias = System.getProperty("KEY_ALIAS")
        keyPassword = System.getProperty("KEY_PASSWORD")
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        // ... rest of config
    }
}
```

### Option 2: Manual Signing (After Build)

```bash
# Sign the APK manually
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore /path/to/keystore.jks \
  app/build/outputs/apk/release/app-release.apk \
  your_key_alias
```

### Verify Signature:
```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

---

## 📊 Build Variants Explained

### Debug APK
- **Signing**: Automatically signed with debug key
- **Optimization**: Not optimized
- **Size**: Larger
- **Performance**: Slower
- **Use Case**: Development & testing
- **Build Time**: ~2 minutes

### Release APK
- **Signing**: Must sign manually or with config
- **Optimization**: ProGuard rules applied
- **Size**: Smaller
- **Performance**: Faster
- **Use Case**: Google Play Store
- **Build Time**: ~3-5 minutes

### Bundle AAB
- **Signing**: Must sign manually or with config
- **Optimization**: ProGuard rules applied
- **Size**: Smallest
- **Performance**: Fastest
- **Use Case**: Google Play Store (RECOMMENDED)
- **Build Time**: ~3-5 minutes

---

## 🐛 Troubleshooting Build Issues

### Issue 1: "Build Failed" or "Task Failed"

**Solution**:
```bash
./gradlew clean
./gradlew build --info
```

The `--info` flag shows detailed logs.

### Issue 2: Out of Memory

**Solution**: Increase Gradle heap size
```bash
export GRADLE_OPTS="-Xmx4096m"
./gradlew assembleRelease
```

### Issue 3: Firebase/Google Services Error

**Solution**: Verify `google-services.json` exists
```bash
ls -la app/google-services.json
```

Should show: `-rw-r--r-- ... google-services.json`

### Issue 4: Dependency Issues

**Solution**: Update dependencies
```bash
./gradlew dependencies --refresh-dependencies
./gradlew clean build
```

### Issue 5: Gradle Daemon Issues

**Solution**: Stop and restart daemon
```bash
./gradlew --stop
./gradlew clean assembleRelease
```

---

## 📦 Installing APK on Device

### Via USB Cable:
```bash
# Debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Release APK
adb install app/build/outputs/apk/release/app-release.apk
```

### Via Android Studio:
1. Open Android Studio
2. Build → Build & Run
3. Select target device

### Via Command Line (for specific device):
```bash
# List connected devices
adb devices

# Install on specific device
adb -s <device_id> install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📤 Uploading to Google Play Store

### Requirements:
- ✅ Play Store Developer Account ($25 one-time)
- ✅ Signed APK/AAB
- ✅ App title, description, icons
- ✅ Privacy policy URL
- ✅ Screenshots

### Steps:

1. **Go to Google Play Console**
   ```
   https://play.google.com/console
   ```

2. **Create New App** (if first time)
   ```
   Click "Create app"
   Enter app name: "ServiceRadar"
   Select category: Business
   Select type: App
   ```

3. **Upload APK/AAB**
   ```
   Release → Production
   Upload AAB or APK
   (AAB is recommended)
   ```

4. **Complete App Details**
   ```
   Title: ServiceRadar
   Description: Service booking app
   Screenshots: Add 2-8 screenshots
   Feature Graphic: Create banner
   Promo Video: Add if available
   ```

5. **Set Pricing & Distribution**
   ```
   Free or Paid
   Select target countries
   Content rating
   Target audience
   ```

6. **Review & Submit**
   ```
   Review all details
   Check compliance
   Submit for review
   ```

7. **Wait for Review**
   ```
   Estimated time: 2-4 hours
   You'll get email notification
   ```

---

## 📊 Build Configuration Details

### Your Current Config:
```
Namespace: com.example.serviceradar
Min SDK: 24 (Android 7.0)
Target SDK: 36 (Android 14)
Version Code: 1
Version Name: 1.0
Java Version: 11
Compose Enabled: Yes
Firebase: Enabled
```

### To Update Version:
Edit `app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = 2        // Increment this
    versionName = "1.1"    // Update this
    // ... rest of config
}
```

---

## ✅ Pre-Build Checklist

Before building, verify:

- [ ] Code compiles: `./gradlew build`
- [ ] No errors or warnings
- [ ] google-services.json present
- [ ] local.properties configured (if auto-signing)
- [ ] Keystore file exists (if signing)
- [ ] Version code & name updated
- [ ] ProGuard rules correct
- [ ] All dependencies resolved
- [ ] No uncommitted changes (git clean)

---

## 🎯 Quick Build Commands Summary

| Command | Purpose | Output | Time |
|---------|---------|--------|------|
| `./gradlew clean assembleDebug` | Test build | Debug APK | 2-3 min |
| `./gradlew clean assembleRelease` | Play Store APK | Release APK | 3-5 min |
| `./gradlew clean bundleRelease` | Play Store Bundle | AAB file | 3-5 min |
| `./gradlew clean build` | Full build | All outputs | 5-10 min |
| `./gradlew build --info` | Detailed build | All + logs | 5-10 min |

---

## 📝 Example: Complete Build & Sign Workflow

```bash
# Step 1: Navigate to project
cd /Users/sumeshvel/Documents/android\ app

# Step 2: Clean previous builds
./gradlew clean

# Step 3: Build release bundle (recommended for Play Store)
./gradlew bundleRelease

# Step 4: Find the output
ls -lh app/build/outputs/bundle/release/app-release.aab

# Step 5: (If not auto-signed) Sign the AAB
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore /path/to/keystore.jks \
  app/build/outputs/bundle/release/app-release.aab \
  your_key_alias

# Step 6: Verify signature
jarsigner -verify -verbose app/build/outputs/bundle/release/app-release.aab

# Done! Upload to Play Store
```

---

## 📞 Need Help?

### Common Build Output Messages:

**"BUILD SUCCESSFUL"**
→ Build worked! Check outputs folder

**"Execution failed"**
→ Check error message, see Troubleshooting section

**"DEPRECATION WARNING"**
→ Can ignore, won't affect build

**"Certificate is expired"**
→ Regenerate signing certificate

---

## 🚀 Next Steps After Building

1. ✅ Build the APK/AAB (follow guide above)
2. ✅ Sign the APK (if not auto-signed)
3. ✅ Test on device (install via adb)
4. ✅ Verify all features work
5. ✅ Upload to Google Play Console
6. ✅ Complete app listing details
7. ✅ Submit for review
8. ✅ Monitor approval process

---

**Ready to build? Start with:**
```bash
cd /Users/sumeshvel/Documents/android\ app
./gradlew clean assembleRelease
```

Good luck! 🚀

---

