package my.edu.utar.utarit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostFragment extends Fragment {

    private EditText postContentEditText;
    private Button btnPost;

    private SupabaseApi api;
    private String accessToken;
    private String userId;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        postContentEditText = view.findViewById(R.id.editPostContent);
        btnPost = view.findViewById(R.id.btnPost);

        api = SupabaseClient.getApi();
        accessToken = SessionManager.getInstance(requireContext()).getAccessToken();
        userId = SessionManager.getInstance(requireContext()).getUserId();
        username = SessionManager.getInstance(requireContext()).getUsername();

        btnPost.setOnClickListener(v -> {
            String content = postContentEditText.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(requireContext(), "Post cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            sendPost(content);
        });

        return view;
    }

    private void sendPost(String content) {
        // Provide default values for fields that backend may set
        Post post = new Post(userId, username, content, "", 0, 0);

        api.addPost(SupabaseConfig.API_KEY, "Bearer " + accessToken, post)
                .enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Post added successfully!", Toast.LENGTH_SHORT).show();
                            postContentEditText.setText("");
                        } else {
                            Toast.makeText(requireContext(), "Failed to add post: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
