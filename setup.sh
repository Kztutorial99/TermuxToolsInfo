#!/bin/bash

# Termux Tools Info - Setup and Build Guide
# Run this script to set up the build environment

echo "========================================"
echo "  Termux Tools Info - Setup Guide"
echo "========================================"
echo ""

# Check if running in Termux
if [ -d "/data/data/com.termux" ]; then
    echo "✓ Running in Termux environment"
    echo ""
    
    echo "To build this project, you need to install Java:"
    echo ""
    echo "  pkg update && pkg upgrade"
    echo "  pkg install openjdk-17"
    echo ""
    
    echo "After installing Java, run:"
    echo "  cd ~/TermuxToolsInfo"
    echo "  ./gradlew assembleDebug"
    echo ""
    echo "Or use the build script:"
    echo "  ./build.sh"
    echo ""
else
    echo "Note: This project is designed for Termux environment"
    echo ""
    echo "Requirements:"
    echo "  - Java JDK 8 or higher"
    echo "  - Android SDK"
    echo "  - Gradle"
    echo ""
    echo "Build commands:"
    echo "  ./gradlew assembleDebug"
    echo ""
fi

echo "========================================"
echo "  Project Structure Created!"
echo "========================================"
echo ""
echo "Project location: ~/TermuxToolsInfo"
echo ""
echo "Key files:"
echo "  - app/src/main/java/.../MainActivity.java"
echo "  - app/src/main/AndroidManifest.xml"
echo "  - app/build.gradle"
echo "  - build.sh"
echo ""
