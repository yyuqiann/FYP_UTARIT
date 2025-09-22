package my.edu.utar.utarit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utarit.adapter.PostAdapter;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private SupabaseApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(adapter);

        api = SupabaseClient.getApi();
        loadPosts();

        return view;
    }

    private void loadPosts() {
        String accessToken = SessionManager.getInstance(requireContext()).getAccessToken();

        api.getPosts("Bearer " + accessToken)
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            postList.clear();
                            postList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
