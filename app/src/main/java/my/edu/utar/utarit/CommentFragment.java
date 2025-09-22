package my.edu.utar.utarit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import my.edu.utar.utarit.adapter.CommentAdapter;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Comment;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editComment;
    private ImageButton btnSend;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
    private Post post;
    private SupabaseApi api;

    public CommentFragment(Post post) {
        this.post = post;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewComments);
        editComment = view.findViewById(R.id.editComment);
        btnSend = view.findViewById(R.id.btnSendComment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentAdapter(getContext(), commentList);
        recyclerView.setAdapter(adapter);

        api = SupabaseClient.getApi();
        loadComments();

        btnSend.setOnClickListener(v -> {
            String text = editComment.getText().toString().trim();
            if (!text.isEmpty()) sendComment(text);
        });

        return view;
    }

    private void loadComments() {
        String accessToken = SessionManager.getInstance(requireContext()).getAccessToken();
        api.getComments(SupabaseConfig.API_KEY, "Bearer " + accessToken, post.getId())
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            commentList.clear();
                            commentList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendComment(String text) {
        String accessToken = SessionManager.getInstance(requireContext()).getAccessToken();
        Map<String, Object> body = new HashMap<>();
        body.put("post_id", post.getId());
        body.put("content", text);

        api.addComment(SupabaseConfig.API_KEY, "Bearer " + accessToken, body)
                .enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            commentList.add(response.body());
                            adapter.notifyItemInserted(commentList.size() - 1);
                            editComment.setText("");
                        } else {
                            Toast.makeText(getContext(), "Failed to send comment", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
