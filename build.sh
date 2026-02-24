#!/bin/bash

# Termux Tools Info - Build Script
# This script builds the APK

set -e

PROJECT_DIR="/data/data/com.termux/files/home/TermuxToolsInfo"
cd "$PROJECT_DIR"

echo "========================================"
echo "  Termux Tools Info - Build Script"
echo "========================================"
echo ""

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed!"
    echo "Please install Java: pkg install openjdk-17"
    exit 1
fi

# Check if Android SDK is available
if [ ! -d "/data/data/com.termux/files/usr" ]; then
    echo "ERROR: Termux environment not found!"
    exit 1
fi

echo "Java version:"
java -version 2>&1 | head -1
echo ""

# Create simple icon if not exists
echo "Creating launcher icons..."
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi

# Create a simple XML-based icon placeholder
cat > app/src/main/res/mipmap-hdpi/ic_launcher.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#009688"/>
</shape>
EOF

cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-hdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-mdpi/ic_launcher.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-mdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xhdpi/ic_launcher.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xhdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xxhdpi/ic_launcher.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xxhdpi/ic_launcher_round.xml

echo ""
echo "Starting Gradle build..."
echo ""

# Run Gradle build
if [ -f "./gradlew" ]; then
    ./gradlew assembleDebug --stacktrace
else
    echo "ERROR: gradlew not found!"
    exit 1
fi

echo ""
echo "========================================"
echo "  Build Complete!"
echo "========================================"
echo ""
echo "APK location:"
find app/build/outputs/apk -name "*.apk" 2>/dev/null || echo "APK not found, check build logs"
echo ""
