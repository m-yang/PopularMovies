package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.model.MovieInfo;
import com.example.android.popularmovies.model.Result;
import com.example.android.popularmovies.rest.MovieDbEndpoint;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.popularmovies.MovieDetailActivity.MOVIE_RESULT_PARCELABLE_KEY;
import static com.example.android.popularmovies.rest.MovieDbEndpoint.BASE_URL;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.PosterClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int SORT_POPULAR = 0;
    private static final int SORT_TOP_RATED = 1;
    private static final int SORT_FAVORITES = 2;

    @BindView(R.id.movies_rv)
    public RecyclerView mPosterGrid;

    public MoviePosterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumns());
        mPosterGrid.setLayoutManager(gridLayoutManager);

        displayMovies(SORT_POPULAR);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private int numColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 500;
        int width = displayMetrics.widthPixels;
        int columns = width / widthDivider;

        return (columns < 2) ? 2 : columns;
    }


    private void displayMovies(long rowId) {

        Retrofit mRetrofit = RetrofitClient.getInstance(BASE_URL);

        MovieDbEndpoint client = mRetrofit.create(MovieDbEndpoint.class);

        Call<MovieInfo> call = null;

        // TODO: Insert API key here
        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);

        if (rowId == SORT_POPULAR) {
            call = client.getMovies(getResources().getString(R.string.sort_popular), apiKey);
        } else if (rowId == SORT_TOP_RATED) {
            call = client.getMovies(getResources().getString(R.string.sort_top_rated), apiKey);
        } else if (rowId == SORT_FAVORITES) {

            Cursor cursor = queryDb();

            List<Result> results = new ArrayList<Result>();

            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RESULT));

                Result result = new Gson().fromJson(json, Result.class);

                results.add(result);
            }

            mAdapter = new MoviePosterAdapter(results, MainActivity.this);
            mPosterGrid.setAdapter(mAdapter);
            return;
        }

        Objects.requireNonNull(call).enqueue(new Callback<MovieInfo>() {
            @Override
            public void onResponse(@NonNull Call<MovieInfo> call, @NonNull Response<MovieInfo> response) {

                MovieInfo movieModel = response.body();

                List<Result> results = Objects.requireNonNull(movieModel).getResults();

                mAdapter = new MoviePosterAdapter(results, MainActivity.this);
                mPosterGrid.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<MovieInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sort_movie_spinner, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_array, R.layout.spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long rowId) {
                displayMovies(rowId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

    @Override
    public void onListItemClick(Result movieResult) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_RESULT_PARCELABLE_KEY, movieResult);

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    private Cursor queryDb() {

        Cursor cursor = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        return cursor;

    }
}
