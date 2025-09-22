package my.edu.utar.utarit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utarit.adapter.UserAdapter;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.User;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.network.SupabaseClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchFragment extends Fragment {

    private EditText inputSearch;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<Profile> userList = new ArrayList<>();
    private SupabaseApi api;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_search, container, false);

        inputSearch = view.findViewById(R.id.inputSearchUsername);
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);

        api = SupabaseClient.getApi();

        adapter = new UserAdapter(userList, profile -> {
            // Open chat with this user
            Bundle bundle = new Bundle();
            bundle.putString("otherUserId", profile.getId());
            MessageFragment chatFragment = new MessageFragment();
            chatFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void searchUsers(String username) {
        if (username.isEmpty()) {
            userList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        // Correct API method
        api.searchUsersByUsername(SupabaseClient.API_KEY, username)
                .enqueue(new Callback<List<Profile>>() {
                    @Override
                    public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userList.clear();
                            userList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Profile>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
