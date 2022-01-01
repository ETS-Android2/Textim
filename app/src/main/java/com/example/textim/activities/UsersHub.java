package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.textim.adapters.UsersAdapter;
import com.example.textim.databinding.ActivityUsersHubBinding;
import com.example.textim.models.User;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersHub extends AppCompatActivity {

    private ActivityUsersHubBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(getApplicationContext());
        setListeners();
        loadUsers();
    }

    private void setListeners() {
        binding.prevIcon.setOnClickListener(v -> onBackPressed());
    }

    private void loadUsers() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String crntUsrID = prefManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        // Discard logged in user
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (crntUsrID.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users);
                            binding.usersRecView.setAdapter(usersAdapter);
                            binding.usersRecView.setVisibility(View.VISIBLE);
                        } else {
                            displayErrMsg();
                        }
                    } else {
                        displayErrMsg();
                    }
                });
    }

    private void displayErrMsg(){
        binding.errorMsg.setText(String.format("%s", "The users list is empty"));
        binding.errorMsg.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading) {
            binding.progBar.setVisibility(View.VISIBLE);
        } else {
            binding.progBar.setVisibility(View.INVISIBLE);
        }
    }

}