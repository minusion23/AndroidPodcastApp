package com.example.android.yourpodcastapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.example.android.yourpodcastapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 10.02.2019.
 */

public class FavoritedFragmentAdapter extends RecyclerView.Adapter<FavoritedFragmentAdapter.FavoritedActivityHolder> {
//Set up adapter to be used with

    private EpisodeItemClickListener mOnClickListener;

    private Context mContext;

    private List<PodcastEpisodeDetails> favoritedEpisodes = new ArrayList<>();

    // Creating a constructor allowing a listener
    public FavoritedFragmentAdapter(EpisodeItemClickListener listener, Context context) {

        mContext = context;
        mOnClickListener = listener;
    }

    public PodcastEpisodeDetails getItem(int id) {
        return favoritedEpisodes.get(id);

    }

    // Creating the object
    @NonNull
    @Override
    public FavoritedFragmentAdapter.FavoritedActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.episode_details_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new FavoritedActivityHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritedFragmentAdapter.FavoritedActivityHolder holder, int position) {

        assert favoritedEpisodes != null;
        Picasso.with(mContext).load(favoritedEpisodes.get(position).getEpisodeArt()).fit().into(holder.myImageView);
        holder.myImageView.setContentDescription(favoritedEpisodes.get(position).getDescription());
        holder.seasonNumberTv.setText(favoritedEpisodes.get(position).getSeasonNumber());
        holder.episodeTitleTv.setText(favoritedEpisodes.get(position).getTitle());
        String duration = "Duration ".concat(favoritedEpisodes.get(position).getDuration());
        holder.episodeDurationTv.setText(duration);
        holder.episodeDateTv.setText(favoritedEpisodes.get(position).getDate().substring(0, 16));
        holder.episodeDescriptionTv.setText(favoritedEpisodes.get(position).getDescription());
        holder.episodeNumberTv.setText(favoritedEpisodes.get(position).getEpisodeNumber());
        holder.seasonNumberTv.setText(favoritedEpisodes.get(position).getSeasonNumber());

    }
//Binding data to the holder

    public List<PodcastEpisodeDetails> getFavoritedEpisodes() {
        return favoritedEpisodes;
    }

    public void setFavoritedEpisodes(List<PodcastEpisodeDetails> favoritedEpisodes) {

        this.favoritedEpisodes = favoritedEpisodes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int a;

        if (favoritedEpisodes != null && !favoritedEpisodes.isEmpty()) {

            a = favoritedEpisodes.size();
        } else {

            a = 0;

        }

        return a;
    }

//    Getting the number of items to be drawn

    public interface EpisodeItemClickListener {
        void onEpisodeItemClickListener(int clickedItemIndex);
    }

    class FavoritedActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView episodeTitleTv;
        ImageView myImageView;
        TextView episodeDurationTv;
        TextView episodeDateTv;
        TextView episodeDescriptionTv;
        TextView episodeNumberTv;
        TextView seasonNumberTv;
        EpisodeItemClickListener episodeItemClickListener;

        FavoritedActivityHolder(View itemView, EpisodeItemClickListener episodeItemClickListener) {
            super(itemView);
            episodeTitleTv = itemView.findViewById(R.id.episode_title);
            myImageView = itemView.findViewById(R.id.episode_artwork);
            episodeDurationTv = itemView.findViewById(R.id.episode_duration);
            episodeDateTv = itemView.findViewById(R.id.episode_date);
            episodeDescriptionTv = itemView.findViewById(R.id.episode_description);
            episodeNumberTv = itemView.findViewById(R.id.episode_reference);
            seasonNumberTv = itemView.findViewById(R.id.season_reference);
            itemView.setOnClickListener(this);
            this.episodeItemClickListener = episodeItemClickListener;
        }
//Implementing and on click to the adapter
        @Override
        public void onClick(View v) {

            mOnClickListener.onEpisodeItemClickListener(getAdapterPosition());

        }
    }
}
