package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Result;
import com.example.android.popularmovies.model.ReviewInfo;
import com.example.android.popularmovies.model.ReviewResult;
import com.example.android.popularmovies.rest.MovieDbEndpoint;
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

        int id = movieResult.getId();

        Log.d(TAG, "id: " + id);


        Retrofit mRetrofit = RetrofitClient.getInstance(BASE_URL);

        MovieDbEndpoint client = mRetrofit.create(MovieDbEndpoint.class);

        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);

        Call<ReviewInfo> call = client.getReviews(id, apiKey);

        call.enqueue(new Callback<ReviewInfo>() {
            @Override
            public void onResponse(@NonNull Call<ReviewInfo> call, @NonNull Response<ReviewInfo> response) {

                Log.d(TAG, "response code: " + response.code());

                ReviewInfo reviewModel = response.body();

                List<ReviewResult> results = reviewModel.getResults();

            }

            @Override
            public void onFailure(@NonNull Call<ReviewInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");
            }
        });


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
}
