# INSTALLATION GUIDE - Termux Tools Info

## Quick Start (In Termux)

### Step 1: Install Java

```bash
pkg update
pkg install openjdk-17
```

### Step 2: Navigate to Project

```bash
cd ~/TermuxToolsInfo
```

### Step 3: Build APK

```bash
# Option A: Using build script
./build.sh

# Option B: Using Gradle directly
chmod +x gradlew
./gradlew assembleDebug
```

### Step 4: Find APK

After successful build, APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Step 5: Install APK

```bash
# Copy to accessible location
cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/

# Or install directly
pm install app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### Error: Java not found
```bash
pkg install openjdk-17
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
```

### Error: SDK not found
The project is configured to use Termux's default path.
Edit `local.properties` if needed.

### Error: Gradle wrapper not found
```bash
chmod +x gradlew
```

### Build fails with memory error
Edit `gradle.properties`:
```
org.gradle.jvmargs=-Xmx1024m
```

## Manual APK Location

If build succeeds, find APK at:
```
TermuxToolsInfo/app/build/outputs/apk/debug/app-debug.apk
```

## Testing

After installation:
1. Open "Termux Tools Info" app
2. Grant storage permissions if requested
3. View installed packages
4. Browse scripts
5. Check system information

## Requirements

- Android 7.0+ (API 24+)
- 50MB free storage
- Termux (for full functionality)
