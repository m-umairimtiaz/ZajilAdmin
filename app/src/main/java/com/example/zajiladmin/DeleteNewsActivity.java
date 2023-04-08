package com.example.zajiladmin;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zajiladmin.news.NewsData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNewsActivity extends AppCompatActivity {

    private RecyclerView deleteNewsRecyclerView;
    private ProgressBar progressBar;
    private ArrayList<NewsData> list;
    private NewsAdapter adapter;

    private DatabaseReference reference;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_news);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delete News");

        deleteNewsRecyclerView = findViewById(R.id.deleteNewsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        reference = FirebaseDatabase.getInstance().getReference().child("News");
        deleteNewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deleteNewsRecyclerView.setHasFixedSize(true);

        getNews();
    }

    private void getNews() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NewsData data = snapshot.getValue(NewsData.class);
                    list.add(data);
                }

                adapter = new NewsAdapter(DeleteNewsActivity.this, list);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                deleteNewsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(DeleteNewsActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}