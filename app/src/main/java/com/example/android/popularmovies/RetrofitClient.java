package com.example.android.popularmovies;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient retrofitClient = null;
    private static Retrofit retrofit = null;

    private RetrofitClient(String baseUrl) {

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static Retrofit getInstance(String baseUrl) {

        if(retrofitClient == null) {
            retrofitClient = new RetrofitClient(baseUrl);
        }
        return retrofit;

    }

}
