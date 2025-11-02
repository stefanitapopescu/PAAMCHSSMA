#!/bin/bash
# Script pentru build și instalare aplicație Android
# Linux/Mac Shell Script

echo "========================================"
echo "  Build si Instalare - Navigare 3D"
echo "========================================"
echo ""

echo "[1/4] Cleaning previous builds..."
./gradlew clean
if [ $? -ne 0 ]; then
    echo "ERROR: Clean failed!"
    exit 1
fi
echo ""

echo "[2/4] Building debug APK..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed!"
    exit 1
fi
echo ""

echo "[3/4] Verificare device conectat..."
adb devices
echo ""

echo "[4/4] Instalare APK..."
adb install -r app/build/outputs/apk/debug/app-debug.apk
if [ $? -ne 0 ]; then
    echo "ERROR: Install failed!"
    echo "Asigura-te ca ai un device conectat si USB debugging activat."
    exit 1
fi
echo ""

echo "========================================"
echo "  SUCCESS! Aplicatia a fost instalata"
echo "========================================"
echo ""
echo "Deschide aplicatia pe device."

