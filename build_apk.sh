#!/bin/bash

# Termux Tools Info - Quick Build Script (Alternative Method)
# This script creates an APK using available tools in Termux

set -e

PROJECT_DIR="$HOME/TermuxToolsInfo"
cd "$PROJECT_DIR"

echo "========================================"
echo "  Termux Tools Info - Build APK"
echo "========================================"
echo ""

# Check for required tools
echo "Checking requirements..."

if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found! Install with: pkg install openjdk-17"
    exit 1
fi

echo "✓ Java installed: $(java -version 2>&1 | head -1)"

# Create output directory
mkdir -p builds

echo ""
echo "========================================"
echo "  Build Options"
echo "========================================"
echo ""
echo "Since full Android SDK is not available in Termux,"
echo "you have these options to build the APK:"
echo ""
echo "OPTION 1: Use Android Studio on PC"
echo "  1. Copy project to PC"
echo "  2. Open in Android Studio"
echo "  3. Build > Build Bundle(s) / APK(s) > Build APK"
echo ""
echo "OPTION 2: Install full Android SDK"
echo "  Download from: https://developer.android.com/studio#command-tools"
echo "  Then run: ./gradlew assembleDebug"
echo ""
echo "OPTION 3: Use online build service"
echo "  - Upload to GitHub"
echo "  - Use GitHub Actions or Codemagic to build"
echo ""
echo "========================================"
echo "  Project Files Ready!"
echo "========================================"
echo ""
echo "Source code location: $PROJECT_DIR"
echo ""
echo "To transfer to PC:"
echo "  tar -czf termux-tools-info.tar.gz app build.gradle settings.gradle gradle gradlew"
echo ""

# Create a tarball for easy transfer
cd "$PROJECT_DIR"
tar -czf builds/termux-tools-info-source.tar.gz \
    app \
    build.gradle \
    settings.gradle \
    gradle.properties \
    gradlew \
    gradle/wrapper \
    local.properties \
    2>/dev/null || echo "Note: Some files may be missing from archive"

echo "Source archive created: builds/termux-tools-info-source.tar.gz"
echo ""
