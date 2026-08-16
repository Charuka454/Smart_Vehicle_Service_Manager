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

