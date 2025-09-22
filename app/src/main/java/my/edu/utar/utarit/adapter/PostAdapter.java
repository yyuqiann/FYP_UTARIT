package my.edu.utar.utarit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.edu.utar.utarit.CommentBottomSheet;
import my.edu.utar.utarit.R;
import my.edu.utar.utarit.SupabaseConfig;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.username.setText(post.getUsername());
        holder.content.setText(post.getContent());
        holder.likes.setText(String.valueOf(post.getLikes()));

        // Like button
        holder.btnLike.setOnClickListener(v -> {
            String accessToken = SessionManager.getInstance(context).getAccessToken();
            SupabaseApi api = SupabaseClient.getApi();

            // update local UI
            int newLikes = post.getLikes() + 1;
            post.setLikes(newLikes);
            holder.likes.setText(String.valueOf(newLikes));

            // update backend
            api.updatePostLikes(SupabaseConfig.API_KEY, "Bearer " + accessToken, post.getId(), newLikes)
                    .enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(context, "Like update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Comment button
        holder.btnComment.setOnClickListener(v -> {
            CommentBottomSheet bottomSheet = new CommentBottomSheet(post);
            bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentSheet");
        });

        // Share button
        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getContent());
            context.startActivity(Intent.createChooser(shareIntent, "Share Post"));
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, content, likes;
        ImageView btnLike, btnComment, btnShare;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.postUsername);
            content = itemView.findViewById(R.id.postContent);
            likes = itemView.findViewById(R.id.postLikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}
