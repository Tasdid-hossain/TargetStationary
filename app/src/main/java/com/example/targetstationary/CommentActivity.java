package com.example.targetstationary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.targetstationary.ViewHolder.CommentAdapter;
import com.example.targetstationary.ViewHolder.OrderDetailsAdapter;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    ArrayList<String> commnetList;
    CommentAdapter commentAdapter;
    RecyclerView recyclerView_comment;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Feedbacks");

        recyclerView_comment = (RecyclerView) findViewById(R.id.recycler_comments);
        recyclerView_comment.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_comment.setLayoutManager(layoutManager);

        commnetList = getIntent().getStringArrayListExtra("commentList");
        loadComments();
    }

    private void loadComments() {
        commentAdapter = new CommentAdapter(commnetList, this);
        commentAdapter.notifyDataSetChanged();
        recyclerView_comment.setAdapter(commentAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
