package my.edu.utar.utarit;

import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.List;

import my.edu.utar.utarit.adapter.CommentAdapter;
import my.edu.utar.utarit.model.Comment;
import my.edu.utar.utarit.network.SupabaseClient;

public class CommentFragment extends Fragment {

    private static final String ARG_POST_ID = "post_id";

    private RecyclerView recyclerView;
    private EditText editComment;
    private ImageButton btnSend;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
    private String postId;

    private CommentAddedCallback callback;

    public interface CommentAddedCallback {
        void onCommentAdded(String postId);
    }

    public static CommentFragment newInstance(String postId, CommentAddedCallback callback) {
        CommentFragment fragment = new CommentFragment();
        fragment.callback = callback;
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) postId = getArguments().getString(ARG_POST_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewComments);
        editComment = view.findViewById(R.id.editComment);
        btnSend = view.findViewById(R.id.btnSendComment);

        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadComments();

        btnSend.setOnClickListener(v -> {
            String content = editComment.getText().toString().trim();
            if (!TextUtils.isEmpty(content)) sendComment(content);
        });

        return view;
    }

    private void loadComments() {
        SupabaseClient.getPostComments(postId, requireContext(), new SupabaseClient.CommentsCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentList.clear();
                commentList.addAll(comments);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading comments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment(String content) {
        SupabaseClient.addPostComment(postId, content, requireContext(), new SupabaseClient.CommentCallback() {
            @Override
            public void onSuccess(Comment newComment) {
                editComment.setText("");
                commentList.add(0, newComment); // top of the list
                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);

                if (callback != null) callback.onCommentAdded(postId); // notify HomeFragment

                Toast.makeText(getContext(), "Comment sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
