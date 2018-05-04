package com.example.android.popularmovies.rest;

import com.example.android.popularmovies.model.MovieInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDbEndpoint {

    String BASE_URL = "https://api.themoviedb.org/";

    @GET("/3/movie/popular")
    Call<MovieInfo> popularMovies(@Query("api_key") String api_key);

}
