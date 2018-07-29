package com.zukkadev.it.flickrtourist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zukkadev.it.flickrtourist.model.FlickrImages;
import com.zukkadev.it.flickrtourist.utils.Constants;

import java.util.List;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.ImagesViewHolder> {

    private List<FlickrImages> mImageData;
    private Context mContext;

    public ImagesRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView flickrImage;
        ImagesViewHolder(View itemView) {
            super(itemView);
            flickrImage = itemView.findViewById(R.id.flickr_image);
        }
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);

        ImagesViewHolder vh = new ImagesViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        final FlickrImages image = mImageData.get(position);
        Picasso.with( mContext ).load( image.getImageURL()).into( holder.flickrImage );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShowDetails = new Intent(mContext, DetailActivity.class);
                Bundle imageBundle = new Bundle();
                imageBundle.putString (Constants.Title, image.getImageTitle());
                imageBundle.putString(Constants.URL, image.getImageURL());
                intentShowDetails.putExtras(imageBundle);
                mContext.startActivity(intentShowDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mImageData) return 0;
        return mImageData.size();
    }

    public void setImageData(List<FlickrImages> imageData) {
        mImageData = imageData;
        notifyDataSetChanged();
    }
}
