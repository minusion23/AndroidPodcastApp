package com.example.android.yourpodcastapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.example.android.yourpodcastapp.Database.PodcastFeedDetails;
import com.example.android.yourpodcastapp.PodcastSearchItem;
import com.example.android.yourpodcastapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Szymon on 08.02.2019.
 */

public class PodcastDetailsAdapter extends RecyclerView.Adapter<PodcastDetailsAdapter.PodcastEpisodeDetailsViewHolder> {
    //Set up adapter to be used with witth the details activity
    private static final String TAG = PodcastSearchItemAdapter.class.getSimpleName();

    final private GridViewClickListener mOnClickListener;

    private List<PodcastEpisodeDetails> items;

    private Context mContext;


//Constructor for the class, the way details are passed to the constructor

    public PodcastDetailsAdapter(List<PodcastEpisodeDetails> items, Context context, GridViewClickListener listener) {

        mContext = context;
        mOnClickListener = listener;
        this.items = items;
    }


//    On click interface to be implemented in the activity

    public interface GridViewClickListener {
        void onGridItemClick(int clickedItemIndex);
    }

    //    Inflate the singe views to be used for the app
    @Override
    public PodcastEpisodeDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.episode_details_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new PodcastEpisodeDetailsViewHolder(view);
    }

    //   Bind data to the inflated views
    @Override
    public void onBindViewHolder(PodcastEpisodeDetailsViewHolder holder, int position) {

//        assert mItem != null;
        Log.v("Test",String.valueOf(items.get(position).getEpisodeArt()));
        Picasso.with(mContext).load(items.get(position).getEpisodeArt()).fit().into(holder.myImageView);
        holder.myImageView.setContentDescription(items.get(position).getDescription());
        holder.seasonNumberTv.setText(items.get(position).getSeasonNumber());
        holder.episodeTitleTv.setText(items.get(position).getTitle());
        String duration = "Duration ".concat(items.get(position).getDuration());
        holder.episodeDurationTv.setText(duration);
        holder.episodeDateTv.setText(items.get(position).getDate().substring(0, 16));
        holder.episodeDescriptionTv.setText(items.get(position).getDescription());
        holder.episodeNumberTv.setText(items.get(position).getEpisodeNumber());
        holder.seasonNumberTv.setText(items.get(position).getSeasonNumber());

    }

//    Get the list count return the correct number of items

    @Override
    public int getItemCount() {
        int a ;

        if(items != null && !items.isEmpty()) {

            a = items.size();
        }
        else {

            a = 0;

        }

        return a;
    }

    // stores and recycles views as they are scrolled off screen

    public class PodcastEpisodeDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView episodeTitleTv;
        ImageView myImageView;
        TextView episodeDurationTv;
        TextView episodeDateTv;
        TextView episodeDescriptionTv;
        TextView episodeNumberTv;
        TextView seasonNumberTv;

        PodcastEpisodeDetailsViewHolder(View itemView) {
            super(itemView);

            episodeTitleTv= itemView.findViewById(R.id.episode_title);
            myImageView = itemView.findViewById(R.id.episode_artwork);
            episodeDurationTv = itemView.findViewById(R.id.episode_duration);
            episodeDateTv = itemView.findViewById(R.id.episode_date);
            episodeDescriptionTv = itemView.findViewById(R.id.episode_description);
            episodeNumberTv = itemView.findViewById(R.id.episode_reference);
            seasonNumberTv = itemView.findViewById(R.id.season_reference);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onGridItemClick(clickedPosition);
        }
    }

    public PodcastEpisodeDetails getItem(int id) {
        return items.get(id);

    }

}
