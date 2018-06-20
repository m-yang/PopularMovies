package com.example.android.popularmovies.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final String TAG = getClass().getName();

    final private TrailerClickListener mOnClickListener;

    List<String> idList;

    public TrailerAdapter(List<String> idList, Context context) {
        TrailerClickListener listener = (TrailerClickListener) context;
        this.mOnClickListener = listener;
        this.idList = idList;
    }

    public interface TrailerClickListener {
        void onTrailerItemClick(String trailerUrl);
    }

    @NonNull
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.trailer_item, parent, false);

        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerViewHolder holder, int position) {

        holder.trailerNumber.setText("Trailer: " + position);
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final TextView trailerNumber;

        TrailerViewHolder(View itemView) {
            super(itemView);
            trailerNumber = itemView.findViewById(R.id.trailer_num_tv);
            trailerNumber.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String trailerUrl = idList.get(clickedPosition);
            mOnClickListener.onTrailerItemClick(trailerUrl);
        }
    }
}
