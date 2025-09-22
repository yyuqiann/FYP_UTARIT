package my.edu.utar.utarit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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

public class CommentBottomSheet extends BottomSheetDialogFragment {

    private Post post;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private EditText editComment;
    private Button btnSend;

    public CommentBottomSheet(Post post) {
        this.post = post;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_bottomsheet, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewComments);
        editComment = view.findViewById(R.id.editComment);
        btnSend = view.findViewById(R.id.btnSendComment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(), commentList);
        recyclerView.setAdapter(commentAdapter);

        loadComments();

        btnSend.setOnClickListener(v -> {
            String text = editComment.getText().toString().trim();
            if (!text.isEmpty()) addComment(text);
        });

        return view;
    }

    private void loadComments() {
        String accessToken = SessionManager.getInstance(requireContext()).getAccessToken();
        SupabaseApi api = SupabaseClient.getApi();

        api.getComments(SupabaseConfig.API_KEY, "Bearer " + accessToken, post.getId())
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            commentList.clear();
                            commentList.addAll(response.body());
                            commentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addComment(String content) {
        String accessToken = SessionManager.getInstance(requireContext()).getAccessToken();
        SupabaseApi api = SupabaseClient.getApi();

        Map<String, Object> body = new HashMap<>();
        body.put("postId", post.getId());
        body.put("content", content);
        body.put("username", SessionManager.getInstance(requireContext()).getUsername());

        api.addComment(SupabaseConfig.API_KEY, "Bearer " + accessToken, body)
                .enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            commentList.add(response.body());
                            commentAdapter.notifyItemInserted(commentList.size() - 1);
                            editComment.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

