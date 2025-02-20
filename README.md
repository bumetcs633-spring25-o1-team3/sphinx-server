# Sphinx

A web application that facilitates learning through interactive flashcards and study tools, specifically designed for MET CS 633 students.

## Prerequisites

- JDK 17 or higher
- Gradle 7.x or higher
- PostgreSQL database
- Google OAuth 2.0 credentials

## Configuration

### Environment Variables

Create a `.env` file in the root directory with the following variables:

```properties
# Database Configuration
POSTGRES_HOST=your_postgres_host
POSTGRES_DB=your_database_name
POSTGRES_USER=your_database_user
POSTGRES_PASSWORD=your_database_password

# Google OAuth 2.0
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
PORT=8085

# Frontend URL
APP_FRONTEND_URL=http://localhost:3000
```

### Google OAuth 2.0 Setup

The application uses Google OAuth 2.0 for user authentication. For development:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to Credentials > Create Credentials > OAuth Client ID
5. Configure the OAuth consent screen
6. Create Web Application credentials
7. Add authorized redirect URIs:
    - http://localhost:8085/login/oauth2/code/google
8. Copy the Client ID and Client Secret to your `.env` file

## Database Setup

The application uses PostgreSQL. For development:

1. Create a PostgreSQL database:
```sql
CREATE DATABASE sphinx_db;
```

2. The application will automatically create the necessary tables when running with the `dev` and `prod` profile (`ddl-auto: update`).

## Build and Run

### Using Gradle Wrapper

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### Using Docker

```bash
# Build the Docker image
docker build -t sphinx-server .

# Run the container
docker run -p 8085:8085 --env-file .env sphinx-server
```

## Testing

```bash
# Run tests
./gradlew test
```

The application uses H2 in-memory database for testing (configured in `application-test.yml`).

## API Endpoints

### Authentication

- `GET /oauth2/authorization/google` - Initiate Google OAuth2 login
- `GET /auth/user` - Get current authenticated user

### Flashcard Sets

- `GET /flashcard-set` - Get all flashcard sets
- `GET /flashcard-set/my-sets` - Get current user's flashcard sets 
- `GET /flashcard-set/{id}` - Get a specific flashcard set
- `POST /flashcard-set` - Create a new flashcard set
- `PUT /flashcard-set/{id}` - Update a flashcard set
- `DELETE /flashcard-set/{id}` - Delete a flashcard set

### Flashcards

- `POST /flashcard` - Create a new flashcard
- `GET /flashcard/set/{setId}` - Get all flashcards in a set
- `GET /flashcard/{id}` - Get a specific flashcard
- `PUT /flashcard/{id}/set/{setId}` - Update a flashcard
- `DELETE /flashcard/{id}/set/{setId}` - Delete a flashcard

## Security

The application implements the following security measures:

- Google OAuth2 authentication
- CORS configuration for frontend integration
- Session-based authentication
- Protected API endpoints
- CSRF protection (disabled for API endpoints)

## Deployment

The application is configured for deployment on Render:

1. Push changes to the master branch
2. GitHub Actions workflow will:
    - Run tests
    - Trigger deployment on Render

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── edu/bu/metcs/sphinx/
│   │       ├── controller/   # REST controllers
│   │       ├── model/        # Entity classes
│   │       ├── repository/   # JPA repositories
│   │       ├── service/      # Business logic
│   │       ├── dto/          # Data Transfer Objects
│   │       └── security/     # Security configuration
│   └── resources/
│       └── application.yml   # Application configuration
└── test/
    └── java/
        └── edu/bu/metcs/sphinx/
            └── integration/  # Integration tests
```

## Contributing

1. Create a new branch for your feature
2. Write tests for new functionality
3. Create a pull request
4. Ensure all tests pass in GitHub Actions
