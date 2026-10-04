@echo off

title JavaOS Virtual Environment
cd /d "%~dp0"

color 07
cls

if not exist out mkdir out

dir /s /b src\main\java\*.java > sources.txt

javac -encoding UTF-8 -cp "lib/*" -d out @sources.txt

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

del sources.txt

java -cp "out;lib/*" com.javaos.Main

echo.
pause