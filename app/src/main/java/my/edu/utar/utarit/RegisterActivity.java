package my.edu.utar.utarit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView signUpButton, resultText;
    private ImageButton backBtn;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpmaGppeHpwY3BmamRiZ3NkY3huIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg3NzQxMzMsImV4cCI6MjA3NDM1MDEzM30.7--8nk7745mYJX_AlEv3O4MzivSpM_ElUJsY904gvD4"; // replace with your anon key
    private static final String BASE_URL = "https://zfhjixzpcpfjdbgsdcxn.supabase.co";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        resultText = findViewById(R.id.resultText);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());
        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            resultText.setText("Please fill all fields");
            return;
        }

        if (password.length() < 6) {
            resultText.setText("Password must be at least 6 characters");
            return;
        }

        signUp(email, password);
    }

    private void signUp(String email, String password) {
        JSONObject json = new JSONObject();
        try {
            // Only email & password are required for auth.users
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/v1/signup")
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> resultText.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.i("Supabase", "Signup response: " + resp);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        try {
                            JSONObject json = new JSONObject(resp);
                            if (json.has("msg")) {
                                resultText.setText("Registration failed: " + json.getString("msg"));
                            } else if (json.has("error_code")) {
                                resultText.setText("Registration failed: " + json.getString("error_code"));
                            } else {
                                resultText.setText("Registration failed");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            resultText.setText("Registration failed");
                        }
                    }
                });
            }

        });
    }


}