package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.textim.adapters.ChatAdapter;
import com.example.textim.databinding.ActivityChatBinding;
import com.example.textim.models.ChatMsg;
import com.example.textim.models.User;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receivingUser;
    private List<ChatMsg> chatMsgs;
    private ChatAdapter chatAdapter;
    private PrefManager prefManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverData();
        init();
        lsnMsg();
    }

    private void init(){
        prefManager = new PrefManager(getApplicationContext());
        chatMsgs = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMsgs,
                prefManager.getString(Constants.KEY_USER_ID),
                getBitmapFromEncStr(receivingUser.image)
        );
        binding.chatRecView.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void sendMsg(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, prefManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receivingUser.id);
        message.put(Constants.KEY_MESSAGE, binding.message.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        db.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.message.setText(null);
    }

    private void lsnMsg(){
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, prefManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receivingUser.id)
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receivingUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, prefManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            int count = chatMsgs.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMsg chatMsg = new ChatMsg();
                    chatMsg.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMsg.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMsg.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMsg.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMsg.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMsgs.add(chatMsg);
                }
            }
            Collections.sort(chatMsgs, (obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMsgs.size(), chatMsgs.size());
                binding.chatRecView.smoothScrollToPosition(chatMsgs.size() - 1);
            }
            binding.chatRecView.setVisibility(View.VISIBLE);
        }
        binding.progBar.setVisibility(View.GONE);
    };

    private void loadReceiverData(){
        receivingUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.username.setText(receivingUser.name);
    }

    private Bitmap getBitmapFromEncStr(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setListeners(){
        binding.prevIcon.setOnClickListener(v -> onBackPressed());
        binding.sendFrame.setOnClickListener(v -> sendMsg());
    }

    private String getReadableDateTime(Date date) {
        return  new SimpleDateFormat("MMMM dd, yyyy - hh-mm a", Locale.getDefault()).format(date);
    }

}