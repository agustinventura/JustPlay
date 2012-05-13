package com.crankcode.activities;

import com.crankcode.services.MediaService;

import android.content.Intent;
import android.os.Bundle;

public class CrankPlayerActivity extends CrankActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(getBaseContext(), MediaService.class));
        setContentView(R.layout.main);
    }
}