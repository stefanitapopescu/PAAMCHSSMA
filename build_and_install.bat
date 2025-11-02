@echo off
REM Script pentru build și instalare aplicație Android
REM Windows Batch Script

echo ========================================
echo   Build si Instalare - Navigare 3D
echo ========================================
echo.

echo [1/4] Cleaning previous builds...
call gradlew clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)
echo.

echo [2/4] Building debug APK...
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

echo [3/4] Verificare device conectat...
adb devices
echo.

echo [4/4] Instalare APK...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Install failed!
    echo Asigura-te ca ai un device conectat si USB debugging activat.
    pause
    exit /b 1
)
echo.

echo ========================================
echo   SUCCESS! Aplicatia a fost instalata
echo ========================================
echo.
echo Deschide aplicatia pe device.
pause

