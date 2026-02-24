# 📱 Termux Tools Info - Cara Build APK

## ✅ Yang Sudah Selesai

Project Android **Termux Tools Info** telah selesai dibuat dengan struktur lengkap:

- ✓ Source code Java (MainActivity, Activities, Adapter, Models, Utils)
- ✓ Layout XML files
- ✓ Resources (colors, strings, themes)
- ✓ Gradle build configuration
- ✓ AndroidManifest.xml
- ✓ Launcher icons

## ⚠️ Status Build APK

**Java sudah terinstall**, tetapi **Android SDK lengkap tidak tersedia** di Termux.

## 🔨 3 Cara untuk Build APK

### CARA 1: GitHub Actions (PALING MUDAH) ⭐

1. **Upload ke GitHub:**
   ```bash
   cd ~/TermuxToolsInfo
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/USERNAME/REPO_NAME.git
   git push -u origin main
   ```

2. **APK akan otomatis terbuild** oleh GitHub Actions

3. **Download APK** dari tab Actions > Build > Artifacts

---

### CARA 2: Android Studio di PC

1. **Copy project ke PC:**
   ```bash
   cd ~/TermuxToolsInfo
   tar -czf termux-tools-info.tar.gz app build.gradle settings.gradle gradle.properties gradlew gradle local.properties
   ```

2. **Di PC:**
   - Extract file tar.gz
   - Buka Android Studio
   - File > Open > Pilih folder TermuxToolsInfo
   - Build > Build Bundle(s) / APK(s) > Build APK(s)

3. **APK ada di:** `app/build/outputs/apk/debug/app-debug.apk`

---

### CARA 3: Install Android SDK Manual (ADVANCED)

1. **Download command line tools:**
   - Kunjungi: https://developer.android.com/studio#command-tools
   - Download "Command line tools only"

2. **Extract dan setup:**
   ```bash
   mkdir -p ~/android-sdk/cmdline-tools
   cd ~/android-sdk/cmdline-tools
   unzip ~/downloads/commandlinetools-linux-*.zip
   mv cmdline-tools latest
   ```

3. **Setup environment:**
   ```bash
   export ANDROID_HOME=~/android-sdk
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
   ```

4. **Install SDK components:**
   ```bash
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;33.0.1"
   ```

5. **Build APK:**
   ```bash
   cd ~/TermuxToolsInfo
   ./gradlew assembleDebug
   ```

---

## 📦 Output

Setelah build berhasil, APK akan ada di:
```
TermuxToolsInfo/app/build/outputs/apk/debug/app-debug.apk
```

## 🎯 Fitur App

- ✅ List semua package Termux yang terinstall
- ✅ Detail package (nama, versi, deskripsi, ukuran)
- ✅ Browser scripts (.sh, .py, .js)
- ✅ System information (Android version, CPU, memory, storage)
- ✅ Pull-to-refresh
- ✅ Material Design UI
- ✅ Dark/Light theme support

## 📋 Requirements

- Android 7.0+ (API 24+)
- Termux (untuk full functionality)

## 🔗 Quick Links

- Project folder: `~/TermuxToolsInfo`
- Source archive: `~/TermuxToolsInfo/builds/termux-tools-info-source.tar.gz`
- GitHub workflow: `.github/workflows/build.yml`

---

## 💡 Rekomendasi

**Gunakan GitHub Actions (CARA 1)** karena:
- ✅ Tidak perlu install Android SDK
- ✅ Build otomatis setiap push
- ✅ APK tersedia untuk download
- ✅ Free untuk public repository
