package my.edu.utar.utarit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.regex.Pattern;

import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.network.SupabaseClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView signInButton, signUpButton, resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        resultText = findViewById(R.id.resultText);

        signInButton.setClickable(true);
        signUpButton.setClickable(true);

        signInButton.setOnClickListener(v -> login());
        signUpButton.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!Pattern.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", email)) {
            resultText.setText("Invalid email format");
            return;
        }

        SupabaseApi api = SupabaseClient.getApi();
        api.getAllUsers(SupabaseConfig.API_KEY, "Bearer " + SupabaseConfig.SERVICE_KEY)
                .enqueue(new Callback<List<Profile>>() {
                    @Override
                    public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean found = false;
                            for (Profile p : response.body()) {
                                if (p.getEmail().equals(email)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                // Login success
                            } else {
                                resultText.setText("Email not found");
                            }
                        } else {
                            resultText.setText("Login failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Profile>> call, Throwable t) {
                        resultText.setText("Error: " + t.getMessage());
                    }
                });

    }
}
