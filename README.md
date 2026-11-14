# NBA Challenge — Android

Starter project for the NBA technical challenge described in the supplied screenshots.

## Stack

- Kotlin + Jetpack Compose
- MVVM / Clean Architecture (single Gradle module, package boundaries)
- Coroutines + Flow
- Retrofit + OkHttp
- Room
- Paging 3 dependencies prepared
- JUnit, MockK, Coroutines Test, Turbine and MockWebServer
- Android Gradle Plugin unit-test coverage
- GitHub Actions CI

## Important API note

The current BALLDONTLIE NBA API requires an API key in the `Authorization` header and uses cursor pagination (`meta.next_cursor`). The API host is `https://api.balldontlie.io/`; NBA routes use the `nba/v1/...` prefix.

Create an API key in the BALLDONTLIE account portal, then provide it to Gradle without committing it:

```bash
./gradlew assembleDebug -PBALLDONTLIE_API_KEY=YOUR_API_KEY
```

For Android Studio, add this to your user-level `~/.gradle/gradle.properties`:

```properties
BALLDONTLIE_API_KEY=YOUR_API_KEY
```

Do not put API keys in source control. For a production app, a key embedded in a mobile binary can be extracted; a backend proxy is the safer approach.

Official API docs: https://docs.balldontlie.io/

## Open the project

1. Open this directory in Android Studio.
2. Install Android SDK 35.
3. Use JDK 17 for the Gradle build.
4. Configure Gradle 8.11.1 in the IDE if prompted.
5. Configure the API key as described above.

This starter archive does not include the binary Gradle Wrapper JAR. Android Studio can sync it using a local Gradle installation; alternatively, generate the standard wrapper from a machine with Gradle 8.11.1 installed:

```bash
gradle wrapper --gradle-version 8.11.1
```

## Run

After Gradle is available:

```bash
gradle assembleDebug
gradle testDebugUnitTest
gradle createDebugUnitTestCoverageReport
```

The HTML coverage report is generated under:

`app/build/reports/coverage/test/debug/index.html`

## Current implementation status

This is a starter scaffold, not a finished implementation of every screen in the challenge.

Included:
- Compose Home and Players tab shells.
- Team sorting UI sample.
- Retrofit API contracts for teams, games and player search.
- Room database, team/game entities and DAOs.
- Team repository using Room as the observable local source.
- One example unit test.
- GitHub Actions workflow for unit tests, coverage artifact and debug APK.

Next implementation steps:
1. Wire repositories and ViewModels into Compose screens using a dependency injection strategy.
2. Add a Paging 3 `RemoteMediator` for games and persist remote pagination keys.
3. Implement player search with debounce and cancellation.
4. Add the player-to-team navigation flow and reuse the team-games screen.
5. Add repository, DAO, RemoteMediator, ViewModel and Compose UI tests.
6. Add a coverage threshold (line and branch) once a meaningful baseline exists.
7. Add emulator-based instrumented tests as a separate CI job.

## API behavior to keep in mind

The API uses cursor-based pagination, not offset pagination. Use `meta.next_cursor` from each response as the next request's `cursor`. The current free tier may also impose rate limits. Handle 401, 429 and transient server errors explicitly.
