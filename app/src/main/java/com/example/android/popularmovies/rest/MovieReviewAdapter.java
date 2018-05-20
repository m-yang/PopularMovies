package com.example.android.popularmovies.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.ReviewResult;

import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {

    private final String TAG = getClass().getName();

    List<ReviewResult> reviewResults;

    public MovieReviewAdapter(List<ReviewResult> reviewResults) {
        this.reviewResults = reviewResults;

        Log.d(TAG, reviewResults.size() + "");
    }

    @NonNull
    @Override
    public MovieReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.review_item, parent, false);

        return new MovieReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewAdapter.ReviewViewHolder holder, int position) {

        String reviewContent =  reviewResults.get(position).getContent();

        holder.reviewTextView.setText(reviewContent);

    }

    @Override
    public int getItemCount() {
        return reviewResults.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView reviewTextView;

        ReviewViewHolder(View itemView) {
            super(itemView);

            reviewTextView = itemView.findViewById(R.id.review_content_tv);

        }
    }
}
