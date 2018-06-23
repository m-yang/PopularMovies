package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
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

    private static String KEY_INSTANCE_STATE = "key-instance-state";
    private static String KEY_SCROLL_STATE = "key-scroll-state";

    private static long rowId = 0;

    private static boolean restoreState = false;

    ArrayList<Result> resultList;

    @BindView(R.id.movies_rv)
    public RecyclerView mPosterGrid;

    public MoviePosterAdapter mAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {

            restoreState = true;
            Parcelable state = savedInstanceState.getParcelable(KEY_SCROLL_STATE);

            resultList = savedInstanceState.getParcelableArrayList(KEY_INSTANCE_STATE);

            gridLayoutManager = new GridLayoutManager(this, numColumns());
            gridLayoutManager.onRestoreInstanceState(state);

            mPosterGrid.setLayoutManager(gridLayoutManager);
        } else {

            gridLayoutManager = new GridLayoutManager(this, numColumns());
            mPosterGrid.setLayoutManager(gridLayoutManager);
            displayMovies();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (resultList == null) {
            displayMovies();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_INSTANCE_STATE, resultList);
        outState.putParcelable(KEY_SCROLL_STATE, mPosterGrid.getLayoutManager().onSaveInstanceState());


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(rowId == SORT_FAVORITES) {
            displayMovies();
        }

    }

    private int numColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 500;
        int width = displayMetrics.widthPixels;
        int columns = width / widthDivider;

        return (columns < 2) ? 2 : columns;
    }


    private void displayMovies() {

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
            Log.d(TAG, "sort fav");

            Cursor cursor = queryDb();

            resultList = new ArrayList<>();

            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RESULT));

                Result result = new Gson().fromJson(json, Result.class);

                resultList.add(result);
            }

            mAdapter = new MoviePosterAdapter(resultList, MainActivity.this);
            mPosterGrid.setAdapter(mAdapter);
            return;
        }

        Objects.requireNonNull(call).enqueue(new Callback<MovieInfo>() {
            @Override
            public void onResponse(@NonNull Call<MovieInfo> call, @NonNull Response<MovieInfo> response) {

                MovieInfo movieModel = response.body();

                resultList = new ArrayList<>(movieModel.getResults());

                mAdapter = new MoviePosterAdapter(resultList, MainActivity.this);
                mPosterGrid.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<MovieInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "failure");

                resultList = new ArrayList<Result>();
                mAdapter = new MoviePosterAdapter(resultList, MainActivity.this);
                mPosterGrid.setAdapter(mAdapter);
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

        spinner.setSelection((int) rowId);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                rowId = id;

                if(restoreState) {
                    if(resultList != null) {
                        mAdapter = new MoviePosterAdapter(resultList, MainActivity.this);
                        mPosterGrid.setAdapter(mAdapter);
                    }
                    restoreState = false;
                } else {
                    displayMovies();
                }

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
