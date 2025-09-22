package my.edu.utar.utarit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utarit.adapter.ChatAdapter;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Message;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> chats = new ArrayList<>();
    private SupabaseApi api;
    private String currentUserId;

    @Nullable
    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        api = SupabaseClient.getApi();
        currentUserId = SessionManager.getInstance(requireContext()).getUserId();

        adapter = new ChatAdapter(getContext(), chats, userId -> {
            // TODO: open MessageFragment for this user
            Toast.makeText(getContext(), "Open chat with: " + userId, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        loadChats();

        return view;
    }

    private void loadChats() {
        String filter = "senderId.eq." + currentUserId + ",receiverId.eq." + currentUserId;
        api.getMessages(SupabaseConfig.API_KEY, "Bearer " + SessionManager.getInstance(requireContext()).getAccessToken(),
                        filter, "timestamp.desc")
                .enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            chats.clear();
                            chats.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to load chats", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
