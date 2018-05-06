package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.rest.MovieDbEndpoint.IMAGE_BASE_URL;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder> {

    private static final String TAG = MoviePosterAdapter.class.getName();

    final private PosterClickListener mOnClickListener;

    private int mNumberItems;

    private List<Result> results;

    private Context context;

    public interface PosterClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviePosterAdapter(List<Result> results, Context context) {
        PosterClickListener listener = (PosterClickListener) context;

        this.results = results;
        this.context = context;
        this.mNumberItems = results.size();
        this.mOnClickListener = listener;
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

        String imageURL = IMAGE_BASE_URL + "w185" + results.get(position).getPosterPath();

        Log.d(TAG, imageURL);

        Picasso.with(context).setLoggingEnabled(true);

        Picasso.with(context)
                .load(imageURL)
                .into(holder.posterImageView);
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
