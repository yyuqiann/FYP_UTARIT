package my.edu.utar.utarit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.network.model.SignInRequest;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.network.model.SignInResponse;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView signInButton, signUpButton, resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto login if session exists
        SessionManager session = SessionManager.getInstance(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.login_activity);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        resultText = findViewById(R.id.resultText);

        // On Sign In button click
        signInButton.setOnClickListener(v -> login());

        // On Sign Up button click
        signUpButton.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            resultText.setText("Please fill in all fields.");
            return;
        }

        // Use SupabaseClient's built-in signIn method
        SupabaseClient.signIn(email, password, this, new SupabaseClient.AuthCallback() {
            @Override
            public void onSuccess(SignInResponse response) {
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                resultText.setText(error);
            }
        });
    }


    private void saveSessionAndNavigate(String accessToken, String userId, String email) {
        // For now, we'll save with "Unknown" username and update it later
        // You can implement a separate API call to get the username from the users table
        SessionManager.getInstance(LoginActivity.this).saveSession(
                accessToken,
                userId,
                "Unknown", // We'll need to get this from users table
                email
        );

        // Store current user info in SupabaseClient for easy access
        SupabaseClient.setCurrentUserId(userId);
        SupabaseClient.setCurrentUserName("Unknown"); // Update this when you get username

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

}