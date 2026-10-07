# PakSchool SMS - Cross-Platform School Management (Flutter)

Complete cross-platform School Management System converted to **Flutter & Dart**.

---

## 🚀 How to Run in VS Code

### Step 1: Open in VS Code
1. Download or export the project files.
2. Open the `flutter_project` folder inside **VS Code**.
3. Make sure you have the **Flutter** & **Dart** extensions installed in VS Code.

### Step 2: Install Dependencies
Open your terminal in VS Code and run:
```bash
flutter pub get
```

### Step 3: Run the App
Connect your Android phone or start an emulator and run:
```bash
flutter run
```

---

## 📦 How to Build and Publish

### 1. Build Android Release APK:
```bash
flutter build apk --release
```
The APK will be generated at:
`build/app/outputs/flutter-apk/app-release.apk`

### 2. Build Android App Bundle (AAB for Google Play Store):
```bash
flutter build appbundle --release
```
The AAB will be generated at:
`build/app/outputs/bundle/release/app-release.aab`

Upload this `.aab` file to your **Google Play Console**.

---

## 📂 Project Architecture

```
flutter_project/
├── pubspec.yaml            # Project dependencies & configurations
├── lib/
│   ├── main.dart           # App entry point with Material 3 navigation
│   ├── database/
│   │   └── db_helper.dart  # SQLite offline database (Students, Attendance, Fees, Staff)
│   ├── models/
│   │   ├── student.dart    # Student entity
│   │   ├── attendance.dart # Attendance entity
│   │   ├── fee_record.dart # Fee records & ledger
│   │   ├── staff.dart      # Staff & payroll entity
│   │   └── broadcast.dart  # WhatsApp / SMS alert entity
│   ├── providers/
│   │   └── school_provider.dart # Reactive state management with ChangeNotifier
│   └── screens/
│       ├── dashboard_screen.dart # Clean dashboard with target class grid & 4 feature cards
│       ├── attendance_screen.dart # Daily roll call & attendance tracking
│       ├── fee_screen.dart       # Fee collections, challans & payment recording
│       ├── staff_screen.dart     # Faculty directory & payroll management
│       └── broadcast_screen.dart # WhatsApp alerts & quick notification templates
```

---

## ✨ Features Included
- **Target Class Selector Grid**: Instant filtering across all Pakistani grades (KG, Nursery, Prep, Class 1 to 10, 1st & 2nd Year).
- **Currently Viewing Database Banner**: Dynamic class indicator with real-time student count.
- **Offline SQLite Database**: Zero cloud dependency; fast local persistence using `sqflite`.
- **Fee Management**: Track Paid, Pending, and Defaulter statuses, and record cash/digital payments.
- **Attendance**: One-click "All Present" and individual status toggle.
- **WhatsApp Integration**: Launch WhatsApp directly with pre-composed student notices.
