#!/usr/bin/env bash
echo "========================================================"
echo "   PLACEMENT - Smart Campus Placement Management System"
echo "========================================================"
find src/main/java -name "*.java" > sources.txt
mkdir -p out
javac -d out @sources.txt

if [ "$1" == "--web" ]; then
    echo "Starting Web Server at http://localhost:8080"
    java -cp out placement.Main --web
else
    java -cp out placement.Main
fi

