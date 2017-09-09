package com.ihandy.a2014011328.bigproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by yu on 17-9-9.
 */

public class FunctionActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        ImageButton imageButton1 = (ImageButton) findViewById(R.id.favorite);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.category);
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.about_me);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent reg = new
                        Intent(FunctionActivity.this, Favorites.class);
                startActivity(reg);
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent reg = new
                        Intent(FunctionActivity.this, Setting.class);
                startActivityForResult(reg,6);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent reg = new
                        Intent(FunctionActivity.this, AboutMe.class);
                startActivity(reg);
            }
        });
    }
}
