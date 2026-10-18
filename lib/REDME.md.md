# External Libraries Directory

This directory is reserved for external JAR files and libraries.

## Currently Used Libraries
- **Built-in Java Swing**: For GUI components
- **Java Time API**: For date/time handling
- **Java Security**: For password hashing

## Potential Future Libraries
- **Jackson/Gson**: For JSON data persistence
- **SQLite JDBC**: For database storage
- **Apache Commons**: For utility functions
- **JFreeChart**: For progress charts and statistics
- **Log4j**: For application logging

## Adding Libraries
1. Download the JAR file
2. Place it in this directory
3. Add to classpath: `java -cp ".:lib/*" main.HabitTrackerApp`

## Build Instructions
To compile and run the application:

```bash
# Compile
javac -cp ".:lib/*" -d bin src/**/*.java

# Run
java -cp "bin:lib/*" main.HabitTrackerApp
