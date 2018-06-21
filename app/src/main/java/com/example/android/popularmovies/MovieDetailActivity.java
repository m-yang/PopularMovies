package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.model.Result;
import com.example.android.popularmovies.model.ReviewInfo;
import com.example.android.popularmovies.model.ReviewResult;
import com.example.android.popularmovies.model.TrailerInfo;
import com.example.android.popularmovies.model.TrailerResult;
import com.example.android.popularmovies.rest.MovieDbEndpoint;
import com.example.android.popularmovies.rest.MovieReviewAdapter;
import com.example.android.popularmovies.rest.TrailerAdapter;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.popularmovies.rest.MovieDbEndpoint.BASE_URL;
import static com.example.android.popularmovies.rest.MovieDbEndpoint.IMAGE_BASE_URL;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener {

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
    public RecyclerView mMovieReviewsRv;

    @BindView(R.id.trailer_rv)
    public RecyclerView mTrailerRv;

    @BindView(R.id.favorite_btn)
    ImageButton favoriteButton;

    boolean starred = false;

    Retrofit mRetrofit;

    List<ReviewResult> reviewResults;
    List<TrailerResult> trailerResults;

    public MovieReviewAdapter mMovieReviewAdapter;
    public TrailerAdapter mTrailerAdapter;

    Result movieResult = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (bundle != null) {
            movieResult = bundle.getParcelable(MOVIE_RESULT_PARCELABLE_KEY);
        }

        mRetrofit = RetrofitClient.getInstance(BASE_URL);

        int id = movieResult.getId();

        setupFavoriteButton();
        populateMovieInfo();
        getReviews(id);
        getTrailers(id);

    }

    private void setupFavoriteButton() {

        if (isFavorite(movieResult.getId())) {
            star();
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!starred) {
                    star();
                    addMovieToDb();
                } else {
                    unstar();
                    int numRemoved = removeMovieFromDb(movieResult.getId());
                    Log.d(TAG, "Removed: " + numRemoved);
                }

            }
        });
    }

    private void star() {
        favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        starred = true;
    }

    private void unstar() {
        favoriteButton.setImageResource(android.R.drawable.btn_star);
        starred = false;
    }

    private void addMovieToDb() {
        String movieResultJson = new Gson().toJson(movieResult);

        ContentValues cv = new ContentValues();

        cv.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RESULT, movieResultJson);

        Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, cv);

        printDb();
    }

    private int removeMovieFromDb(int movieId) {
        Cursor cursor = queryDb();

        int numRemoved = 0;

        while (cursor.moveToNext()) {
            String json = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RESULT));
            String _id = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry._ID));

            Result result = new Gson().fromJson(json, Result.class);

            if (result.getId() == movieId) {

                Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(_id).build();
                numRemoved += getContentResolver().delete(uri, null, null);

            }
        }

        return numRemoved;
    }

    private void populateMovieInfo() {

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

                mMovieReviewAdapter = new MovieReviewAdapter(reviewResults);

                mMovieReviewsRv.setAdapter(mMovieReviewAdapter);
                mMovieReviewsRv.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this));

            }

            @Override
            public void onFailure(@NonNull Call<ReviewInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");
            }
        });
    }

    private void getTrailers(int id) {

        MovieDbEndpoint client = mRetrofit.create(MovieDbEndpoint.class);

        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);

        Call<TrailerInfo> call = client.getTrailer(id, apiKey);

        call.enqueue(new Callback<TrailerInfo>() {
            @Override
            public void onResponse(@NonNull Call<TrailerInfo> call, @NonNull Response<TrailerInfo> response) {

                TrailerInfo reviewModel = response.body();
                trailerResults = reviewModel.getResults();

                List<String> idList = new ArrayList<>();

                for(int i = 0; i < trailerResults.size(); i++) {
                    idList.add(trailerResults.get(i).getKey());
                }

                mTrailerAdapter = new TrailerAdapter(idList, MovieDetailActivity.this);

                mTrailerRv.setAdapter(mTrailerAdapter);
                mTrailerRv.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this));

            }

            @Override
            public void onFailure(@NonNull Call<TrailerInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");
            }
        });
    }

    private void launchTrailer(String trailerUrl) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerUrl));

        try {
            MovieDetailActivity.this.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't start activity");
        }
    }

    private void printDb() {

        Cursor cursor = queryDb();

        while (cursor.moveToNext()) {

            String json = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RESULT));
            String _id = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry._ID));

            Result r = new Gson().fromJson(json, Result.class);

            Log.d(TAG, r.getId() + " " + _id);

        }

    }

    private Cursor queryDb() {

        Cursor cursor = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        return cursor;

    }

    private boolean isFavorite(int movieId) {

        Cursor cursor = queryDb();

        while (cursor.moveToNext()) {
            String json = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RESULT));
            String _id = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry._ID));

            Result result = new Gson().fromJson(json, Result.class);

            if (result.getId() == movieId) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onTrailerItemClick(String trailerUrl) {
        launchTrailer(trailerUrl);
    }
}
