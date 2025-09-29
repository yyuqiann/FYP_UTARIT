package my.edu.utar.utarit.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.edu.utar.utarit.R;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnCommentClickListener {
        void onCommentClick(Post post);
    }

    private final Context context;
    private final List<Post> posts;
    private final String currentUserId;
    private final OnCommentClickListener commentClickListener;

    public PostAdapter(Context context, List<Post> posts, OnCommentClickListener listener) {
        this.context = context;
        this.posts = posts;
        this.currentUserId = SessionManager.getInstance(context).getUserId();
        this.commentClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.usernameTextView.setText(post.getDisplayName());
        holder.contentTextView.setText(post.getContent());
        holder.likesCountText.setText(String.valueOf(post.getLikesCount()));
        holder.commentCountText.setText(String.valueOf(post.getCommentsCount()));

        // Set like icon
        boolean isLiked = post.isLikedBy(currentUserId);
        holder.likeButton.setImageResource(isLiked ? R.drawable.ic_like_filled : R.drawable.ic_like_outline);

        // Like button click
        holder.likeButton.setOnClickListener(v -> {
            holder.likeButton.setEnabled(false); // disable during API call
            boolean currentlyLiked = post.isLikedBy(currentUserId);

            if (currentlyLiked) {
                // Optimistically remove like
                post.removeLike(currentUserId);
                holder.likeButton.setImageResource(R.drawable.ic_like_outline);
                holder.likesCountText.setText(String.valueOf(post.getLikesCount()));

                SupabaseClient.removeLike(post.getId(), context, new SupabaseClient.LikeCallback() {
                    @Override
                    public void onSuccess(boolean isLikedNow) {
                        holder.likeButton.setEnabled(true);
                    }

                    @Override
                    public void onError(String error) {
                        // Rollback on error
                        post.addLike(currentUserId);
                        holder.likeButton.setImageResource(R.drawable.ic_like_filled);
                        holder.likesCountText.setText(String.valueOf(post.getLikesCount()));
                        Toast.makeText(context, "Failed to unlike: " + error, Toast.LENGTH_SHORT).show();
                        holder.likeButton.setEnabled(true);
                    }
                });
            } else {
                // Optimistically add like
                post.addLike(currentUserId);
                holder.likeButton.setImageResource(R.drawable.ic_like_filled);
                holder.likesCountText.setText(String.valueOf(post.getLikesCount()));

                SupabaseClient.addPostLike(post.getId(), context, new SupabaseClient.LikeCallback() {
                    @Override
                    public void onSuccess(boolean isLikedNow) {
                        holder.likeButton.setEnabled(true);
                    }

                    @Override
                    public void onError(String error) {
                        // Rollback on error
                        post.removeLike(currentUserId);
                        holder.likeButton.setImageResource(R.drawable.ic_like_outline);
                        holder.likesCountText.setText(String.valueOf(post.getLikesCount()));
                        Toast.makeText(context, "Failed to like: " + error, Toast.LENGTH_SHORT).show();
                        holder.likeButton.setEnabled(true);
                    }
                });
            }
        });

        // Share button
        holder.shareButton.setOnClickListener(v -> {
            String postLink = "https://utarit/posts/" + post.getId();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Post Link", postLink));
            Toast.makeText(context, "Post link copied!", Toast.LENGTH_SHORT).show();
        });

        // Comment button
        // Comment button opens fragment
        holder.commentButton.setOnClickListener(v -> {
            if (commentClickListener != null) {
                commentClickListener.onCommentClick(post);
            }

        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, contentTextView, likesCountText, commentCountText;
        ImageView likeButton, shareButton, commentButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.postUsername);
            contentTextView = itemView.findViewById(R.id.postContent);
            likesCountText = itemView.findViewById(R.id.postLikes);
            commentCountText = itemView.findViewById(R.id.postComments);
            likeButton = itemView.findViewById(R.id.btnLike);
            shareButton = itemView.findViewById(R.id.btnShare);
            commentButton = itemView.findViewById(R.id.btnComment);
        }
    }
}
