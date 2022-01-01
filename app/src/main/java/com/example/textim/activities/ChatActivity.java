package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.textim.databinding.ActivityChatBinding;
import com.example.textim.models.User;
import com.example.textim.utilities.Constants;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User crntUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadCrntUserDetails();
    }

    private void setListeners(){
        binding.prevIcon.setOnClickListener(v -> onBackPressed());
    }

    private void loadCrntUserDetails(){
        crntUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.username.setText(crntUser.name);
    }

}