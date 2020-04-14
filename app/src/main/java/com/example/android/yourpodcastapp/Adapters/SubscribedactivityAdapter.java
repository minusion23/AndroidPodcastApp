package com.example.android.yourpodcastapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.yourpodcastapp.Database.PodcastFeedDetails;
import com.example.android.yourpodcastapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 09.02.2019.
 */

public class SubscribedactivityAdapter extends RecyclerView.Adapter<SubscribedactivityAdapter.SubscribedActivityHolder> {
    //Adapter used with the subscribed activity. Using grid view and custom on click listener to open podcast details
    private GridViewClickListener mOnClickListener;

    private Context mContext;

    private List<PodcastFeedDetails> subscribedPodcasts = new ArrayList<>();

    // Creating a constructor allowing a listener
    public SubscribedactivityAdapter(GridViewClickListener listener, Context context) {

        mContext = context;
        mOnClickListener = listener;
    }

    public PodcastFeedDetails getItem(int id) {
        return subscribedPodcasts.get(id);

    }

    // Creating the object
    @NonNull
    @Override
    public SubscribedActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.search_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new SubscribedActivityHolder(view, mOnClickListener);
    }
//Binding data to the holder

    @Override
    public void onBindViewHolder(@NonNull SubscribedActivityHolder holder, int position) {

        Log.v("Test", String.valueOf(subscribedPodcasts.get(position).getArtWork()));
        Picasso.with(mContext).load(subscribedPodcasts.get(position).getArtWork()).fit().into(holder.myImageView);
        holder.myImageView.setContentDescription(subscribedPodcasts.get(position).getTitle());
    }

    public List<PodcastFeedDetails> getSubscribedPodcasts() {
        return subscribedPodcasts;
    }

    public void setPodcasts(List<PodcastFeedDetails> subscribedPodcasts) {

        this.subscribedPodcasts = subscribedPodcasts;
        notifyDataSetChanged();
    }

//    Getting the number of items to be drawn

    @Override
    public int getItemCount() {
        int a;

        if (subscribedPodcasts != null && !subscribedPodcasts.isEmpty()) {

            a = subscribedPodcasts.size();
        } else {

            a = 0;

        }

        return a;
    }


    public interface GridViewClickListener {
        void onGridItemClick(int clickedItemIndex);
    }

    class SubscribedActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView myImageView;
        GridViewClickListener gridViewClickListener;

        SubscribedActivityHolder(View itemView, GridViewClickListener gridViewClickListener) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.searchGridImageItem);
            itemView.setOnClickListener(this);
            this.gridViewClickListener = gridViewClickListener;
        }

        @Override
        public void onClick(View v) {

            gridViewClickListener.onGridItemClick(getAdapterPosition());

        }
    }
}

