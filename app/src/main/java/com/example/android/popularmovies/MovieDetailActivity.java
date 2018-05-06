package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.popularmovies.model.Result;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_RESULT_PARCELABLE_KEY = "index";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle bundle = getIntent().getExtras();

        Result movieResult = null;

        if(bundle != null) {
            movieResult = bundle.getParcelable(MOVIE_RESULT_PARCELABLE_KEY);
        }

        Toast.makeText(this, movieResult.getOverview(), Toast.LENGTH_SHORT).show();

    }
}
