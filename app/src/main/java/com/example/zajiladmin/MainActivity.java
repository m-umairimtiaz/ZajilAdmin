package com.example.zajiladmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.zajiladmin.member.AddMember;
import com.example.zajiladmin.news.UploadNews;
import com.example.zajiladmin.uploadimage.UploadImage;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView uploadnews, uploadimage, addmember, deletenews;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Zajil Admin Dashboard");

        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin", "false").equals("false")) {
            openLogin();
        }

        uploadnews = findViewById(R.id.uploadNews);
        uploadimage = findViewById(R.id.uploadImage);
        addmember = findViewById(R.id.member);
        deletenews = findViewById(R.id.delete);


        uploadnews.setOnClickListener(this);
        uploadimage.setOnClickListener(this);
        addmember.setOnClickListener(this);
        deletenews.setOnClickListener(this);

    }


    private void openLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.uploadNews:
                intent = new Intent(MainActivity.this, UploadNews.class);
                startActivity(intent);
                break;
            case R.id.uploadImage:
                intent = new Intent(MainActivity.this, UploadImage.class);
                startActivity(intent);
                break;
            case R.id.member:
                intent = new Intent(MainActivity.this, AddMember.class);
                startActivity(intent);
                break;
            case R.id.delete:
                intent = new Intent(MainActivity.this, DeleteNewsActivity.class);
                startActivity(intent);
                break;
        }
    }
}