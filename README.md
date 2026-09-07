# PlantCare

An Android app (Kotlin + Jetpack Compose) for tracking watering and fertilizing schedules for houseplants.

## Features

- Add a plant: name, type (affects how fast it "dries out"), pot diameter and height, photo.
- Automatic base watering interval calculated from pot volume and plant type.
- Weather-based adjustment (temperature, humidity, wind speed as a proxy for evaporation)
  via the free Open-Meteo API (no key required), using the device's last known location.
- User feedback loop: "Dried out early" / "Still moist" buttons on the plant screen,
  which train a personal correction factor for that specific plant.
- Custom fertilizer schedule per plant (interval in days, fertilizer name, notes).
- Notes field for watering peculiarities, plus a last-repotted-date field.
- Care history (watering, fertilizing, checks, repotting, dryness notes) on the plant detail screen.
- Notifications:
  - when a watering/fertilizing date is due (daily background check via WorkManager);
  - a general "check your plants" reminder roughly every 2–3 days.

## Project structure

```
app/src/main/java/com/uliana/plantcare/
├── data/
│   ├── local/            # Room: entities, DAOs, database, type converters
│   ├── remote/           # Retrofit interface and Open-Meteo models
│   └── repository/       # PlantRepository, WeatherRepository — all data access business logic
├── domain/
│   └── WateringCalculator.kt   # watering interval and weather-adjustment formulas
├── notification/
│   ├── NotificationHelper.kt
│   ├── BootReceiver.kt
│   └── workers/          # WorkManager workers (watering/fertilizing, general check)
├── ui/
│   ├── theme/            # Material 3 theme
│   ├── navigation/       # NavHost and routes
│   ├── plantlist/        # Plant list screen
│   ├── addedit/          # Add/edit plant screen
│   ├── detail/           # Plant detail screen (watering, fertilizing, notes, history)
│   └── components/       # Reusable composables (plant card)
├── PlantCareApplication.kt   # Manual DI + background task scheduling
└── MainActivity.kt           # Permissions (notifications, location) + NavHost entry point
```

## Building

1. Open the project folder in Android Studio (Koala or newer).
2. Let Gradle sync (internet is only needed to download dependencies on first build).
3. Run on a device or emulator with API 26+.

On first launch, the app will request notification permission (Android 13+) and location
permission (for weather-based watering adjustment). If you decline location access, the
watering interval calculation still works — just without weather adjustment, based only on
pot/plant parameters and your feedback.

## Watering calculation logic (summary)

1. **Base**: interval ~ square root of pot volume, divided by the plant category's
   evaporation coefficient (succulents dry out slower, tropical/flowering plants faster).
2. **Weather**: heat and wind shorten the interval; high air humidity lengthens it.
3. **Feedback**: marking "dried out early" decreases the plant's personal correction factor
   (water more often); marking "still moist" increases it (water less often). The factor is
   applied to all future calculations for that plant.

The formulas live in a single file, `domain/WateringCalculator.kt` — easy to tune to your
own experience (e.g. adjusting the reference temperature/humidity values).

## Possible next steps

- Settings screen with manual city/coordinates input instead of GPS.
- Home screen widget showing upcoming watering dates.
- Cloud backup for photos (currently only a `content://` URI from the gallery is stored).
- Tests for `WateringCalculator`.
