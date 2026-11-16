# NBA Android App

An Android application developed with Kotlin to explore NBA information, focusing on software engineering best practices, clean architecture, local data persistence, and automated testing.

## Technologies

- **Kotlin** – primary programming language.
- **Jetpack Compose** – declarative UI toolkit.
- **MVVM** – presentation layer architecture pattern.
- **Clean Architecture** – separation of presentation, domain, and data layers.
- **Kotlin Coroutines and StateFlow** – asynchronous programming and state management.
- **Hilt** – dependency injection.
- **Retrofit and OkHttp** – API communication.
- **Room** – local data persistence.
- **Paging 3** – paginated player loading.
- **JUnit 5 and MockK** – unit testing.
- **Jetpack Compose UI Testing** – UI testing.

## Features

- Browse NBA teams.
- Browse and search NBA players with pagination support.
- View games for a selected team.
- Persist data locally using Room.
- Access previously stored information when the device is offline.

## Architecture

The project follows a Clean Architecture approach, organized into three main layers.

### Presentation

Responsible for the user interface and screen state management.

- Jetpack Compose.
- ViewModels.
- StateFlow.
- Navigation Compose.

### Domain

Contains business rules and application contracts.

- Models.
- Use Cases.
- Repository interfaces.

### Data

Responsible for data access and integration with external services.

- Repository implementations.
- Retrofit API.
- Room Database.
- DAOs.
- DTOs and data mapping.

The data flow follows this structure:

`Presentation → Domain → Data`

## Requirements

To run the project, you will need:

- Android Studio.
- JDK 17.
- Android SDK 35.
- An Android device or emulator running Android 8.0 (API 26) or higher.
- A [Ball Don't Lie API](https://www.balldontlie.io/) key.

## API Key Configuration

The application uses the Ball Don't Lie API to retrieve NBA data.

The API key will be provided separately via email.

Before running the application, you must configure the key as a Gradle property.

### Recommended option: configure user-level Gradle properties

Open the `gradle.properties` file located in your user-level Gradle directory.

Common locations:

**macOS / Linux**

`~/.gradle/gradle.properties`

**Windows**

`%USERPROFILE%\.gradle\gradle.properties`

Add the following property:

```properties
BALLDONTLIE_API_KEY=your_api_key
```

Replace `your_api_key` with the key provided by email.

Do not wrap the value in quotation marks.

### Alternative: configure project-level Gradle properties

You can also add the property to the `gradle.properties` file located in the project root:

```properties
BALLDONTLIE_API_KEY=your_api_key
```

If you choose this option, make sure the file containing the API key is not committed to the repository.

**Important:** Never expose your API key publicly or commit files containing sensitive credentials to GitHub.

### How the API key is used

The project reads the property through Gradle:

```kotlin
buildConfigField(
    "String",
    "BALLDONTLIE_API_KEY",
    "\"${project.findProperty("BALLDONTLIE_API_KEY") ?: ""}\""
)
```

The key is made available to the Android application through the `BuildConfig` class.

After configuring the property, synchronize the project with Gradle in Android Studio.

## Running the Application

1. Clone the repository:

```bash
git clone <REPOSITORY_URL>
```

2. Open the project in Android Studio.

3. Configure the API key as described in the previous section.

4. Wait for Gradle synchronization to complete.

5. Select an Android device or start an emulator.

6. Run the application from Android Studio.

### Build from the command line

To build the Debug variant:

```bash
./gradlew :app:assembleDebug
```

On Windows:

```bash
gradlew.bat :app:assembleDebug
```

## Tests

The project includes unit tests covering business logic, ViewModels, and repository implementations.

To run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

## Test Coverage

The project uses the JaCoCo integration provided by the Android Gradle Plugin.

To generate the unit test coverage report:

```bash
./gradlew :app:createDebugUnitTestCoverageReport
```

The HTML report is generated