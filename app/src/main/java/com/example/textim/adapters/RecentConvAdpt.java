package com.example.textim.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textim.databinding.RecentCoversationsContainerBinding;
import com.example.textim.listeners.ConvosListener;
import com.example.textim.models.ChatMsg;
import com.example.textim.models.User;

import java.util.List;

public class RecentConvAdpt extends RecyclerView.Adapter<RecentConvAdpt.ConversationViewHolder> {

    private final List<ChatMsg> chatMsgs;
    private final ConvosListener convosListener;

    public RecentConvAdpt(List<ChatMsg> chatMsgs, ConvosListener convosListener) {
        this.chatMsgs = chatMsgs;
        this.convosListener = convosListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                RecentCoversationsContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMsgs.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMsgs.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        RecentCoversationsContainerBinding binding;
        ConversationViewHolder(RecentCoversationsContainerBinding recentCoversationsContainerBinding){
            super(recentCoversationsContainerBinding.getRoot());
            binding = recentCoversationsContainerBinding;

        }

        void setData(ChatMsg chatMsg) {
            binding.username.setText(chatMsg.convoName);
            binding.recentMessage.setText(chatMsg.message);
            binding.profileImage.setImageBitmap(getConvoImg(chatMsg.convoImg));
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMsg.convoID;
                user.name = chatMsg.convoName;
                user.image = chatMsg.convoImg;
                convosListener.onConvoClicked(user);
            });
        }
    }

    private Bitmap getConvoImg (String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
