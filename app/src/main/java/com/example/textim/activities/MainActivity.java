package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.textim.R;
import com.example.textim.databinding.ActivityLogInBinding;
import com.example.textim.databinding.ActivityMainBinding;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getApplicationContext());
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fetchUserData();
    }

    private void fetchUserData() {
        binding.username.setText(prefManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(prefManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
        binding.profileImage.setImageBitmap(bitmap);
    }
}