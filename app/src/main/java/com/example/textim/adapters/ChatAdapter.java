package com.example.textim.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textim.databinding.ReceivedMessagesContainerBinding;
import com.example.textim.databinding.SentMessagesContainerBinding;
import com.example.textim.models.ChatMsg;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMsg> chatMsgs;
    private final String crntUserId;
    private final Bitmap crntUserProfileImg;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;


    public ChatAdapter(List<ChatMsg> chatMsgs, String crntUserId, Bitmap crntUserProfileImg) {
        this.chatMsgs = chatMsgs;
        this.crntUserId = crntUserId;
        this.crntUserProfileImg = crntUserProfileImg;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    SentMessagesContainerBinding.inflate(LayoutInflater.from(parent.getContext())),
                    parent,
                    false
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ReceivedMessagesContainerBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMsgs.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMsgs.get(position), crntUserProfileImg);
        }
    }

    @Override
    public int getItemCount() {
        return chatMsgs.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMsgs.get(position).crntUserId.equals(crntUserId)){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final SentMessagesContainerBinding binding;

        SentMessageViewHolder(SentMessagesContainerBinding sentMessagesContainerBinding, ViewGroup parent, boolean b) {
            super(sentMessagesContainerBinding.getRoot());
            binding = sentMessagesContainerBinding;
        }

        void setData(ChatMsg chatMsg) {
            binding.message.setText(chatMsg.message);
            binding.dateText.setText(chatMsg.dateTime);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private ReceivedMessagesContainerBinding binding;

        ReceivedMessageViewHolder(ReceivedMessagesContainerBinding receivedMessagesContainerBinding){
            super(receivedMessagesContainerBinding.getRoot());
            binding = receivedMessagesContainerBinding;
        }

        void setData(ChatMsg chatMsg, Bitmap receiverProfileImg) {
            binding.message.setText(chatMsg.message);
            binding.dateText.setText(chatMsg.dateTime);
            binding.profileImage.setImageBitmap(receiverProfileImg);
        }
    }
}
