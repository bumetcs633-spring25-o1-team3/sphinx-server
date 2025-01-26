# Running the Application

## Prerequisites
- JDK 17 or higher
- Gradle 7.x or higher

## Build and Run

### Using Gradle Wrapper
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### Using JAR file
```bash
# Build JAR
./gradlew build

# Run JAR
java -jar build/libs/<project-name>.jar
```

## Configuration
- Application configuration is in `src/main/resources/application.properties`

## Troubleshooting
- For build errors, run: `./gradlew clean build`