package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.textim.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}