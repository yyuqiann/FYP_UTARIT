package my.edu.utar.utarit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.network.SupabaseClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView signUpButton, resultText;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        resultText = findViewById(R.id.resultText);
        backBtn = findViewById(R.id.backBtn);

        // Go back to LoginActivity
        backBtn.setOnClickListener(v -> finish());

        signUpButton.setOnClickListener(v -> register());
    }

    private void register() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!Pattern.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", email)) {
            resultText.setText("Invalid email format");
            return;
        }
        if (password.length() < 6) {
            resultText.setText("Password must be at least 6 characters");
            return;
        }

        SupabaseApi api = SupabaseClient.getApi();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("username", email.split("@")[0]);
        userMap.put("password", password);

        api.createUser(SupabaseConfig.API_KEY, "Bearer " + SupabaseConfig.SERVICE_KEY, userMap)
                .enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            resultText.setText("Registration failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable t) {
                        resultText.setText("Error: " + t.getMessage());
                    }
                });
    }
}
