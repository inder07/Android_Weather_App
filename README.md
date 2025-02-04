# Android Weather App

## Overview
This is an Android weather application that allows users to view current weather conditions, hourly forecasts, and a 5-day forecast for their current location or any searched location. The app retrieves weather data using the OpenWeatherMap API and supports switching between Celsius and Fahrenheit units. It also includes local data caching for improved performance and reduced API calls.

## Features
- Display current weather conditions
- Show hourly and 5-day weather forecasts
- Support for multiple locations (search by city name or ZIP code)
- Unit conversion between Celsius and Fahrenheit
- Caching mechanism using Room to reduce API calls
- Graceful error handling (e.g., API failures, network errors, or location issues)
- User-friendly UI

## Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit
- **Local Storage**: Room Database
- **Asynchronous Programming**: Coroutines
- **Minimum SDK**: Android 6.0 (Marshmallow, API Level 23)

---

## Setup Instructions

### 1. Clone the Repository
```sh
  git clone https://github.com/inder07/Android_Weather_App.git
  cd Android_Weather_App
```

### 2. Obtain an OpenWeatherMap API Key
- Sign up at [OpenWeatherMap](https://openweathermap.org/)
- Generate an API key from your account

### 3. Add API Key to the Project
Navigate to data/remote/Constants in the root directory of the project and replace the following line with your api key:
```
APP_ID=your_api_key_here
```

### 4. Build and Run the App
#### Using Android Studio:
1. Open **Android Studio**
2. Click **File > Open**, then select the cloned project directory
3. Sync Gradle by clicking "Sync Now"
4. Run the app using **Run > Run 'app'**

#### Using Command Line:
```sh
./gradlew build
./gradlew installDebug
```

---

## Dependencies
This project uses the following dependencies:
```gradle
dependencies {
    //Gson
    implementation("com.google.code.gson:gson:2.9.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    //Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")

    //Room database
    implementation("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //Dagger - Hilt
    implementation(libs.com.google.dagger.hilt.android)
    ksp(libs.com.google.dagger.hilt.android.compiler)

    //Picasso
    implementation("com.squareup.picasso:picasso:2.71828")
}
```

---

## Troubleshooting
1. **Gradle Sync Issues**: Ensure you have the latest stable version of **Android Studio** and that Gradle is updated.
2. **API Key Not Working**: Double-check your API key and ensure it has the correct permissions.
3. **App Crashes on Launch**: Run `adb logcat` or check the log in Android Studio for errors.
4. **Network Errors**: Verify that your device/emulator has an active internet connection.

---

## Contributing
Feel free to fork this repository and contribute! Follow these steps:
1. **Fork** the repo
2. **Create a feature branch** (`git checkout -b feature-branch`)
3. **Commit changes** (`git commit -m "Added new feature"`)
4. **Push** to your forked repo (`git push origin feature-branch`)
5. **Create a pull request**

---

