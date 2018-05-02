package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder> {

    private static final String TAG = MoviePosterAdapter.class.getName();

    final private PosterClickListener mOnClickListener;

    private int mNumberItems;

    public interface PosterClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviePosterAdapter(int numberOfItems, PosterClickListener listener) {
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.poster_grid_item, parent, false);

        PosterViewHolder viewHolder = new PosterViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MoviePosterAdapter.PosterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class PosterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView posterImageView;

        public PosterViewHolder(View itemView) {

            super(itemView);

            posterImageView = (ImageView) itemView.findViewById(R.id.poster_iv);
            posterImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
