#!/bin/bash

# Advanced Programming Final Project - Run Script

echo "🚀 Advanced Programming - Agent Graph Manager"
echo "============================================="

# Create build directory if it doesn't exist
if [ ! -d "build" ]; then
    echo "📁 Creating build directory..."
    mkdir -p build
fi

# Clean any .class files from src directories
echo "🧹 Cleaning src directories..."
find src -name "*.class" -delete 2>/dev/null || true

# Compile Java files to build directory
echo "🔨 Compiling Java files..."
if javac -cp src -d build $(find src -name "*.java"); then
    echo "✅ Compilation successful!"
    echo "📦 All .class files saved to build/ directory"
else
    echo "❌ Compilation failed!"
    exit 1
fi

# Create necessary directories
echo "📂 Setting up directories..."
mkdir -p config_files
mkdir -p html_files

# Run the application
echo "🌟 Starting server..."
echo "📱 Once started, open http://localhost:8080 in your browser"
echo "⏹️  Press Ctrl+C to stop the server"
echo ""

java -cp build server.WebApplication
