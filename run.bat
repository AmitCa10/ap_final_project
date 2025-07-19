@echo off
echo 🚀 Advanced Programming - Agent Graph Manager
echo =============================================

REM Create build directory if it doesn't exist
if not exist "build" (
    echo 📁 Creating build directory...
    mkdir build
)

REM Clean any .class files from src directories
echo 🧹 Cleaning src directories...
for /r src %%i in (*.class) do del "%%i" >nul 2>&1

REM Compile Java files to build directory
echo 🔨 Compiling Java files...
javac -cp src -d build src\graph\*.java src\configs\*.java src\server\*.java src\servlet\*.java src\servlets\*.java src\views\*.java src\Main.java
if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo 📦 All .class files saved to build\ directory

REM Create necessary directories
echo 📂 Setting up directories...
if not exist "config_files" mkdir config_files
if not exist "html_files" mkdir html_files

REM Run the application
echo 🌟 Starting server...
echo 📱 Once started, open http://localhost:8080/app/index.html in your browser
echo ⏹️  Press Ctrl+C to stop the server
echo.

java -cp build Main

pause
