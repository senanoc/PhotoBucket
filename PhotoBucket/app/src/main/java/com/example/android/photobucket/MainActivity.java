package com.example.android.photobucket;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        PhotoBucketAdapter photoBucketAdapter = new PhotoBucketAdapter();
        recyclerView.setAdapter(photoBucketAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();

            }
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.photobucket_dialog, null, false);
        builder.setView(view);
        builder.setTitle("Add a caption photo");
        final TextView captionEditText = view.findViewById(R.id.dialog_caption_edittext);
        final TextView imageURLEditText = view.findViewById(R.id.dialog_imageURL_edittext);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> pb = new HashMap<>();
                pb.put(Constants.KEY_CAPTION, captionEditText.getText().toString());
                pb.put(Constants.KEY_URL, imageURLEditText.getText().toString());
                pb.put(Constants.KEY_CREATED, new Date());
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).add(pb);
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);

        builder.create().show();
    }



}
