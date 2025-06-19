package com.eneko.gastospersonalesaplicacion.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.eneko.gastospersonalesaplicacion.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK); // señal para que MainActivity se reinicie
        super.onBackPressed();
    }

}

