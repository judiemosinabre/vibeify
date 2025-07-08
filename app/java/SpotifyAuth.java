package com.example.vibeifyfer;

import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class SpotifyAuth {
    public interface TokenCallback {
        void onTokenReceived(String token);
        void onError(String error);
    }

    public static void getAccessToken(TokenCallback callback) {
        new Thread(() -> {
            try {
                String clientId    = BuildConfig.SPOTIFY_CLIENT_ID;
                String clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET;
                String credentials = clientId + ":" + clientSecret;
                String encoded = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                URL url = new URL("https://accounts.spotify.com/api/token");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Basic " + encoded);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write("grant_type=client_credentials");
                writer.flush();
                writer.close();
                os.close();

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                String accessToken = json.getString("access_token");

                callback.onTokenReceived(accessToken);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }
}
