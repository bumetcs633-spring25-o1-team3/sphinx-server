# Running the Application

## Prerequisites
- JDK 17 or higher
- Gradle 7.x or higher
- PostgreSQL database

## Build and Run

### Using Gradle Wrapper
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

## Configuration

### Environment Variables
Create a `.env` file in the project root with the following variables:
```properties
POSTGRES_HOST=your-database-host
POSTGRES_DB=your-database-name
POSTGRES_USER=your-username
POSTGRES_PASSWORD=your-password
```

Note: Make sure to add `.env` to your `.gitignore` file.

### Application Configuration
The application uses YAML configuration files located in `src/main/resources/`:

- `application.yml` - Default configuration
- `application-dev.yml` - Development environment settings
- `application-prod.yml` - Production environment settings

To specify which profile to use, set the `SPRING_PROFILES_ACTIVE` environment variable:
```properties
SPRING_PROFILES_ACTIVE=dev  # For development
SPRING_PROFILES_ACTIVE=prod # For production
```

## Troubleshooting
- For build errors, run: `./gradlew clean build`