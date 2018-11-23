package com.manojbhadane.library_formcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
//        }
//        setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
