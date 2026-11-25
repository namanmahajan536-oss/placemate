@echo off
echo ========================================================
echo    PLACEMENT - Smart Campus Placement Management System
echo ========================================================
echo 1. Launch Terminal Application
echo 2. Launch Modern Web Server ^& Dashboard
echo 3. Compile all Java sources
echo ========================================================
set /p opt="Select an option (1-3): "

if "%opt%"=="1" (
    echo Compiling and running Terminal Console...
    dir /s /B src\main\java\*.java > sources.txt
    javac -d out @sources.txt
    java -cp out placement.Main
) else if "%opt%"=="2" (
    echo Starting Web Server on http://localhost:8080...
    dir /s /B src\main\java\*.java > sources.txt
    javac -d out @sources.txt
    java -cp out placement.Main --web
) else if "%opt%"=="3" (
    echo Compiling Java code...
    dir /s /B src\main\java\*.java > sources.txt
    javac -d out @sources.txt
    echo Done!
) else (
    echo Invalid choice.
)
pause

