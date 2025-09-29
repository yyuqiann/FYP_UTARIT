package my.edu.utar.utarit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private EditText usernameEditText;
    private Button btnUpdateUsername;

    private String accessToken;
    private Profile currentProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        usernameEditText = view.findViewById(R.id.editUsername);
        btnUpdateUsername = view.findViewById(R.id.btnUpdateUsername);

        accessToken = SessionManager.getInstance(requireContext()).getAccessToken();

        loadCurrentProfile();

        btnUpdateUsername.setOnClickListener(v -> {
            String newUsername = usernameEditText.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                updateUsername(newUsername);
            } else {
                Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadCurrentProfile() {
        SupabaseApi api = SupabaseClient.getApi();
        String userId = SessionManager.getInstance(requireContext()).getUserId();

        Call<List<Profile>> call = api.getUserProfile(
                SupabaseConfig.API_KEY,
                "Bearer " + accessToken,
                userId
        );

        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    currentProfile = response.body().get(0);
                    usernameEditText.setText(currentProfile.getDisplayName());
                } else {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUsername(String newUsername) {
        if (currentProfile == null) return;

        SupabaseApi api = SupabaseClient.getApi();

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);

        api.updateUserProfile(
                SupabaseConfig.API_KEY,
                "Bearer " + accessToken,
                currentProfile.getId(),
                updates
        ).enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Username updated!", Toast.LENGTH_SHORT).show();

                    // Update session
                    SessionManager.getInstance(requireContext()).setUsername(newUsername);
                } else {
                    Toast.makeText(getContext(), "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Toast.makeText(getContext(), "Update error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
