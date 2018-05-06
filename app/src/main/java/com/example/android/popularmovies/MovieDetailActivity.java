package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Result;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.rest.MovieDbEndpoint.IMAGE_BASE_URL;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getName();

    public static final String MOVIE_RESULT_PARCELABLE_KEY = "index";

    @BindView(R.id.detail_poster_iv)
    public ImageView posterImageView;

    @BindView(R.id.title_tv)
    public TextView titleTextView;

    @BindView(R.id.release_date_tv)
    public TextView releaseDateTextView;

    @BindView(R.id.rating_tv)
    public TextView ratingTextView;

    @BindView(R.id.synopsis_tv)
    public TextView synopsisTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        Result movieResult = null;
        if (bundle != null) {
            movieResult = bundle.getParcelable(MOVIE_RESULT_PARCELABLE_KEY);
        }

        Log.d(TAG, movieResult.getOverview());

        String imageURL = IMAGE_BASE_URL + "w185" + movieResult.getPosterPath();
        Picasso.with(this)
                .load(imageURL)
                .into(posterImageView);

        titleTextView.setText(movieResult.getTitle());

        releaseDateTextView.setText(getResources().getString(R.string.detail_released) +  ": " + movieResult.getReleaseDate());

        ratingTextView.setText(getResources().getString(R.string.detail_rating) +  ": " + movieResult.getVoteAverage());

        synopsisTextView.setText(movieResult.getOverview());

    }
}
