package my.edu.utar.utarit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import my.edu.utar.utarit.R;
import my.edu.utar.utarit.model.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(String userId);
    }

    private Context context;
    private List<Message> chats;
    private OnChatClickListener listener;

    public ChatAdapter(Context context, List<Message> chats, OnChatClickListener listener) {
        this.context = context;
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = chats.get(position);

        // Display receiver's username (or sender) and last message
        holder.username.setText(message.getReceiverId()); // Replace with actual username if available
        holder.lastMessage.setText(message.getContent());

        holder.itemView.setOnClickListener(v -> listener.onChatClick(message.getReceiverId()));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, lastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.chatUsername);
            lastMessage = itemView.findViewById(R.id.chatLastMessage);
        }
    }
}
