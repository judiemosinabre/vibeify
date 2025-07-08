package com.example.vibeifyfer;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MusicSuggestionActivity extends AppCompatActivity {

    String name, artist, albumImage, mood;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private boolean isSeeking = false;
    private TextView timeStart, timeEnd;

    ImageButton cameraIconButton, qrIconButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_music_suggestion);

        seekBar = findViewById(R.id.seekBar);
        timeStart = findViewById(R.id.timeStart);
        timeEnd = findViewById(R.id.timeEnd);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        qrIconButton = findViewById(R.id.qrIconButton);
        cameraIconButton = findViewById(R.id.cameraIconButton);

        TextView heading = findViewById(R.id.heading2);
        heading.setTypeface(FontUtil.getInterBold18(this));

        mood = getIntent().getStringExtra("mood");
        String selectedTrackId = getRandomTrackIdForMood(mood);

        SpotifyAuth.getAccessToken(new SpotifyAuth.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                getTrackDetails(token, selectedTrackId); // spotify handles fallback later
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MusicSuggestionActivity.this, "Spotify auth failed", Toast.LENGTH_SHORT).show());
            }
        });

        Button qrBtn = findViewById(R.id.qrBtn);
        qrBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MusicSuggestionActivity.this, QRScanActivity.class);
            intent.putExtra("track_url", "https://open.spotify.com/track/" + selectedTrackId);
            intent.putExtra("track_name", name);
            intent.putExtra("artist_name", artist);
            intent.putExtra("mood", getIntent().getStringExtra("mood"));
            intent.putExtra("album_image", albumImage);
            startActivity(intent);
            finish();
        });

        // home button returns to MainActivity and clears mood info
        cameraIconButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(MusicSuggestionActivity.this, MainActivity.class);

            // clear the back stack so MainActivity is fresh
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish(); // close QRScanActivity
        });

        // next button
        qrIconButton.setOnClickListener(v -> {
            Intent intent = new Intent(MusicSuggestionActivity.this, QRScanActivity.class);
            intent.putExtra("track_url", "https://open.spotify.com/track/" + selectedTrackId);
            intent.putExtra("track_name", name);
            intent.putExtra("artist_name", artist);
            intent.putExtra("mood", getIntent().getStringExtra("mood"));
            intent.putExtra("album_image", albumImage);
            startActivity(intent);
            finish();
        });
    }

    private String getRandomTrackIdForMood(String mood) {
        mood = mood.toLowerCase();
        List<String> tracks = MoodSongs.moodToTracks.getOrDefault(mood, MoodSongs.moodToTracks.get("Neutral"));
        return tracks.get(new Random().nextInt(tracks.size()));
    }

    private void getTrackDetails(String token, String trackId) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.spotify.com/v1/tracks/" + trackId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + token);

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                name = json.getString("name");
                String previewUrl = json.optString("preview_url", null);
                Log.d("MusicSuggestion", "Preview URL: " + previewUrl);
                albumImage = json.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                artist = json.getJSONArray("artists").getJSONObject(0).getString("name");

                String spotifyUrl = "https://open.spotify.com/track/" + trackId;
                // always show UI first
                runOnUiThread(() -> updateUI(name, artist, albumImage, null)); // Temporarily null previewUrl

                if (previewUrl == null || previewUrl.isEmpty() || previewUrl.equals("null")) {
                    runOnUiThread(() -> {
                        // Toast.makeText(MusicSuggestionActivity.this, "Preview not available. Fetching fallback...", Toast.LENGTH_SHORT).show();
                    });
                    fetchFallbackMusic(trackId); // fetch from Firebase Storage
                    return;
                }

                // update UI again but now with previewUrl
                runOnUiThread(() -> updateUI(name, artist, albumImage, previewUrl));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Toast.makeText(MusicSuggestionActivity.this, "Failed to get track details", Toast.LENGTH_SHORT).show();
                    Button playPauseBtn = findViewById(R.id.playPauseBtn);
                    playPauseBtn.setEnabled(false);
                });
            }
        }).start();
    }

    private void fetchFallbackMusic(String trackId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // StorageReference storageRef = storage.getReference().child(trackId + ".mp3");
        // Convert mood to lowercase and build the path
        String path = "vibeify/" + mood.toLowerCase() + "/" + trackId + ".mp3";
        StorageReference storageRef = storage.getReference().child(path);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String fallbackUrl = uri.toString();
            runOnUiThread(() -> {
                // now that audio is available, re-init media player
                updateUI(name, artist, albumImage, fallbackUrl); // albumImage already loaded before
            });
        }).addOnFailureListener(e -> {
            runOnUiThread(() -> Toast.makeText(this, "Fallback preview not available", Toast.LENGTH_SHORT).show());
        });
    }

    private void updateUI(String trackName, String artistName, String albumImage, String previewUrl) {
        this.name = trackName;
        this.artist = artistName;
        this.albumImage = albumImage;

        TextView songNameText = findViewById(R.id.songNameText);
        TextView artistText = findViewById(R.id.artistText);
        ImageView albumCover = findViewById(R.id.albumCover);
        Button playPauseBtn = findViewById(R.id.playPauseBtn);

        // load Animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_vinyl);

        songNameText.setText(name);
        artistText.setText(artist);
        if (albumImage != null && !albumImage.isEmpty()) {
            Glide.with(this).load(albumImage).circleCrop().into(albumCover);
        }

        playPauseBtn.setEnabled(false);
        playPauseBtn.setText("Loading");

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(previewUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mp.getDuration();
                seekBar.setMax(duration);
                timeStart.setText("00:00");
                timeEnd.setText(formatTime(duration));

                playPauseBtn.setEnabled(true);
                playPauseBtn.setText("PLAY");

                // thread to update seekBar and timeStart
                Handler handler = new Handler();
                Runnable updateSeek = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying() && !isSeeking) {
                            int currentPos = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPos);
                            timeStart.setText(formatTime(currentPos));
                        }
                        handler.postDelayed(this, 500);
                    }
                };
                handler.post(updateSeek);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                playPauseBtn.setText("PLAY");
                seekBar.setProgress(0);
                timeStart.setText("00:00");
                albumCover.clearAnimation();
            });

        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(this, "Error preparing media player", Toast.LENGTH_SHORT).show();
            playPauseBtn.setEnabled(false);
        }

        playPauseBtn.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPauseBtn.setText("PLAY");
                    albumCover.clearAnimation();  // stop rotation when paused
                } else {
                    mediaPlayer.start();
                    playPauseBtn.setText("PAUSE");
                    albumCover.startAnimation(rotateAnimation);  // start rotation when playing
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }

            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    timeStart.setText(formatTime(progress)); // update immediately on drag
                }
            }
        });
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}