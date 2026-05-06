@echo off
echo ================================
echo   Building RielItem Plugin
echo   LoyaltyMC - /loyalty command
echo ================================

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven not found! Please install Maven first.
    echo Download: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Building...
mvn clean package -q
if %errorlevel% neq 0 (
    echo BUILD FAILED!
    pause
    exit /b 1
)

echo.
echo ================================
echo  SUCCESS!
echo  JAR: target\RielItem_v1_1_loyalty.jar
echo  Copy it to your server plugins folder
echo ================================
pause
