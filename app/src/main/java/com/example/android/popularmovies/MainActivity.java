package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        mAdapter = new MoviePosterAdapter(10, this);
        mPosterGrid.setAdapter(mAdapter);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Toast.makeText(this, "Movie Poster Clicked!", Toast.LENGTH_SHORT).show();

    }
}
