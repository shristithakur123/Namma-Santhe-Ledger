
# 📱 **Namma Santhe Ledger**

> A simple Android-based digital ledger application developed for small vendors and shopkeepers to manage customer credit (Udari/Khata) and payment records efficiently.
> The app replaces traditional paper notebooks with a secure and user-friendly digital solution.

---

# 🚩 **Problem Statement**

Small vendors and local shopkeepers often maintain customer credit records manually in notebooks.
This traditional method creates several challenges such as:

* Loss or damage of records
* Manual calculation mistakes
* Difficulty tracking pending payments
* Time-consuming management
* No proper reminder system for customers

A simple and reliable digital system is needed to help vendors manage transactions easily and securely.

---

# ✨ **Features**

## 📌 Digital Credit Management

Allows vendors to add and manage customer credit (Udari) and payment transactions digitally.

## 👥 Customer Management

Stores customer details and maintains complete transaction history.

## 💰 Balance Calculation

Automatically calculates total credit, payments received, and pending balance.

## 📊 Transaction History

Displays all transactions with amount, type, notes, and date for easy tracking.

## 🔍 Search & Filter

Users can quickly search customers and filter transactions.

## 📱 WhatsApp Reminder

Generates pre-filled WhatsApp reminder messages for pending payments.

## 🗂️ Offline Data Storage

Uses Room Database for secure offline storage of customer and transaction data.

## 🎨 User-Friendly Interface

Simple and modern UI built using Jetpack Compose for easy usability.

---

# 🛠️ **Technologies Used**

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* Room Database
* MVVM Architecture
* StateFlow
* Kotlin Coroutines

---

# 👨‍💻 **Target Users**

This application is designed for:

* Small vendors
* Vegetable sellers
* Grocery shop owners
* Street vendors
* Local retail businesses

---

# 📱 **Screens**

1. Splash Screen
2. Login Screen
3. Register Screen
4. Dashboard / Home Screen
5. Quick Entry Step 1 (Select Customer)
6. Quick Entry Step 2 (Enter Details)
7. Customer List Screen
8. Customer Detail / Ledger Screen
9. Transaction List Screen
10. Daily Summary Screen
11. WhatsApp Reminder Screen

---

# 🗂️ **Project Structure**

```bash
NammaSantheLedger/
├── app/
│   └── src/
│       └── main/
│           └── java/com/example/nammasantheledger/
│               ├── data/
│               │   ├── AppDatabase.kt
│               │   ├── RoomEntities.kt
│               │   ├── RoomDAOs.kt
│               │   ├── LedgerRepository.kt
│               │   └── LedgerViewModel.kt
│               ├── uiscreen/
│               │   └── screens/
│               │       ├── SplashScreen.kt
│               │       ├── LoginScreen.kt
│               │       ├── CreateAccountScreen.kt
│               │       ├── DashboardScreen.kt
│               │       ├── QuickEntryScreen.kt
│               │       ├── CustomerListScreen.kt
│               │       ├── CustomerDetailScreen.kt
│               │       ├── TransactionListScreen.kt
│               │       ├── DailySummaryScreen.kt
│               │       └── WhatsAppReminderScreen.kt
│               └── MainActivity.kt
├── res/
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── mipmap/
│       └── ic_launcher (App Icon)
├── build.gradle.kts
├── AndroidManifest.xml
└── README.md
```

---

# ⚙️ **Installation & Setup**

## ✅ Requirements

* Android Studio Meerkat or higher
* JDK 11 or higher
* Android SDK (API 24+)
* Minimum Android 7.0 device or emulator

---

## 🚀 Steps to Run

### Steps to Run

**Step 1 — Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/NammaSantheLedger.git
```
**Step 2 — Open in Android Studio**
File → Open → Select the cloned folder

**Step 3 — Wait for Gradle Sync**
Android Studio will automatically download all dependencies

**Step 4 — Create Emulator**
Device Manager → Create Virtual Device → Pixel 6 → API 34 → Finish

**Step 5 — Run the App**
Click the ▶ Run button or press Shift + F10

**Step 6 — Register and Start**
Create a new account → Login → Start adding entries

---

# 🔮 **Future Improvements**

* Cloud sync and backup
* Multi-language support (Kannada, Hindi)
* PDF export of ledger
* SMS reminder (without WhatsApp)
* Analytics and graphs
* Biometric login

---

# 🙌 **Conclusion**

Namma Santhe Ledger helps small vendors digitally manage customer credits and payments in a simple, fast, and secure way.
The application reduces manual errors, improves payment tracking, and provides a modern alternative to traditional notebook-based ledger systems.

