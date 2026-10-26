# Cookable

Cookable is an Android application developed as my final-year university project for my Software Development degree at Munster Technological University (MTU).

The project focuses on making everyday cooking and kitchen organisation easier by bringing cupboard management, recipes, and shopping together in one application.

## Features

- **Cupboard:** A dedicated section for managing kitchen ingredients.
- **Recipes:** A section for recipe-related functionality.
- **Shopping:** A dedicated section for shopping-related functionality.
- **Navigation:** A bottom navigation interface for moving between the main sections of the application.

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Design System:** Material 3
- **Navigation:** Navigation Compose
- **Database:** Room
- **Networking:** Retrofit and Gson
- **Image Loading:** Coil Compose
- **Build System:** Gradle with Kotlin DSL
- **Testing:** JUnit and AndroidX testing libraries

## Getting Started

### Prerequisites

To build and run Cookable locally, you will need:

- [Android Studio](https://developer.android.com/studio)
- [Git](https://git-scm.com/)
- An Android device or emulator running Android 14 (API 34) or later
- A compatible JDK for the project's Gradle configuration

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Ganainmtech/Cookable.git
   ```

2. Navigate to the project directory:

   ```bash
   cd Cookable
   ```

3. Open the project in Android Studio.

4. Allow Gradle to sync and download the required dependencies.

5. Start an Android emulator or connect a compatible Android device.

6. Select the `app` run configuration and click **Run**.

### Building from the Command Line

To build a debug APK, run the following command from the project root:

```bash
./gradlew assembleDebug
```

On Windows, use:

```bat
gradlew.bat assembleDebug
```

The generated APK will be located in:

```text
app/build/outputs/apk/debug/
```

## Project Structure

```text
Cookable/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   ├── res/
│       │   └── AndroidManifest.xml
│       ├── test/
│       └── androidTest/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── README.md
```

## Testing

Run the unit tests using Gradle:

```bash
./gradlew test
```

Run instrumented Android tests on a connected device or emulator using:

```bash
./gradlew connectedAndroidTest
```

## Project Background

Cookable was developed as my final-year university project as part of my Software Development studies at Munster Technological University.

The project provided an opportunity to apply software engineering principles and Android development technologies to a practical application, including user interface development, application architecture, navigation, and data management.

## Future Improvements

Potential areas for future development include:

- Expanding cupboard and ingredient management.
- Enhancing recipe organisation and discovery.
- Connecting available ingredients with shopping requirements.
- Improving the overall user experience.

## License

No license has been specified for this repository. Unless a license is added, the project remains subject to the default applicable copyright protections.

---

Developed as a final-year Software Development project at Munster Technological University (MTU).
