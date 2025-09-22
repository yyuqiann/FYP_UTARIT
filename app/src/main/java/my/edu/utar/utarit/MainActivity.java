package my.edu.utar.utarit;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);

        // 默认 fragment：HomeFragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

        // Bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.event) {
                replaceFragment(new EventFragment());
            } else if (id == R.id.add) {
                replaceFragment(new AddPostFragment());
            } else if (id == R.id.chat) {
                replaceFragment(new ChatFragment());
            } else if (id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        // FAB click → AddPostFragment
        fab.setOnClickListener(view -> replaceFragment(new AddPostFragment()));
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}
