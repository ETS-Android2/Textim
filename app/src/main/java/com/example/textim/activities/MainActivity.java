package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.textim.databinding.ActivityMainBinding;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(getApplicationContext());
        fetchUserData();
        getToken();
        setListeners();
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
}