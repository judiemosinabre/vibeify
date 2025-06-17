# vibeify
## Overview  
Vibeify is a smart, mood-aware mobile app that uses facial emotion recognition to recommend a single song tailored to your current feelings. Simply scan your face, and Vibeify instantly plays a 30-second Spotify preview matching your vibe—no typing or searching needed.

By integrating real-time facial expression analysis with Spotify’s music library, Vibeify offers a fun, personalized music experience perfect for students, music lovers, and anyone who wants songs that truly resonate.

## Features

### 1. Face Emotion Detection  
- Uses the device’s front camera with Google ML Kit Face Detection API.  
- Detects emotions like happy, sad, neutral, angry by analyzing facial expressions and key landmarks (eyes, mouth, eyebrows).  
- Tracks facial features such as smiling probability, eye openness, and head tilt in real-time.

### 2. Mood-Mapped Music Recommender  
- Matches detected emotions to suitable Spotify categories or playlists (e.g., Happy → Pop/EDM, Sad → Acoustic/Chill).  
- Selects and plays a single 30-second song preview that fits the user’s mood.

### 3. Spotify Integration  
- Connects to Spotify Web API (free tier) to fetch playlists, artist/track info, and audio previews.  
- Provides seamless music playback aligned with the user’s emotion.

### 4. Interactive Exhibit Mode  
- Generates a QR code linking directly to the recommended song on Spotify.  
- Allows users to instantly open the song on their own devices without installing the app.  
- Ideal for tech fairs, demo booths, or interactive exhibits.

---

## Technology Stack

- **Mobile Platform:** Android Studio (Java/Kotlin) or Flutter (Dart)  
- **AI/ML:** Google ML Kit — Face Detection API for emotion recognition  
- **Music API:** Spotify Web API (Free tier)  
- **Backend (optional):** Firebase Realtime Database or Firestore for mood logs and analytics  
- **Image Loading:** Glide or Picasso for album art display  

---

## Target Users

- Music lovers (ages 13–30)  
- Social media enthusiasts  
- Students and professionals seeking quick, mood-based song discovery  
- Visitors at tech fairs or school exhibits wanting interactive experiences  

---

## How It Works

1. User opens the app and grants camera access.  
2. The front camera captures facial expressions and sends data to ML Kit for emotion analysis.  
3. The app infers the current mood (happy, sad, neutral, angry, etc.).  
4. Based on the mood, a corresponding Spotify category or playlist is chosen.  
5. A 30-second song preview matching the mood plays instantly.  
6. In exhibit mode, a QR code can be scanned to open the song directly on another device.

---

## UI
![Updated Sample UI](https://github.com/user-attachments/assets/f9cb04b8-4b38-4429-b619-bfb0f83183ae)




---

## Getting Started

1. Clone the repository.  
2. Set up your Spotify developer account and obtain API credentials.  
3. Configure Google ML Kit Face Detection in your app.  
4. Build and run the app on an Android device or emulator.  

---
