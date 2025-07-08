package com.example.vibeifyfer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRScanActivity extends AppCompatActivity {
    Button homeBtn, backBtn, shareBtn;
    ImageView qrImageView;
    ImageView glassCard;
    ImageButton cameraIconButton, albumIconButton;

    String trackUrl, songName, artistName, mood, albumImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrscan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View mainLayout = findViewById(R.id.main);

        qrImageView = findViewById(R.id.qrImageView);
        homeBtn = findViewById(R.id.homeeBtn);
        backBtn = findViewById(R.id.backBtn);
        cameraIconButton = findViewById(R.id.cameraIconButton);
        albumIconButton = findViewById(R.id.albumIconButton);
        // glassCard = findViewById(R.id.glassCard);
        shareBtn = findViewById(R.id.shareToStoryBtn);

        Intent intent = getIntent();
        trackUrl = getIntent().getStringExtra("track_url");
        songName = getIntent().getStringExtra("track_name");
        artistName = getIntent().getStringExtra("artist_name");
        mood = getIntent().getStringExtra("mood");
        albumImage = getIntent().getStringExtra("album_image");

        TextView songNameText = findViewById(R.id.songNameText);
        TextView artistText = findViewById(R.id.artistText);
        TextView moodText = findViewById(R.id.moodText);

        songNameText.setText(songName);
        artistText.setText(artistName);
        moodText.setText("Mood:  " + mood);

        try {
            Bitmap qrBitmap = generateQRCode(trackUrl);
            qrImageView.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // set mood background color and text
        GradientDrawable moodTextBg = new GradientDrawable();
        moodTextBg.setCornerRadius(24); 

        switch (mood) {
            case "Super Happy":
                moodTextBg.setColor(Color.parseColor("#FFD93D")); // bright yellow — energetic, joyful
                break;
            case "Happy":
                moodTextBg.setColor(Color.parseColor("#F58E8A")); // pinkish — cheerful but calmer
                break;
            case "Neutral":
                moodTextBg.setColor(Color.parseColor("#A6A6A6")); // gray — calm, balanced
                break;
            case "Sad":
                moodTextBg.setColor(Color.parseColor("#4EA1D3")); // muted blue — soft and comforting
                break;
            case "Angry":
                moodTextBg.setColor(Color.parseColor("#D9534F")); // red — intense and bold
                break;
            default:
                moodTextBg.setColor(Color.parseColor("#CCCCCC")); // fallback — light gray
                break;
        }

        moodText.setBackground(moodTextBg);

        shareBtn.setOnClickListener(v -> {
            shareSongGeneric(albumImage, songName, artistName, trackUrl);
        });

        // home button clears mood info
        homeBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(QRScanActivity.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish(); // close QRScanActivity
        });

        // back button 
        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(QRScanActivity.this, MusicSuggestionActivity.class);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                backIntent.putExtras(extras);
            }
            startActivity(backIntent);
            finish();
        });

        // footer Navs
        // returns to MainActivity and clears mood info
        cameraIconButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(QRScanActivity.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish(); // close QRScanActivity
        });

        // back button 
        albumIconButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(QRScanActivity.this, MusicSuggestionActivity.class);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                backIntent.putExtras(extras);
            }
            startActivity(backIntent);
            finish();
        });
    }

    private Bitmap generateQRCode(String text) throws WriterException {
        int size = 512;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.createBitmap(bitMatrix);
    }

    private void shareSongGeneric(String albumImageUrl, String songName, String artistName, String trackUrl) {
        Glide.with(this)
                .asBitmap()
                .load(albumImageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            // save bitmap to cache
                            File file = new File(getCacheDir(), "share_image.png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();

                            Uri imageUri = FileProvider.getUriForFile(
                                    getApplicationContext(),
                                    getPackageName() + ".provider",
                                    file
                            );

                            // create intent with both image and text
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/png"); // must be image type to include image
                            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,
                                            "\nVibeify recommends this song!" +
                                                    "\n" +
                                            "\n🎧 Listen On Spotify: " + trackUrl);

                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(shareIntent, "Share song with"));

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Sharing failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }
}