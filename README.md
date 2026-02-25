# Termux Tools Info

A comprehensive Android application with **embedded Real Termux Terminal** - just like AIDE-Termux!

## 🎉 New Features (v1.0)

- **🖥️ Embedded Real Termux Terminal** - Full Termux terminal experience without installing Termux app!
- **Package Viewer**: View all installed Termux packages with details
- **Scripts Browser**: Browse and manage your scripts (.sh, .py, .js)
- **System Information**: Detailed system information

## 📱 Screenshots

The app features:
- Real Termux terminal emulator (green on black)
- Material Design UI
- Dark/Light theme support
- Pull-to-refresh functionality
- Package search

## 🚀 Building from Source

### Method 1: GitHub Actions (Recommended)

The APK will be automatically built when you push to the repository:

1. Push your code to GitHub
2. Go to **Actions** tab
3. Select the workflow run
4. Download the APK from artifacts

### Method 2: Local Build with Android Studio

```bash
# Clone the repository
git clone https://github.com/Kztutorial99/TermuxToolsInfo.git
cd TermuxToolsInfo

# Open in Android Studio
# Let Gradle sync download Termux modules
# Build > Build Bundle(s) / APK(s) > Build APK(s)
```

### Method 3: Local Build with Gradle

```bash
cd ~/TermuxToolsInfo

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease
```

## 📋 Requirements

- Android Studio Arctic Fox or newer
- JDK 17 or higher
- Android SDK (API 24+)
- NDK 25.2.9519653 (for Termux terminal-emulator)

## 🏗️ Project Structure

```
TermuxToolsInfo/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── java/com/termux/tools/info/
│   │   │   ├── MainActivity.java
│   │   │   ├── TerminalActivity.java    # Real Termux Terminal
│   │   │   ├── PackageDetailsActivity.java
│   │   │   ├── ScriptsActivity.java
│   │   │   ├── SystemInfoActivity.java
│   │   │   ├── adapter/
│   │   │   ├── model/
│   │   │   └── util/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── terminal-emulator/            # Termux terminal emulator (from termux-app)
├── terminal-view/                # Termux terminal view (from termux-app)
├── termux-shared/                # Termux shared utilities (from termux-app)
├── .github/workflows/
│   └── build.yml                 # GitHub Actions workflow
├── gradle/
├── build.gradle
├── settings.gradle
└── gradlew
```

## 🔧 Technologies Used

- **Language**: Java
- **Terminal**: Real Termux Libraries (terminal-emulator, terminal-view, termux-shared)
- **UI Framework**: AndroidX, Material Components
- **Build Tool**: Gradle 7.6.3+
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **NDK**: 25.2.9519653

## 📦 APK Output Location

```
app/build/outputs/apk/debug/app-debug.apk
```

Or in the `Apk/` folder after build:
```
Apk/TermuxToolsInfo-v1.0-Terminal.apk
```

## 📲 Installation

```bash
# Via ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Or copy to device and install manually
pm install app/build/outputs/apk/debug/app-debug.apk
```

## 🔐 Permissions

- `READ_EXTERNAL_STORAGE`: To scan scripts in storage
- `WRITE_EXTERNAL_STORAGE`: To save package information
- `INTERNET`: For package information lookup
- `FOREGROUND_SERVICE`: For terminal session

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 📄 License

MIT License - feel free to use this project for learning or personal use.

## 👨‍💻 Author

Created for Termux users who want to manage their packages and scripts easily.

## 📝 Special Thanks

- [Termux App](https://github.com/termux/termux-app) - For the amazing terminal emulator
- [AIDE-Termux](https://github.com/victor20010/AIDE-Termux) - For inspiration on integrating Termux

## ⚠️ Disclaimer

This app includes the real Termux terminal emulator libraries. The terminal functionality works standalone without requiring the Termux app to be installed.
