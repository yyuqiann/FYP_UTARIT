package my.edu.utar.utarit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Modern full-screen
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }



        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView text = findViewById(R.id.textView);
        MediaPlayer boingSound = MediaPlayer.create(this, R.raw.boing_sound);

        // Load animations
        Animation bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(bounceIn);

        bounceIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Safe Handler usage
                new Handler(Looper.getMainLooper()).postDelayed(boingSound::start, 500);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                text.setVisibility(View.VISIBLE);
                text.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                navigateNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void navigateNext() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("user_id", sharedPreferences.getString("user_id", null));
            intent.putExtra("access_token", sharedPreferences.getString("accessToken", null));
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in2, R.anim.fade_out);
        finish();
    }
}
