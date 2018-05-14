package com.example.android.popularmovies.rest;

import com.example.android.popularmovies.model.MovieInfo;
import com.example.android.popularmovies.model.ReviewInfo;
import com.example.android.popularmovies.model.TrailerInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbEndpoint {

    String BASE_URL = "https://api.themoviedb.org/";
    String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    @GET("/3/movie/{sort}")
    Call<MovieInfo> getMovies(@Path("sort") String sort, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/reviews")
    Call<ReviewInfo> getReviews(@Path("id") int id, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/videos")
    Call<TrailerInfo> getTrailer(@Path("id") int id, @Query("api_key") String api_key);

}
