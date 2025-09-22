package my.edu.utar.utarit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.edu.utar.utarit.adapter.MessageAdapter;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.model.Message;
import my.edu.utar.utarit.network.SupabaseClient;
import my.edu.utar.utarit.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageButton sendButton;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private SupabaseApi api;
    private String currentUserId, otherUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.btnSend);

        currentUserId = SessionManager.getInstance(requireContext()).getUserId();
        otherUserId = getArguments() != null ? getArguments().getString("otherUserId") : null;

        api = SupabaseClient.getApi();

        adapter = new MessageAdapter(getContext(), messages, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadMessages();

        sendButton.setOnClickListener(v -> {
            String text = inputMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
                inputMessage.setText("");
            }
        });

        return view;
    }

    private void loadMessages() {
        String filter = "senderId.eq." + currentUserId + ",receiverId.eq." + otherUserId;
        api.getMessages(SupabaseConfig.API_KEY, "Bearer " + SessionManager.getInstance(requireContext()).getAccessToken(),
                        filter, "timestamp.asc")
                .enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messages.clear();
                            messages.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messages.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error loading messages: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessage(String content) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
        Message message = new Message(currentUserId, otherUserId, content, timestamp);

        api.sendMessage(SupabaseConfig.API_KEY, "Bearer " + SessionManager.getInstance(requireContext()).getAccessToken(),
                        message)
                .enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messages.add(response.body());
                            adapter.notifyItemInserted(messages.size() - 1);
                            recyclerView.scrollToPosition(messages.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(getContext(), "Error sending message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
