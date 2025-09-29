package my.edu.utar.utarit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import my.edu.utar.utarit.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnSettings, btnAbout, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
       //btnSettings = view.findViewById(R.id.btnSettings);
         btnAbout = view.findViewById(R.id.btnAbout);
        btnLogout = view.findViewById(R.id.btnLogout);

        SessionManager session = SessionManager.getInstance(requireContext());
        tvUsername.setText(session.getUsername());
        tvEmail.setText(session.getEmail());

        //btnSettings.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                //.beginTransaction()
                //.replace(R.id.frame_layout, new SettingsFragment())
                //.addToBackStack(null)
                //.commit())

        btnAbout.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new AboutFragment())
                .addToBackStack(null)
                .commit());

        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
