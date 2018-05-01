package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movies_rv)
    private RecyclerView mPosterGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mPosterGrid.setLayoutManager(gridLayoutManager);


    }
}
