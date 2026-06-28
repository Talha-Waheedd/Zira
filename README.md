# Zira — AI Study Companion

An AI-powered Android study app for university and college students in Pakistan. Zira delivers
step-by-step tutoring, auto-generated spaced-repetition flashcards, quizzes, and progress tracking —
with offline access to saved explanations and flashcards.

> Built in **Java** for **Android API 26+**, following Google's recommended **MVVM** architecture
> and **Material Design 3 (Material You)**.

---

## Features

| Feature | Description |
| --- | --- |
| **Ask Zira** | Conversational, step-by-step explanations with follow-up question chips and a typewriter reveal animation. Saved offline. |
| **AI Flashcards** | Auto-generate decks per subject and review them with a spaced-repetition (SM-2) scheduler. Stored locally in Room. |
| **Quiz Mode** | AI-generated multiple-choice quizzes with instant correct/incorrect feedback and a results summary. |
| **Progress Tracking** | Weekly study-time bar chart, subject mastery pie chart, a 28-day streak calendar, and weak-topic list. |
| **Study Schedule** | Generate a personalized exam-prep plan from your exam dates and daily study goal. |
| **Profile & Settings** | Edit subjects, daily goal, dark mode, and notification preferences. |

---

## Tech Stack

- **Language:** Java
- **Architecture:** MVVM (View → ViewModel → Repository → Local/Remote)
- **UI:** Material Design 3 (Material You), ViewBinding, ViewPager2, RecyclerView
- **Local data:** Room (flashcards, explanations) for offline support
- **Remote data:** Retrofit + Gson + OkHttp (consumes a FastAPI backend)
- **Auth & cloud:** Firebase Authentication (Email/Password) + Cloud Firestore
- **Charts:** MPAndroidChart
- **Animations:** Lottie
- **Spaced repetition:** SM-2 algorithm (Easy = +7d, Medium = +3d, Hard = +1d)

---

## Architecture

```
View (Activity / Fragment)
        │  observes LiveData / sends events
        ▼
ViewModel (holds UI state, survives config changes)
        │  calls
        ▼
ZiraRepository (single source of truth)
   ├── Room (AppDatabase, DAOs)        ← offline cache
   ├── Retrofit ApiService             ← FastAPI backend
   └── Firebase Auth + Firestore       ← user profile & sessions
```

The **View never touches** the database, network, or Firebase directly — all data flows through the
ViewModel and Repository.

### Package structure (`app/src/main/java/com/zira/app/`)

```
ZiraApplication.java          Application init (Firebase, theme)
data/
  local/                      Room database, DAOs, entities, type converters
  remote/                     ApiService, RetrofitClient, request/response models
  repository/                 ZiraRepository, ProgressAggregator, UserProfileLiveData
  model/                      UserProfile, ProgressData
ui/
  auth/                       Login, Register
  onboarding/                 ViewPager onboarding (Welcome, Subjects, Goal)
  home/                       Dashboard + HomeViewModel
  ask/                        Chat UI + AskViewModel + MessageAdapter
  flashcards/                 Deck list, review pager, rating sheet
  quiz/                       Quiz, Result + QuizViewModel
  progress/                   Charts, streak calendar, weak topics
  profile/                    Settings + ProfileViewModel
  schedule/                   Study-schedule generator
utils/                        Constants, DateUtils, NetworkUtils, NavigationUtils,
                              PrefsHelper, Sm2Utils
```

---

## Getting Started

### Prerequisites

- Android Studio (Hedgehog or newer)
- JDK 17
- An Android device or emulator running API 26+
- A Firebase project
- The Zira FastAPI backend URL

### 1. Clone

```bash
git clone <your-repo-url>
cd Zira
```

### 2. Configure Firebase

1. Create a project named **zira-app** in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with the package name **`com.zira.app`**.
3. Download the generated **`google-services.json`** and place it in **`app/`** (it replaces the
   placeholder currently committed).
4. Enable **Authentication → Email/Password**.
5. Create a **Cloud Firestore** database (start in test mode; secure with rules before release).

### 3. Set the backend URL

Open `app/src/main/java/com/zira/app/utils/Constants.java` (or `RetrofitClient.java`) and replace the
placeholder base URL:

```java
public static final String BASE_URL = "https://your-backend-url.com/";
```

### 4. Build & run

```bash
./gradlew assembleDebug      # build a debug APK
./gradlew installDebug       # install on a connected device
```

Or open the project in Android Studio and press **Run**.

---

## Backend API

The app consumes the following endpoints (built separately as a Python FastAPI service):

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/explain` | Step-by-step explanation + follow-ups + key concepts |
| `POST` | `/api/quiz` | Multiple-choice questions for a subject/difficulty |
| `POST` | `/api/flashcards` | Generate flashcards for a topic |
| `POST` | `/api/schedule` | Generate a study schedule from exam dates |

Request/response models live in `data/remote/model/`.

---

## Firestore Data Model

| Collection | Doc ID | Key fields |
| --- | --- | --- |
| `users` | `{uid}` | `name`, `email`, `subjects[]`, `dailyGoalMins`, `streakCount`, `lastStudyDate`, `totalXP`, `createdAt` |
| `users/{uid}/explanations` | auto | `question`, `explanation`, `subject`, `keyConceptsList[]`, `timestamp` |
| `users/{uid}/quiz_sessions` | auto | `subject`, `score`, `totalQuestions`, `wrongTopics[]`, `timeTakenSecs`, `timestamp` |
| `users/{uid}/study_sessions` | auto | `durationMins`, `subject`, `activityType` (ask/quiz/flashcard), `timestamp` |

---

## Offline Support

- **Flashcards** and **explanations** are cached in a Room database (`zira-db`), so review and reading
  work without a connection.
- AI features that require the network (Ask, Quiz, Flashcard generation, Schedule) check connectivity
  via `NetworkUtils` and show a friendly offline message instead of failing silently.

---

## Release Build

Release builds are minified and resource-shrunk via R8/ProGuard (`app/proguard-rules.pro` keeps Gson,
Retrofit, Room, Firebase, MPAndroidChart, and all data models).

```bash
./gradlew assembleRelease
```

For a distributable build, configure a signing config / keystore and bump `versionCode` /
`versionName` in `app/build.gradle`.

---

## Design System

- **Seed color:** `#6750A4` (Deep Purple) — Material You derives the rest.
- **Type scale:** Material 3 (Display Large for splash, Headline Large for titles, Body Large for
  content, etc.).
- **Animations (Lottie):** splash, typing indicator, success checkmark, empty states, streak fire.

---

## License

This project is provided for educational purposes. Add your preferred license here.
