package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.popularmovies.model.MovieInfo;
import com.example.android.popularmovies.model.Result;
import com.example.android.popularmovies.rest.MovieDbEndpoint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.popularmovies.MovieDetailActivity.MOVIE_RESULT_PARCELABLE_KEY;
import static com.example.android.popularmovies.rest.MovieDbEndpoint.BASE_URL;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.PosterClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int SORT_POPULAR = 0;
    private static final int SORT_TOP_RATED = 1;

    @BindView(R.id.movies_rv)
    public RecyclerView mPosterGrid;

    private MoviePosterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mPosterGrid.setLayoutManager(gridLayoutManager);

        displayMovies(SORT_POPULAR);
    }

    private void displayMovies(long rowId) {

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        MovieDbEndpoint client = retrofit.create(MovieDbEndpoint.class);

        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);


        Call<MovieInfo> call = null;

        if (rowId == SORT_POPULAR) {
            call = client.popularMovies(apiKey);
        } else if (rowId == SORT_TOP_RATED) {
            call = client.topRatedMovies(apiKey);
        }

        call.enqueue(new Callback<MovieInfo>() {
            @Override
            public void onResponse(Call<MovieInfo> call, Response<MovieInfo> response) {

                MovieInfo movieModel = response.body();

                List<Result> results = movieModel.getResults();

                mAdapter = new MoviePosterAdapter(results, MainActivity.this);
                mPosterGrid.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<MovieInfo> call, Throwable t) {
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
}
