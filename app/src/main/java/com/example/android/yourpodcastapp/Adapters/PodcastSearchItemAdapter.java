package com.example.android.yourpodcastapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.yourpodcastapp.PodcastSearchItem;
import com.example.android.yourpodcastapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Szymon on 07.02.2019.
 */
    public class PodcastSearchItemAdapter extends RecyclerView.Adapter<PodcastSearchItemAdapter.PodcastSearchItemViewHolder> {

        private static final String TAG = PodcastSearchItemAdapter.class.getSimpleName();

        final private GridViewClickListener mOnClickListener;

        private PodcastSearchItem item;

        private ArrayList<PodcastSearchItem> searchItems;

        private Context mContext;

//Constructor for the class, the way details are passed to the constructor

        public PodcastSearchItemAdapter(ArrayList<PodcastSearchItem> items, Context context, GridViewClickListener listener) {

            searchItems = items;
            mContext = context;
            mOnClickListener = listener;
        }

//    On click interface to be implemented in the activity

        public interface GridViewClickListener {
            void onGridItemClick(int clickedItemIndex);
        }

        //    Inflate the singe views to be used for the app
        @Override
        public PodcastSearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.search_grid_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;
            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            return new PodcastSearchItemViewHolder(view);
        }

        //   Bind data to the inflated views
        @Override
        public void onBindViewHolder(PodcastSearchItemViewHolder holder, int position) {


            Log.v("Test",String.valueOf(searchItems.get(position).getArtwork()));
            Picasso.with(mContext).load(searchItems.get(position).getArtwork()).fit().into(holder.myImageView);
            holder.myImageView.setContentDescription(searchItems.get(position).getName());

        }


        @Override
        public int getItemCount() {
            int a ;

            if(searchItems != null && !searchItems.isEmpty()) {

                a = searchItems.size();
            }
            else {

                a = 0;

            }

            return a;
        }

        // stores and recycles views as they are scrolled off screen

        public class PodcastSearchItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            ImageView myImageView;

            PodcastSearchItemViewHolder(View itemView) {
                super(itemView);
                myImageView = itemView.findViewById(R.id.searchGridImageItem);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int clickedPosition = getAdapterPosition();
                mOnClickListener.onGridItemClick(clickedPosition);
            }
        }

        public PodcastSearchItem getItem(int id) {
            return searchItems.get(id);

        }

}
