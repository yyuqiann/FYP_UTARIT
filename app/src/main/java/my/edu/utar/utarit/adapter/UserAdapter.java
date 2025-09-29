package my.edu.utar.utarit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.edu.utar.utarit.R;
import my.edu.utar.utarit.model.Profile;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<Profile> users;
    private OnUserClickListener listener;

    // 🔹 Constructor
    public UserAdapter(List<Profile> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Profile user = users.get(position);
        holder.username.setText(user.getDisplayName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // 🔹 ViewHolder
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.txtUsername);
        }
    }

    // 🔹 Callback interface
    public interface OnUserClickListener {
        void onUserClick(Profile profile);
    }
}
