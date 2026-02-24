# Termux Tools Info

A comprehensive Android application for viewing installed packages, scripts, and system information in Termux.

## Features

- **Package Viewer**: View all installed Termux packages with details
- **Scripts Browser**: Browse and manage your scripts (.sh, .py, .js)
- **System Information**: Detailed system information including:
  - Android version and device info
  - CPU and memory information
  - Storage details
  - Termux-specific information

## Requirements

- Android 7.0 (API 24) or higher
- Termux installed (for full functionality)
- Java JDK 8 or higher
- Android SDK

## Building from Source

### Method 1: Using Build Script

```bash
cd ~/TermuxToolsInfo
./build.sh
```

### Method 2: Using Gradle Directly

```bash
cd ~/TermuxToolsInfo

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease
```

### Install Required Dependencies

```bash
# In Termux
pkg update
pkg install openjdk-17
pkg install android-tools
```

## Project Structure

```
TermuxToolsInfo/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/termux/tools/info/
│   │       │   ├── MainActivity.java
│   │       │   ├── PackageDetailsActivity.java
│   │       │   ├── ScriptsActivity.java
│   │       │   ├── SystemInfoActivity.java
│   │       │   ├── adapter/
│   │       │   │   └── PackageAdapter.java
│   │       │   ├── model/
│   │       │   │   ├── PackageInfo.java
│   │       │   │   └── ScriptInfo.java
│   │       │   └── util/
│   │       │       └── TermuxScanner.java
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   ├── values/
│   │       │   ├── menu/
│   │       │   └── drawable/
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
│   └── wrapper/
├── build.gradle
├── settings.gradle
├── gradlew
└── build.sh
```

## Output APK Location

After successful build, the APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Installation

```bash
# Install the APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or copy to device and install manually
pm install app/build/outputs/apk/debug/app-debug.apk
```

## Screenshots

The app features:
- Material Design UI
- Dark/Light theme support
- Pull-to-refresh functionality
- Package search
- Detailed package information

## Technologies Used

- **Language**: Java
- **UI Framework**: AndroidX, Material Components
- **Build Tool**: Gradle
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

## Permissions

- `READ_EXTERNAL_STORAGE`: To scan scripts in storage
- `WRITE_EXTERNAL_STORAGE`: To save package information
- `INTERNET`: For package information lookup

## Contributing

Feel free to submit issues and enhancement requests!

## License

MIT License - feel free to use this project for learning or personal use.

## Author

Created for Termux users who want to manage their packages and scripts easily.

## Disclaimer

This app is designed to work with Termux. Some features may require Termux to be installed on the device.
