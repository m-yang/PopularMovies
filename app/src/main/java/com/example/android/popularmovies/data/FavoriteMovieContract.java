package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

public class FavoriteMovieContract {

    final static class FavoriteMovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_NAME = "movieName";
    }
}
