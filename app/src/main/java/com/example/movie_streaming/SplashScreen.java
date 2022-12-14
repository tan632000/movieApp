package com.example.movie_streaming;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_SCREEN = 4200;
    LottieAnimationView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        splash = findViewById(R.id.splash_main);

        new Handler().postDelayed(this::isLogin, SPLASH_SCREEN);
    }

    private void isLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (user == null) {
            intent = new Intent(SplashScreen.this, LoginScreen.class);
        } else {
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}