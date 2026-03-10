# 🚀 APK BUILD - QUICK REFERENCE CARD

## Copy & Paste Commands

### 1️⃣ For Testing (Debug APK) - Fastest
```bash
cd /Users/sumeshvel/Documents/android\ app && ./gradlew assembleDebug
```
**Output**: `app/build/outputs/apk/debug/app-debug.apk`  
**Time**: 2-3 minutes  
**Use**: Install on your phone to test

### 2️⃣ For Play Store (Release APK)
```bash
cd /Users/sumeshvel/Documents/android\ app && ./gradlew assembleRelease
```
**Output**: `app/build/outputs/apk/release/app-release.apk`  
**Time**: 3-5 minutes  
**Use**: Upload to Google Play Store (requires signing)

### 3️⃣ For Play Store (Bundle AAB) - RECOMMENDED
```bash
cd /Users/sumeshvel/Documents/android\ app && ./gradlew bundleRelease
```
**Output**: `app/build/outputs/bundle/release/app-release.aab`  
**Time**: 3-5 minutes  
**Use**: Upload to Google Play Store (requires signing)

### 4️⃣ Full Clean Build
```bash
cd /Users/sumeshvel/Documents/android\ app && ./gradlew clean build
```
**Time**: 5-10 minutes  
**Use**: When you want everything fresh

---

## Install APK on Phone (After Debug Build)

```bash
adb install /Users/sumeshvel/Documents/android\ app/app/build/outputs/apk/debug/app-debug.apk
```

---

## Files After Build

| What | Where |
|------|-------|
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` |
| Release APK | `app/build/outputs/apk/release/app-release.apk` |
| Release AAB | `app/build/outputs/bundle/release/app-release.aab` |

---

## Signing (For Play Store Upload)

### Create Keystore (if you don't have one):
```bash
keytool -genkey -v -keystore ~/my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias serviceradar-key
```

### Sign APK:
```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore ~/my-release-key.jks \
  app/build/outputs/apk/release/app-release.apk \
  serviceradar-key
```

### Verify Signature:
```bash
jarsigner -verify -verbose app/build/outputs/apk/release/app-release.apk
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `gradlew: command not found` | `cd /Users/sumeshvel/Documents/android\ app` first |
| Build fails | `./gradlew clean` then rebuild |
| Out of memory | `export GRADLE_OPTS="-Xmx4096m"` |
| google-services.json error | Verify file exists in `app/` folder |

---

## Version Codes (Update Before Each Release)

Edit `app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = 1    // Increment this: 1 → 2 → 3
    versionName = "1.0" // Update: "1.0" → "1.1" → "1.2"
}
```

---

## Your Build Info

```
Project: ServiceRadar
Min SDK: 24 (Android 7.0)
Target SDK: 36 (Android 14)
Package: com.example.serviceradar
App Name: ServiceRadar
```

---

## Build Status Indicators

✅ **BUILD SUCCESSFUL** - Your APK is ready!  
❌ **BUILD FAILED** - Error occurred, check logs  
⚠️ **DEPRECATION WARNING** - Can ignore

---

## Time Estimates

- First build: 10-15 minutes (downloads dependencies)
- Subsequent builds: 3-5 minutes
- After small code change: 2-3 minutes
- Full clean rebuild: 5-10 minutes

---

## Next Steps After Building

1. ✅ Build APK using commands above
2. ✅ Test on device (debug APK)
3. ✅ Create signing key (if needed)
4. ✅ Sign release APK/AAB
5. ✅ Upload to Google Play Console
6. ✅ Submit for review

---

**Ready? Pick a command above and paste it in Terminal!** 🚀

