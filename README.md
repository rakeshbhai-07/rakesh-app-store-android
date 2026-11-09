# RAKESH APP STORE — Native Android

Kotlin + Jetpack Compose + Material 3. This project is a native Android app and does not use a WebView.

## Included
- Firebase Realtime Database project list (`projects` node)
- Search, category filter and sorting
- Native project detail dialog
- Rewarded ad gate before starting a download
- DownloadManager-based downloads
- Local download history
- Native YouTube and Telegram link intents
- GitHub Actions debug APK build

## Firebase
The app is configured with the Firebase web app values supplied in the existing HTML project. For a production Android app, register `com.aizoid.unityadsdemo` as an Android app in Firebase Console and use its Android configuration. The Realtime Database rules must permit the intended reads and writes; do not make a production database public just to make the app work.

The project expects data under `/projects/{projectId}` with fields:
`name`, `description`, `category`, `version`, `size`, `logo`, `fileURL`, `rating`, `downloads`, `enabled`.

## Unity Ads
Configured values:
- Game ID: `800430632`
- Rewarded: `BP_Rewarded_Android`
- Interstitial: `BP_Interstitial_Android` (declared for later placement)
- Banner: `BP_Banner_Android` (declared for later placement)
- Test mode: enabled

Rewarded download behavior: the download is started only when Unity Ads reports `COMPLETED`. Skipped ads, failed ads, and ads that are not ready do not start the download.

## Build APK on GitHub
1. Upload the project contents to the repository root.
2. Open the repository's **Actions** tab.
3. Select **Build Android APK** and tap **Run workflow** (or push to `main`).
4. Open the completed workflow run and download the `RAKESH-APP-STORE-debug` artifact.
5. Extract the ZIP and install `app-debug.apk` on your Android phone.

## Important
This initial project is a buildable starter implementation. Test the Firebase Android app registration, database permissions, Unity Ads placements and real device behavior before public release. Banner and interstitial placements are configured as constants but are not yet displayed in this first build.
