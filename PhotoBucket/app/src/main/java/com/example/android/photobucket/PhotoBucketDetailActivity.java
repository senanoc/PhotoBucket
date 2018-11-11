package com.example.android.photobucket;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.koushikdutta.ion.Ion;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class PhotoBucketDetailActivity extends AppCompatActivity {

    private DocumentSnapshot mDocSnapshot;
    private DocumentReference mDocRef;
    private TextView mCaptionTextView;
    private TextView mImageURLTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_bucket_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCaptionTextView = findViewById(R.id.detail_caption);
        mImageURLTextView = findViewById(R.id.detail_imageURL);

        String docId = getIntent().getStringExtra(Constants.EXTRA_DOC_ID);

        mDocRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).document(docId);
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(e != null){
                    Log.w(Constants.TAG, "Listen Failed!");
                    return;
                }
                if(documentSnapshot.exists()){
                    mDocSnapshot = documentSnapshot;
                    mCaptionTextView.setText((String)documentSnapshot.get(Constants.KEY_CAPTION));
                    mImageURLTextView.setText((String)documentSnapshot.get(Constants.KEY_URL));
                    Ion.with(mImageView).load((String)documentSnapshot.get(Constants.KEY_URL));

                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.photobucket_dialog, null, false);
        builder.setView(view);
        builder.setTitle("Edit this caption photo");
        final TextView captionEditText = view.findViewById(R.id.dialog_caption_edittext);
        final TextView imageURLEditText = view.findViewById(R.id.dialog_imageURL_edittext);

        captionEditText.setText((String)mDocSnapshot.get(Constants.KEY_CAPTION));
        imageURLEditText.setText((String)mDocSnapshot.get(Constants.KEY_URL));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> pb = new HashMap<>();
                pb.put(Constants.KEY_CAPTION, captionEditText.getText().toString());
                pb.put(Constants.KEY_URL, imageURLEditText.getText().toString());
                pb.put(Constants.KEY_CREATED, new Date());
                mDocRef.update(pb);
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);

        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                mDocRef.delete();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
