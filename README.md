<div align = "center">

# ReLaunch

<img width="512" height="512" alt="power-button (2)" src="https://github.com/user-attachments/assets/57ff0abb-847d-4c22-9023-9218d58ad589" />

> Open the Android system power menu without using your physical power button.

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com) [![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org) [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose) [![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Developed by [MiSFiT-SeCuRiTY](https://github.com/MiSFiT-SeCuRiTY)**

</div>
---

## Download

**[⬇️ Download ReLaunch v1.0.0 APK](https://github.com/MiSFiT-SeCuRiTY/ReLaunch/releases/latest)**

Requires **Android 8.0 (API 26)** or newer. The power menu feature requires **Android 11 (API 30)** or newer.

---

## What ReLaunch Does

ReLaunch uses Android's official **`AccessibilityService`** framework to invoke the documented `GLOBAL_ACTION_POWER_DIALOG` system action. When your device permits it, this opens the same power menu that appears after a long-press of the physical power button — giving you **Restart**, **Power off**, and **Emergency** options.

ReLaunch is **honest about capability**. It never claims to do something the OS does not expose. If your device blocks the request, the app tells you clearly and offers alternatives.

---

## What ReLaunch Does NOT Do

- ❌ **Cannot reboot or power off directly.** Those actions require `android.permission.REBOOT` (signature-level, system-only) or Device Owner privileges.
- ❌ **Does not simulate physical button presses.** No root, no input injection, and no shell access.
- ❌ **Does not use hidden APIs or reflection** to fake system capabilities.
- ❌ **Does not request `SHOW_POWER_MENU`.** That permission is signature-level and cannot be granted to normal apps.
- ❌ **Does not read your screen, keystrokes, or app content.** The accessibility service is configured with `canRetrieveWindowContent="false"` and does not observe events.
- ❌ **Has no internet permission.** It cannot phone home, send analytics, or download anything.

You can verify these claims by inspecting [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml). The app does not declare any `<uses-permission>` entries.

---

## Capability Matrix

| Setup | Open Power Menu | Reboot | Shutdown |
|---|---|---|---|
| **Unprivileged app on Android 11+** | ✅ With accessibility enabled | ❌ | ❌ |
| **Unprivileged app on Android 8–10** | ❌ No public API exists | ❌ | ❌ |
| **Unprivileged app on OEM ROM** (Samsung One UI, MIUI, etc.) | ⚠️ Varies | ❌ | ❌ |
| **Device Owner** (via `adb shell dpm`) | ✅ Works | ✅ `DevicePolicyManager.reboot()` | ⚠️ Via DPM capabilities |
| **Rooted device** | ✅ Works | ✅ `su -c reboot` | ✅ `su -c reboot -p` |
| **System-signed app** (firmware build) | ✅ Works | ✅ | ✅ |

ReLaunch targets the **first row** because that is what regular users can install and use.

---

## Installation

### Option 1 — Prebuilt APK (Easiest)

1. Visit the [ReLaunch Releases](https://github.com/MiSFiT-SeCuRiTY/ReLaunch/releases/latest) page.
2. Download the latest `ReLaunch-v1.0.0.apk`.
3. On your phone, allow installation from unknown sources when prompted.
4. Install the APK.

### Option 2 — Build from Source

**Requirements:**

- Android Studio Ladybug (2024.2) or newer
- JDK 17
- Android SDK Platform 35

Clone the repository:

```bash
git clone https://github.com/MiSFiT-SeCuRiTY/ReLaunch.git
cd ReLaunch
```

**Build on macOS or Linux:**

```bash
./gradlew assembleDebug
```

**Build on Windows:**

```bat
gradlew.bat assembleDebug
```

The generated APK will be located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install on a connected device:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Alternatively, open the project in Android Studio and click **Run ▶**.

---

## ⚠️ First-Time Setup (Android 13+)

Because ReLaunch is sideloaded and uses an `AccessibilityService`, Android 13+ may block the accessibility toggle by default. This is a security policy for sideloaded accessibility apps, not a ReLaunch-specific warning or proof of malware.

**You may need to do the following once:**

1. Install ReLaunch.
2. Open ReLaunch and tap **Open Accessibility Settings**. Alternatively, navigate to **Settings → Accessibility → Downloaded apps**.
3. Try to enable **ReLaunch Power Menu**.
4. If Android displays **Restricted setting**, acknowledge the warning.
5. Go to **Settings → Apps → ReLaunch**.
6. Tap the **⋮ (three-dot menu)** at the top-right of the App Info screen.
7. Select **Allow restricted settings**, if available.
8. Return to **Settings → Accessibility** and toggle **ReLaunch Power Menu** ON.
9. Confirm the warning dialog.

ReLaunch is now ready to use.

**On Android 12 and below**, this restriction generally does not apply; enable accessibility normally.

**ADB installation:** Installing with `adb install` may avoid the restricted-settings block on some devices, but behavior depends on the Android version and manufacturer.

---

## Why Does Android Restrict Accessibility Apps?

Android's accessibility framework is powerful and has been abused by malware in the past. Starting with Android 13, Google introduced restrictions that can prevent sideloaded apps from enabling accessibility without additional user consent.

This is a security measure designed to protect users.

ReLaunch respects this policy and asks users to enable the service consciously.

---

## Usage

1. Open ReLaunch.
2. Enable its accessibility service when prompted.
3. Tap the **Tap to Open** button.
4. If supported by your device, the system power menu appears.
5. Choose **Restart**, **Power off**, or another available system option.

If the button is disabled or the action fails, ReLaunch displays the relevant status and diagnostics.

---

## Privacy

- ✅ No analytics.
- ✅ No network requests.
- ✅ No screen content access.
- ✅ No keystroke logging.
- ✅ No logging of user actions.
- ✅ No internet permission requested.

The accessibility service is intended to respond only to calls from within the app itself.

---

## Known Limitations

1. **OEM variability:** Some manufacturers or firmware builds may block `GLOBAL_ACTION_POWER_DIALOG` for accessibility services. ReLaunch reports when the feature is unavailable. The physical power button or the manufacturer's alternative controls may still be necessary.
2. **Android version limitations:** The public power-menu action is supported on Android 11 (API 30) and later. On Android 8–10, ReLaunch displays an explanation instead of offering a nonfunctional button.
3. **Accessibility service interruptions:** Android may stop the service under certain conditions, including resource pressure. ReLaunch checks service availability and prompts the user to re-enable it when needed.
4. **No direct reboot or shutdown:** Ordinary third-party apps cannot directly reboot or shut down an Android device without privileged permissions or appropriate device-management capabilities. Device Owner provisioning and root access are outside this app's scope.

---

## Tech Stack

- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM with `StateFlow`, organized into `ui/`, `domain/`, and `service/`
- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 35 (Android 15)
- **Build System:** Gradle with Kotlin DSL

### Project Structure

```text
app/src/main/java/com/misfit/relaunch/
├── MainActivity.kt
├── ReLaunchApplication.kt
├── domain/
│   ├── CompatibilityChecker.kt
│   ├── PowerMenuInvoker.kt
│   └── model/
│       ├── CompatibilityStatus.kt
│       └── PowerMenuResult.kt
├── service/
│   └── PowerAccessibilityService.kt
└── ui/
    ├── HomeScreen.kt
    ├── HomeViewModel.kt
    └── theme/
        └── Material 3 theme files
```

**Key components:**

- `MainActivity.kt` — Compose activity entry point.
- `ReLaunchApplication.kt` — Application-level dependency holder.
- `CompatibilityChecker.kt` — Detects API level and accessibility service status.
- `PowerMenuInvoker.kt` — Defines the power-menu invocation interface and default implementation.
- `CompatibilityStatus.kt` — Represents device compatibility information.
- `PowerMenuResult.kt` — Represents the outcome of a power-menu request.
- `PowerAccessibilityService.kt` — Implements the accessibility-based power-menu mechanism.
- `HomeScreen.kt` — Main Compose user interface.
- `HomeViewModel.kt` — Holds UI state and coordinates business logic.
- `theme/` — Contains Material 3 theme files.

---

## Contributing

Contributions are welcome!

1. Fork the repository.
2. Create a feature branch.
3. Make your changes and add or update tests where appropriate.
4. Follow the existing code style.
5. Submit a pull request.

For major changes, open an issue first to discuss your proposal.

---

## License

This project is licensed under the **MIT License**. See the [`LICENSE`](LICENSE) file for details.

---

## Credits

**Developed by [MiSFiT-SeCuRiTY](https://github.com/MiSFiT-SeCuRiTY)**

Icon design, code, and documentation by the same developer.

---

<p align="center">
  <b>ReLaunch — Power control, without the power button.</b>
</p>
