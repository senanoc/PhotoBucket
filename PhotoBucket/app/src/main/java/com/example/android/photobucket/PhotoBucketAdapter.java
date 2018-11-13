package com.example.android.photobucket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class PhotoBucketAdapter extends RecyclerView.Adapter<PhotoBucketAdapter.PhotoBucketViewHolder>{

    private List<DocumentSnapshot> mPhotoBucketSnapshots = new ArrayList<>();


    public PhotoBucketAdapter() {
        CollectionReference photoBucketRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH);

        photoBucketRef.orderBy(Constants.KEY_CREATED, Query.Direction.DESCENDING).limit(50)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(Constants.TAG, "Listening failed!");
                            return;
                        }
                        mPhotoBucketSnapshots = documentSnapshots.getDocuments();
                        notifyDataSetChanged();

                    }
                });
    }

    @NonNull
    @Override
    public PhotoBucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photobucket_itemview, parent, false);
        return new PhotoBucketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoBucketViewHolder PhotoBucketViewHolder, int i) {
        DocumentSnapshot ds = mPhotoBucketSnapshots.get(i);
        String caption = (String)ds.get(Constants.KEY_CAPTION);
        String imageURL = (String)ds.get(Constants.KEY_URL);
        PhotoBucketViewHolder.mCaptionTextView.setText(caption);
        PhotoBucketViewHolder.mImageURLTextView.setText(imageURL);
        //Ion.with(PhotoBucketDetailActivity.mImageView).load(imageURL);
    }

    @Override
    public int getItemCount() {
        return mPhotoBucketSnapshots.size();
    }

    class PhotoBucketViewHolder extends RecyclerView.ViewHolder {
        private TextView mCaptionTextView;
        private TextView mImageURLTextView;

        public PhotoBucketViewHolder(@NonNull View itemView) {
            super(itemView);
            mCaptionTextView = itemView.findViewById(R.id.itemview_caption);
            mImageURLTextView = itemView.findViewById(R.id.itemview_imageURL);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentSnapshot ds = mPhotoBucketSnapshots.get(getAdapterPosition());
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PhotoBucketDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_DOC_ID, ds.getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}