# Smart Vehicle Service Manager

Android app (Kotlin) for small vehicle service centers — offline-first with
Room Database, auto-sync to Firebase, CameraX photo capture, GPS garage
location, and service reminder notifications.

## What's included
- `app/src/main/java/.../data/` — Room entities, DAOs, repositories
- `app/src/main/java/.../sync/` — Firebase Firestore sync (WorkManager background job)
- `app/src/main/java/.../notification/` — service reminder notifications
- `app/src/main/java/.../ui/` — Login/Register (Firebase Auth), Dashboard,
  Customers, Vehicles (with camera + GPS capture), Service History
- Full Gradle project (Kotlin DSL), ready to open in Android Studio

## Setup — step by step

### 1. Install prerequisites
- Android Studio (latest stable) — https://developer.android.com/studio
- A free Firebase account — https://console.firebase.google.com

### 2. Open the project
- Unzip this file anywhere on your PC
- Android Studio → **Open** → select the unzipped `SmartVehicleServiceManager` folder
- Let Gradle sync (first sync downloads dependencies — needs internet, takes a while,
  be patient especially on an i3 PC)

### 3. Connect Firebase (required for Auth + Sync to work)
1. Go to the Firebase Console → **Add project** → name it anything (e.g. "SmartVehicleService")
2. Inside the project, click **Add app → Android**
3. Package name: `com.example.smartvehicleservice` (must match exactly)
4. Download the generated **google-services.json**
5. Copy it into the `app/` folder of this project, replacing
   `app/google-services.json.PLACEHOLDER` — rename it to exactly `google-services.json`
6. In Firebase Console:
   - **Authentication** → Sign-in method → enable **Email/Password**
   - **Firestore Database** → Create database → start in **test mode** (fine for a student project)

### 4. Run the app
- Connect your Oppo A1K via USB (see the earlier connection guide — enable
  Developer Options + USB Debugging)
- In Android Studio, select your phone from the device dropdown
- Click ▶ **Run**
- First launch: **Register** a new account, then log in

### 5. Try the features
- Add a **Customer**
- Add a **Vehicle** for that customer → capture a photo → capture GPS location
- Open a vehicle → add a **Service Record** with a reminder (in days)
- Turn on **Airplane Mode**, add another customer/vehicle — it still saves
  (Room/offline). Turn Wi-Fi back on and wait a few minutes — the background
  `SyncWorker` pushes unsynced records to Firestore automatically
  (list items show "Pending sync…" until then, then "Synced ✓")

## Coverage vs. your proposal (all items now implemented)
- Camera capture uses the **real CameraX API** (`Preview` + `ImageCapture` use
  cases bound to a `PreviewView` in `CameraCaptureActivity`) — not just a
  system-camera shortcut
- Search & Filter works for **both** Customers and Vehicles
- Dashboard shows live counts (customers, vehicles, service records, pending
  sync) plus the next 5 upcoming service reminders
- Long-press any customer or vehicle row to delete it (with a confirmation dialog)

## Notes for your report / demo
- Offline-first architecture: every screen writes to Room first, never directly to Firebase
- `isSynced` flag on each entity tracks what still needs to go to the cloud
- `SyncWorker` (WorkManager, `NetworkType.CONNECTED` constraint) runs every 15 minutes
  and on connectivity regain
- `ReminderWorker` runs every 6 hours and fires a local notification for any
  service record whose reminder date has passed
- CameraX capture uses the system camera via `ActivityResultContracts.TakePicture()`
  with a `FileProvider`, and the photo path is stored in Room + synced to Firestore
- GPS uses `FusedLocationProviderClient` (Google Play Services)

## If Gradle sync fails
- Make sure you're connected to the internet (first sync downloads Android/Kotlin/Firebase libraries)
- File → Invalidate Caches / Restart if it gets stuck
- Make sure `google-services.json` is in the `app/` folder (not the project root)
