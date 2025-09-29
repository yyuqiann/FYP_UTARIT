package my.edu.utar.utarit;

import static my.edu.utar.utarit.network.SupabaseClient.getApi;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utarit.adapter.PostAdapter;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private static final String TAG = "SupabaseHome";

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the PostAdapter with a comment click listener
        postAdapter = new PostAdapter(requireContext(), postList, post -> {
            // Open CommentFragment with a callback
            CommentFragment fragment = CommentFragment.newInstance(post.getId(), postId -> {
                // Find the post in the list and increment its comment count
                for (Post p : postList) {
                    if (p.getId().equals(postId)) {
                        p.addCommentCount(1); // optional helper in Post model
                        postAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            });

            // Replace the current fragment with CommentFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(postAdapter);

        // Fetch posts from Supabase
        fetchPosts();

        return view;
    }


    private void fetchPosts() {
        String accessToken = SessionManager.getInstance(requireContext()).getAccessToken();
        if (accessToken == null) return;

        String authHeader = "Bearer " + accessToken;
        getApi()
                .getAllPostsWithDetails(SupabaseClient.API_KEY, authHeader)
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            postList.clear();
                            postList.addAll(response.body());
                            postAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Fetched " + postList.size() + " posts");
                        } else {
                            Log.e(TAG, "Error fetching posts: " + response.code() + " " + response.message());
                            Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Network error fetching posts", t);
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ================= Refresh posts =================
    public void refreshPosts() {
        SupabaseClient.getPosts(requireContext(), new SupabaseClient.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                postList.clear();
                postList.addAll(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading posts: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
