package com.gaiabit.gaiabit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Model.ChatModel;
import com.gaiabit.gaiabit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    Context context;
    List<ChatModel> list;

    public ChatAdapter(Context context, List<ChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) { // Image type
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_image, parent, false);
        } else { // Text type
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items, parent, false);
        }
        return new ChatHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getImage() != null && !list.get(position).getImage().isEmpty()) {
            return 1; // Type for image message
        }
        return 0; // Type for text message
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ChatModel message = list.get(position);

        assert user != null;
        if (getItemViewType(position) == 1) { // Image message
            if (message.getSenderID().equalsIgnoreCase(user.getUid())) {
                holder.rightChatImage.setVisibility(View.VISIBLE);
                holder.leftChatImage.setVisibility(View.GONE);
                Glide.with(context).load(message.getImage()).into(holder.rightChatImage);

                holder.readStatus.setVisibility(View.VISIBLE);
                holder.readStatus.setText(message.isRead() ? "已讀" : "未讀");
            } else {
                holder.leftChatImage.setVisibility(View.VISIBLE);
                holder.rightChatImage.setVisibility(View.GONE);
                Glide.with(context).load(message.getImage()).into(holder.leftChatImage);

                holder.readStatus.setVisibility(View.GONE);
            }
        } else { // Text message
            if (message.getSenderID().equalsIgnoreCase(user.getUid())) {
                holder.leftChat.setVisibility(View.GONE);
                holder.rightChat.setVisibility(View.VISIBLE);
                holder.rightChat.setText(message.getMessage());

                holder.readStatus.setVisibility(View.VISIBLE);
                holder.readStatus.setText(message.isRead() ? "已讀" : "未讀");
            } else {
                holder.rightChat.setVisibility(View.GONE);
                holder.leftChat.setVisibility(View.VISIBLE);
                holder.leftChat.setText(message.getMessage());
                holder.readStatus.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        TextView leftChat, rightChat, readStatus;
        ImageView leftChatImage, rightChatImage; // Only for image messages

        public ChatHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            leftChat = itemView.findViewById(R.id.leftChat);
            rightChat = itemView.findViewById(R.id.rightChat);
            readStatus = itemView.findViewById(R.id.readStatus);

            if (viewType == 1) {
                leftChatImage = itemView.findViewById(R.id.leftChatImage); // Ensure this ID exists in chat_item_image.xml
                rightChatImage = itemView.findViewById(R.id.rightChatImage); // Ensure this ID exists in chat_item_image.xml
            }
        }
    }
}
