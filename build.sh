#!/bin/bash
# Coffee Shop Management - Build Script

echo "Compiling Java files..."
cd "$(dirname "$0")"

# Create bin directory if not exists
mkdir -p bin

# Compile all Java files
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin -sourcepath src src/Main.java src/view/*.java src/model/*.java src/dao/*.java src/controller/*.java 2>&1

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Running app..."
    java -cp "bin:lib/mysql-connector-j-9.6.0.jar" Main
else
    echo "Compilation failed!"
    exit 1
fi
