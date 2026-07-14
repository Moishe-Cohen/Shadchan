# Shadchan — Multiplatform (Android + Windows)

This is the Kotlin Multiplatform / Compose Multiplatform version of the Shadchan
app, migrated from the original single-module Android project so it can also
ship as a native Windows installer, while remaining one codebase.

## What changed vs. the Android-only version

| Concern | Android-only (before) | Multiplatform (this project) |
|---|---|---|
| Database | Room | SQLDelight (same SQLite, generates typed Kotlin from `Shadchan.sq`) |
| Settings storage | DataStore | DataStore on Android, `java.util.prefs` on desktop |
| File picking (photo/resume/backup) | `ActivityResultContracts` | `rememberOpenFileLauncher` / `rememberSaveFileLauncher` (expect/actual — Android SAF vs Swing `JFileChooser`) |
| Opening a resume file | `Intent.ACTION_VIEW` via FileProvider | `PlatformActions.openFile` (Android intent vs `java.awt.Desktop`) |
| ViewModel | `AndroidViewModel` | Plain class with its own `CoroutineScope`, works on both targets |
| UI (screens, components, theme, navigation) | Jetpack Compose | **Unchanged** — same Composables, now in `commonMain`, running on both targets |

Roughly 90% of your original UI and business logic code moved into
`commonMain` untouched or near-untouched. Only the pieces that talk directly
to Android APIs (Room, DataStore, SAF file pickers, Intents) were rewritten
behind `expect`/`actual` so each platform supplies its own implementation.

## Project layout

```
composeApp/
  src/
    commonMain/    — screens, components, theme, ViewModel, repository, data models (shared)
      sqldelight/  — Shadchan.sq: table schema + queries (replaces Room DAOs)
    androidMain/   — MainActivity, AndroidManifest, Room→SQLDelight driver, DataStore, SAF pickers
    desktopMain/   — Main.kt (Compose window), JDBC SQLite driver, JFileChooser, java.util.prefs
```

## Building the Android APK

```
./gradlew :composeApp:assembleDebug
```
Output: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

## Building the Windows installer

On a Windows machine (or via the GitHub Actions workflow below — `jpackage`
must run on the target OS to produce a native installer):

```
./gradlew.bat :composeApp:packageReleaseMsi :composeApp:packageReleaseExe
```
Output: `composeApp/build/compose/binaries/main-release/{msi,exe}/`

The installer bundles its own Java runtime, so nothing needs to be
pre-installed on the machine you install it to.

## Building both automatically (GitHub Actions)

`.github/workflows/build-all.yml` builds the Android APK on `ubuntu-latest`
and the Windows `.msi`/`.exe` on `windows-latest` on every push, and attaches
both as downloadable Artifacts on the workflow run page. Push this project to
a GitHub repo and check the Actions tab.

## Opening in Android Studio

Open the project root as-is — Android Studio (Giraffe+) understands
Kotlin Multiplatform modules and will show `androidMain` as the app source
set to run/debug like before. You'll need Android Studio's bundled JDK 17.

## Important — I could not compile-test this locally

This project was assembled and hand-verified line-by-line (SQL parameter
order, generated SQLDelight signatures, expect/actual pairings), but the
sandbox this was built in has no access to Google's or Maven Central's
package repositories, so I was not able to run an actual Gradle build here.
**Treat the first CI run as the real first compile** — dependency versions
(Compose Multiplatform, Navigation, SQLDelight, Coil3) may need small bumps
if something's moved since this was written. If a build fails, paste me the
error and I'll fix it directly.

## Known simplifications versus the original Android app

- The photo/resume/backup file pickers use plain OS dialogs (SAF on Android,
  `JFileChooser` on desktop) rather than anything fancier.
- Backup/restore on desktop stores its SQLite DB, photos, and resumes under
  `~/.shadchan/` instead of Android's app-private storage.
- No app icon has been set for the Windows build yet — add one under
  `nativeDistributions { windows { iconFile.set(...) } }` in
  `composeApp/build.gradle.kts` once you have an `.ico`.
