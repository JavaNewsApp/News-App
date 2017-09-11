package com.example.ForMoreNews.bigproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yu on 17-9-11.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
    }
}