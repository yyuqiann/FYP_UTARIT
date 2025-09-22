package my.edu.utar.utarit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.edu.utar.utarit.adapter.UserListAdapter;

public class CreateChatActivity extends AppCompatActivity {

    private EditText searchUserInput;
    private RecyclerView recyclerView;
    private UserListAdapter adapter;

    private List<String> allUsers = Arrays.asList("Alice", "Bob", "Charlie", "David", "Evelyn", "FICT Club");
    private List<String> filteredUsers = new ArrayList<>(allUsers);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        Toolbar toolbar = findViewById(R.id.create_chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        searchUserInput = findViewById(R.id.searchUserInput);
        recyclerView = findViewById(R.id.recyclerViewUsers);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(filteredUsers, user ->
                Toast.makeText(this, "Start chat with " + user, Toast.LENGTH_SHORT).show());
        recyclerView.setAdapter(adapter);

        searchUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterUsers(String query) {
        filteredUsers.clear();
        for (String user : allUsers) {
            if (user.toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
