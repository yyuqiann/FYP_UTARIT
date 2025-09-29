package my.edu.utar.utarit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;

public class AddPostFragment extends Fragment {

    private EditText editPostContent;
    private Button btnPost;
    private boolean isPosting = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editPostContent = view.findViewById(R.id.editPostContent);
        btnPost = view.findViewById(R.id.btnPost);

        // Enable/disable post button based on text input
        editPostContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnPost.setEnabled(!s.toString().trim().isEmpty() && !isPosting);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnPost.setOnClickListener(v -> createPost());
    }

    private void createPost() {
        String content = editPostContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Please enter some content", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        SupabaseClient.createPost(content, requireContext(), new SupabaseClient.PostCallback() {
            @Override
            public void onSuccess(Post post) {
                setLoadingState(false);
                Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                editPostContent.setText("");
                navigateToHome();
            }

            @Override
            public void onError(String error) {
                setLoadingState(false);
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean loading) {
        isPosting = loading;
        btnPost.setEnabled(!loading && !editPostContent.getText().toString().trim().isEmpty());
        btnPost.setText(loading ? "Posting..." : "Post");
        editPostContent.setEnabled(!loading);
    }

    private void navigateToHome() {
        // Switch bottom navigation to home
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.home);
        }

        // Optionally, refresh HomeFragment
        Fragment homeFragment = getParentFragmentManager().findFragmentByTag("HOME_FRAGMENT_TAG");
        if (homeFragment instanceof HomeFragment) {
            ((HomeFragment) homeFragment).refreshPosts();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoadingState(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        setLoadingState(false);
    }
}
