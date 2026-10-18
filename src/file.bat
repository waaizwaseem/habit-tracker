@echo off
echo Compiling Habit Tracker...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/*" -d bin src/models/*.java src/services/*.java src/persistence/*.java src/utils/*.java src/ui/*.java src/main/*.java

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo Running Habit Tracker...
    java -cp "bin;lib/*" main.HabitTrackerApp
) else (
    echo Compilation failed!
    pause
)
