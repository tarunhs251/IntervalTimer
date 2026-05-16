# IntervalTimer - Customizable Interval Training App

A modern **Kotlin + Jetpack Compose** Android app for interval training with customizable work/rest durations, sets, and alarm sounds. Built with MVVM architecture, Material 3 design, and background service support.

## 🎯 Features

✅ **Customizable Timer**
- Set custom work duration (seconds)
- Set custom rest duration (seconds)
- Configure number of sets
- Session summary with total time calculation

✅ **Active Training Screen**
- Large countdown display (MM:SS format)
- Current set/set total counter
- Work/Rest mode indicator with color animation
- Circular + linear progress bars
- Pause, Resume, Skip, and Finish controls

✅ **Alarm/Buzzer Sounds**
- System buzzer tone (880 Hz, 500ms)
- Custom audio file selection from device storage
- Alarm plays on every set/rest switch

✅ **Background Support**
- Foreground service for background timer operation
- Persistent notification with controls
- Pause/Resume/Skip/Finish buttons in notification
- Screen stays on via WakeLock

✅ **Data Persistence**
- SharedPreferences saves last used settings
- Auto-load on app restart

✅ **Material 3 Design**
- Light and dark theme support
- Smooth animations and color transitions
- Responsive UI for all screen sizes
- Modern typography and spacing

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + StateFlow
- **State Management**: ViewModel + StateFlow
- **Database**: SharedPreferences
- **Services**: Foreground Service + Notifications
- **Sound**: MediaPlayer (custom) + AudioTrack (buzzer)
- **Threading**: Kotlin Coroutines
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## 📱 Project Structure

```
IntervalTimer/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/intervaltimer/
│   │   │   ├── MainActivity.kt              # Entry point, navigation
│   │   │   ├── TimerSettings.kt             # Data class
│   │   │   ├── TimerUIState.kt              # UI state sealed class
│   │   │   ├── viewmodel/
│   │   │   │   └── TimerViewModel.kt        # MVVM logic, timer control
│   │   │   ├── service/
│   │   │   │   └── TimerService.kt          # Foreground service + notifications
│   │   │   ├── data/
│   │   │   │   └── PreferencesManager.kt    # SharedPreferences wrapper
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── SetupScreen.kt       # Settings configuration UI
│   │   │       │   └── TimerScreen.kt       # Active countdown UI
│   │   │       └── theme/
│   │   │           ├── IntervalTimerTheme.kt
│   │   │           ├── Type.kt               # Typography system
│   │   │           └── Shape.kt              # Shape system
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml              # String resources
│   │   │   │   ├── colors.xml               # Light theme colors
│   │   │   │   └── themes.xml               # Theme definitions
│   │   │   ├── values-night/
│   │   │   │   └── colors.xml               # Dark theme colors
│   │   │   └── xml/
│   │   │       ├── data_extraction_rules.xml
│   │   │       └── backup_rules.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                     # App-level Gradle config
│   └── proguard-rules.pro                   # Code obfuscation rules
├── gradle/
│   └── libs.versions.toml                   # Version catalog
├── build.gradle.kts                         # Root-level Gradle config
├── settings.gradle.kts                      # Project configuration
├── .gitignore
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Electric Eel or newer
- Android SDK 24 (API level 7.0) or higher
- Kotlin 1.9.10+

### Build & Run

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd IntervalTimer
   ```

2. **Open in Android Studio**
   - File → Open → Select IntervalTimer folder
   - Wait for Gradle sync to complete

3. **Build the app**
   ```bash
   ./gradlew build
   ```

4. **Run on emulator or device**
   ```bash
   ./gradlew installDebug
   ```
   Or use Android Studio's Run button (Shift + F10)

## 📋 How to Use

### Setup Screen
1. Enter **Work Duration** (seconds) - time for active interval
2. Enter **Rest Duration** (seconds) - time between sets
3. Enter **Number of Sets** - total cycles
4. Choose alarm sound:
   - **System Buzzer** - built-in tone
   - **Custom Audio File** - select from device storage
5. Review **Session Summary** (total time)
6. Tap **Start Training**

### Timer Screen
- **Large countdown display** shows time remaining
- **Color coding**: Red for work, Green for rest
- **Set counter** shows current set (e.g., "Set 2 of 5")
- **Progress bars** (circular + linear) show interval completion
- **Controls**:
  - ⏸️ **Pause** - pause timer (resume available when paused)
  - ⏭️ **Skip** - move to next interval immediately
  - ✕ **Finish** - end session and return to setup

### Background Operation
- Timer continues if app is backgrounded
- Notification shows current status and controls
- Tap notification to bring app to foreground
- All controls (pause, resume, skip, finish) work from notification

### Settings Persistence
- Last used settings auto-load when app opens
- Changes saved automatically to SharedPreferences

## 🔧 Key Implementation Details

### Timer Logic (TimerViewModel.kt)
- 100ms tick rate for smooth countdown
- State-based UI updates via StateFlow
- Automatic switch between work/rest modes
- Auto-completion after final set

### Alarm System
- **Custom sound**: MediaPlayer with content URI
- **Buzzer**: Programmatic sine wave generation (880 Hz)
- Auto-release after playback
- Fallback to buzzer if custom sound fails

### Background Service (TimerService.kt)
- Extends Service for foreground operation
- Creates NotificationChannel (Android 8+)
- Supports notification control actions:
  - Pause intent
  - Resume intent
  - Skip intent
  - Finish intent (stops service)
- START_STICKY mode for persistence

### Screen Management (WakeLock)
- PowerManager.WakeLock prevents screen from sleeping
- 10-minute timeout
- Released on pause/finish

### Permissions
- **WAKE_LOCK** - keep screen on during session
- **POST_NOTIFICATIONS** - show persistent notification (Android 13+)
- **READ_EXTERNAL_STORAGE** - access custom audio files
- **READ_MEDIA_AUDIO** - access audio files (Android 13+)

## 📦 Dependencies

### Core
- `androidx.core:core-ktx` - Core Android extensions
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle awareness
- `androidx.activity:activity-compose` - Compose integration

### Compose
- `androidx.compose.ui:ui` - Base UI components
- `androidx.compose.material3:material3` - Material Design 3
- `androidx.compose.material:material-icons-extended` - Icon library

### State Management
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel + Compose
- `androidx.lifecycle:lifecycle-runtime-compose` - Recomposition control

### Async
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Coroutine utilities
- `org.jetbrains.kotlinx:kotlinx-coroutines-core` - Core coroutines

### Optional
- `androidx.work:work-runtime-ktx` - WorkManager for scheduled tasks

## 🎨 Theming

- **Material 3 Dynamic Colors** support on Android 12+
- **Light theme**: Purple primary, blue secondary
- **Dark theme**: Lavender primary, light blue secondary
- **Adaptive**: Auto-switches based on system settings

## 📝 Building for Release

1. **Configure signing**
   - Create/use keystore for signing
   - Add to `local.properties` or gradle.properties

2. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```

3. **Build release AAB** (for Play Store)
   ```bash
   ./gradlew bundleRelease
   ```

## 🐛 Troubleshooting

### Custom alarm sound not playing
- Check file format (MP3, WAV supported)
- Verify READ_EXTERNAL_STORAGE permission granted
- Check file is accessible from Documents or Downloads

### Timer not working in background
- Verify WAKE_LOCK permission
- Check notification is showing (Android 13+ requires POST_NOTIFICATIONS)
- Ensure app isn't force-stopped

### UI not updating
- Confirm ViewModel is properly initialized
- Check StateFlow collectors are active
- Verify recomposition conditions met

## 📄 License

This project is provided as-is for educational and development purposes.

## 🤝 Contributing

Feel free to fork, modify, and distribute. Contributions welcome!

---

**Last Updated**: May 2026  
**Min SDK**: 24  
**Target SDK**: 34  
**Kotlin**: 1.9.10+
