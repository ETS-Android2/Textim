package com.example.textim.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.textim.adapters.RecentConvAdpt;
import com.example.textim.databinding.ActivityMainBinding;
import com.example.textim.listeners.ConvosListener;
import com.example.textim.models.ChatMsg;
import com.example.textim.models.User;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConvosListener {

    private ActivityMainBinding binding;
    private PrefManager prefManager;
    private List<ChatMsg> convos;
    private RecentConvAdpt convosAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(getApplicationContext());
        init();
        fetchUserData();
        getToken();
        setListeners();
        lsnConvos();
    }

    private void init() {
        convos = new ArrayList<>();
        convosAdapter = new RecentConvAdpt(convos, this);
        binding.recentsRecView.setAdapter(convosAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.logout.setOnClickListener(v->logOut());

        binding.newChat.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), UsersHub.class)));
    }

    private void fetchUserData() {
        binding.username.setText(prefManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(prefManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
        binding.profileImage.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void lsnConvos(){
        db.collection(Constants.KEY_COLLECTION_RECENTS)
                .whereEqualTo(Constants.KEY_SENDER_ID, prefManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_RECENTS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, prefManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()){

                if(documentChange.getType() == DocumentChange.Type.ADDED) {

                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMsg chatMsg = new ChatMsg();
                    chatMsg.senderId = senderId;
                    chatMsg.receiverId = receiverId;

                    if (prefManager.getString(Constants.KEY_USER_ID).equals(senderId)) {

                        chatMsg.convoImg = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMG);
                        chatMsg.convoName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMsg.convoID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                    } else {

                        chatMsg.convoImg = documentChange.getDocument().getString(Constants.KEY_SENDER_IMG);
                        chatMsg.convoName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMsg.convoID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);

                    }

                    chatMsg.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMsg.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    convos.add(chatMsg);

                } else if (documentChange.getType() ==  DocumentChange.Type.MODIFIED) {

                    for (int i = 0; i < convos.size(); i++) {

                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                        if (convos.get(i).senderId.equals(senderId) && convos.get(i).receiverId.equals(receiverId)) {

                            convos.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            convos.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;

                        }
                    }
                }
            }

            Collections.sort(convos, (obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));

            convosAdapter.notifyDataSetChanged();
            binding.recentsRecView.smoothScrollToPosition(0);
            binding.recentsRecView.setVisibility(View.VISIBLE);
            binding.progBar.setVisibility(View.GONE);
        }
    };

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef =
                db.collection(Constants.KEY_COLLECTION_USERS).document(
                        prefManager.getString(Constants.KEY_USER_ID)
                );

        docRef.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e -> showToast("Error Updating Token"));
    }

    private void logOut(){
        showToast("Logging Out!");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Constants.KEY_COLLECTION_USERS).document(
                prefManager.getString(Constants.KEY_USER_ID)
        );
        HashMap<String, Object> ref = new HashMap<>();
        ref.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        docRef.update(ref)
                .addOnSuccessListener(unused -> {
                    prefManager.clear();
                    startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Not able to logout!"));
    }

    @Override
    public void onConvoClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}