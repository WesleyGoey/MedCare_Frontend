# ⏰ MedCare - Healthcare & Medication Reminder Mobile App (Frontend)

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)

MedCare is the client-side mobile application of a specialized healthcare ecosystem designed to tackle medication non-compliance among the elderly. Built with accessibility at its core, this Android application bridges high-contrast, intuitive interfaces with reactive background services to deliver real-time automated alerts and straight-forward treatment tracking for senior users and their caregivers.

*Companion Backend Repository:* [MedCare Backend](https://github.com/WesleyGoey/MedCare_Backend.git)

---

## 📌 Project Context & Metadata

| Attribute | Details |
| :--- | :--- |
| 🎓 Institution | Universitas Ciputra Surabaya |
| 🚀 Academic Timeline | Semester 3 - Visual Programming Final Project |
| 📅 Development Period | September 2025 – January 2026 |
| 👥 Team Size | 3 Developers |
| 💻 Platform | Mobile (Android Native) |

---

## 🚀 Technical Features & Architecture

### 🧠 MVVM Architecture & Data Flow
- Reactive UI Pattern: Implemented Model-View-ViewModel (MVVM) architecture to cleanly separate UI presentation from business logic, ensuring a stable and testable frontend state.
- API Consumption Layer: Built structured data channels utilizing Repositories and Data Transfer Objects (DTOs) to establish clean, asynchronous communication with the remote RESTful backend.

### 🔔 High-Accessibility & Reminder Logic
- Automated Reminder Engine: Engineered a high-priority system notification and device alarm listener to guarantee that time-critical alerts trigger reliably on the user's device, even when the application runs in the background.
- Senior-Centric UX Design: Programmed clear layouts, readable typographies, and simplified input interactions tailored to reduce cognitive load and simplify complex schedule viewing.

### 📈 Treatment Progress & Tracking
- Dynamic Adherence Dashboard: Developed an intuitive tracking calendar and history log interface that visualizes daily medication intake, empowering caregivers to easily monitor patient compliance trends.
- Medication CRUD UI: Constructed interactive entry interfaces to smoothly manage and synchronize custom medicine catalog settings with database entities.

---

## 💻 Tech Stack

- Language: Kotlin
- Architecture: MVVM (Model-View-ViewModel)
- Network Client: Retrofit / HTTP Client Wrapper
- Interface: Android Native UI Framework
