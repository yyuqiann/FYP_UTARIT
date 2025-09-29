package my.edu.utar.utarit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import my.edu.utar.utarit.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Show HomeFragment by default when the activity is first created
        if (savedInstanceState == null) {
            Fragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, homeFragment, "HOME_FRAGMENT")
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.home);  // Set default selection
        }

        // Bottom Navigation Listener for switching between fragments
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Determine which fragment to load based on the selected item
            if (item.getItemId() == R.id.home) selectedFragment = new HomeFragment();
            //else if (item.getItemId() == R.id.event) selectedFragment = new EventFragment();
            else if (item.getItemId() == R.id.add) selectedFragment = new AddPostFragment();
            //else if (item.getItemId() == R.id.chat) selectedFragment = new ChatFragment();
            else if (item.getItemId() == R.id.profile) selectedFragment = new ProfileFragment();

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

}
