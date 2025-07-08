package com.example.vibeifyfer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    String detectedMood = "Neutral";
    TextView moodMessage;
    private long lastAnalyzedTime = 0;
    private static final long ANALYSIS_INTERVAL_MS = 2000; // 2 seconds
    ImageButton albumIconButton, qrIconButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        qrIconButton = findViewById(R.id.qrIconButton);
        albumIconButton = findViewById(R.id.albumIconButton);

        moodMessage = findViewById(R.id.moodMessage);
        TextView heading = findViewById(R.id.heading);
        heading.setTypeface(FontUtil.getInterBold18(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCamera();
        }

        ImageButton cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicSuggestionActivity.class);
            intent.putExtra("mood", detectedMood); 
            startActivity(intent);
        });

        albumIconButton.setOnClickListener(v -> {
            Toast.makeText(this, "Please capture a photo first to view the song suggestion", Toast.LENGTH_SHORT).show();
        });

        qrIconButton.setOnClickListener(v -> {
            Toast.makeText(this, "Oops! Capture a photo first to unlock this feature.", Toast.LENGTH_SHORT).show();
        });



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                PreviewView previewView = findViewById(R.id.previewView);
                Preview preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(320, 240))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

                FaceDetector detector = FaceDetection.getClient(options);

                FaceOverlayView overlay = findViewById(R.id.overlay);

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastAnalyzedTime < ANALYSIS_INTERVAL_MS) {
                        imageProxy.close(); // skip processing
                        return;
                    }

                    lastAnalyzedTime = currentTime; // update timestamp
                    @SuppressLint("UnsafeOptInUsageError")
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                        detector.process(image)
                                .addOnSuccessListener(faces -> {
                                    List<RectF> faceBounds = new ArrayList<>();

                                    int imageWidth = image.getWidth();   // Typically 480
                                    int imageHeight = image.getHeight(); // Typically 640

                                    float scaleX = (float) previewView.getWidth() / imageHeight;
                                    float scaleY = (float) previewView.getHeight() / imageWidth;

                                    for (Face face : faces) {
                                        float smileProb = face.getSmilingProbability() != null ? face.getSmilingProbability() : 0f;
                                        float leftEyeOpenProb = face.getLeftEyeOpenProbability() != null ? face.getLeftEyeOpenProbability() : 0f;
                                        float rightEyeOpenProb = face.getRightEyeOpenProbability() != null ? face.getRightEyeOpenProbability() : 0f;
                                        float headTiltY = face.getHeadEulerAngleY(); // Left/Right rotation

                                        // Angry-ish (head turned + no smile)
                                        if (smileProb < 0.1 && Math.abs(headTiltY) > 15) {
                                            detectedMood = "Angry";
                                            runOnUiThread(() -> moodMessage.setText("Uh-oh, looks like someone's upset 😠"));
                                        }
                                        // Sad
                                        else if (smileProb < 0.2) {
                                            detectedMood = "Sad";
                                            runOnUiThread(() -> moodMessage.setText("You look a bit down tho"));
                                        }
                                        // Neutral
                                        else if (smileProb < 0.5) {
                                            detectedMood = "Neutral";
                                            runOnUiThread(() -> moodMessage.setText("You look calm. Here's something chill to vibe with. 😌"));
                                        }
                                        // Happy (clear smile)
                                        else if (smileProb < 0.80) {
                                            detectedMood = "Happy";
                                            runOnUiThread(() -> moodMessage.setText("I love your smile! Here's music that resonates with you"));
                                        }
                                        // Laughing (very big smile + open eyes)
                                        else {
                                            if (leftEyeOpenProb > 0.6 && rightEyeOpenProb > 0.6) {
                                                detectedMood = "Super Happy";
                                                runOnUiThread(() -> moodMessage.setText("You're glowing with laughter! 😄"));
                                            } else {
                                                detectedMood = "Happy";
                                                runOnUiThread(() -> moodMessage.setText("I love your smile! Here's music that resonates with you"));
                                            }
                                        }

                                        Log.d("Vibeify", "Mood Detected: " + detectedMood);
                                        //Toast.makeText(this, "Mood: " + detectedMood, Toast.LENGTH_SHORT).show();

                                        // Convert and mirror bounding box
                                        float left = face.getBoundingBox().left * scaleX;
                                        float top = face.getBoundingBox().top * scaleY;
                                        float right = face.getBoundingBox().right * scaleX;
                                        float bottom = face.getBoundingBox().bottom * scaleY;

                                        // Mirror horizontally for front camera
                                        float mirroredLeft = previewView.getWidth() - right;
                                        float mirroredRight = previewView.getWidth() - left;

                                        RectF bounds = new RectF(mirroredLeft, top, mirroredRight, bottom);
                                        faceBounds.add(bounds);
                                    }

                                    overlay.setFaces(faceBounds);
                                    imageProxy.close();
                                })
                                .addOnFailureListener(e -> {
                                    imageProxy.close();
                                });
                    }
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
}