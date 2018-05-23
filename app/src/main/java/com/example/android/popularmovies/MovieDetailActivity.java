package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Result;
import com.example.android.popularmovies.model.ReviewInfo;
import com.example.android.popularmovies.model.ReviewResult;
import com.example.android.popularmovies.model.TrailerInfo;
import com.example.android.popularmovies.model.TrailerResult;
import com.example.android.popularmovies.rest.MovieDbEndpoint;
import com.example.android.popularmovies.rest.MovieReviewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.popularmovies.rest.MovieDbEndpoint.BASE_URL;
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

    @BindView(R.id.movie_review_rv)
    public RecyclerView mMovieReviews;

    @BindView(R.id.play_trailer_iv)
    ImageView playTrailerImageView;

    Retrofit mRetrofit;

    List<ReviewResult> reviewResults;
    List<TrailerResult> trailerResults;

    public MovieReviewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Result movieResult = null;
        if (bundle != null) {
            movieResult = bundle.getParcelable(MOVIE_RESULT_PARCELABLE_KEY);
        }

        mRetrofit = RetrofitClient.getInstance(BASE_URL);

        int id = movieResult.getId();

        populateMovieInfo(movieResult);
        getReviews(id);
        getTrailer(id);

    }

    private void populateMovieInfo(Result movieResult) {

        String imageURL = IMAGE_BASE_URL + "w185" + movieResult.getPosterPath();
        Picasso.with(this)
                .load(imageURL)
                .into(posterImageView);

        titleTextView.setText(movieResult.getTitle());

        String releaseDate = getResources().getString(R.string.detail_released) + ": " + movieResult.getReleaseDate();
        releaseDateTextView.setText(releaseDate);

        String rating = getResources().getString(R.string.detail_rating) + ": " + movieResult.getVoteAverage();

        ratingTextView.setText(rating);

        synopsisTextView.setText(movieResult.getOverview());

    }

    private void getReviews(int id) {

        MovieDbEndpoint client = mRetrofit.create(MovieDbEndpoint.class);

        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);

        Call<ReviewInfo> call = client.getReviews(id, apiKey);

        call.enqueue(new Callback<ReviewInfo>() {
            @Override
            public void onResponse(@NonNull Call<ReviewInfo> call, @NonNull Response<ReviewInfo> response) {

                ReviewInfo reviewModel = response.body();
                reviewResults = reviewModel.getResults();

                mAdapter = new MovieReviewAdapter(reviewResults);

                mMovieReviews.setAdapter(mAdapter);
                mMovieReviews.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this));

            }

            @Override
            public void onFailure(@NonNull Call<ReviewInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");
            }
        });
    }

    private void getTrailer(int id) {

        MovieDbEndpoint client = mRetrofit.create(MovieDbEndpoint.class);

        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);

        Call<TrailerInfo> call = client.getTrailer(id, apiKey);

        call.enqueue(new Callback<TrailerInfo>() {
            @Override
            public void onResponse(@NonNull Call<TrailerInfo> call, @NonNull Response<TrailerInfo> response) {

                TrailerInfo reviewModel = response.body();
                trailerResults = reviewModel.getResults();

                final String id = trailerResults.get(0).getKey();

                playTrailerImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchTrailer(id);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<TrailerInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");
            }
        });
    }

    private void launchTrailer(String trailerID) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerID));

        try {
            MovieDetailActivity.this.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't start activity");
        }
    }
}
