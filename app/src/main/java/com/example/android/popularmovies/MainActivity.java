package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

import static com.example.android.popularmovies.rest.MovieDbEndpoint.BASE_URL;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.PosterClickListener {

    private static final String TAG = MainActivity.class.getName();

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

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();


        MovieDbEndpoint client = retrofit.create(MovieDbEndpoint.class);

        String apiKey = getResources().getString(R.string.MOVIE_DB_API_KEY);

        Call<MovieInfo> call = client.popularMovies(apiKey);
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
    public void onListItemClick(int clickedItemIndex) {

        launchMovieDetailActivity(clickedItemIndex);

        Log.d(TAG, "index " + clickedItemIndex);

    }

    private void launchMovieDetailActivity(int index) {

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.INDEX, index);
        startActivity(intent);
    }

}
