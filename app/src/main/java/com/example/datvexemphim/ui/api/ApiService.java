package com.example.datvexemphim.ui.api;

import com.example.datvexemphim.ui.model.Cinema;
import com.example.datvexemphim.ui.model.filmAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://rapchieuphim.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("api/v1/movies")
    Call<List<filmAPI>> selectApiFilm();

    @GET("api/v1/cinemas")
    Call<List<Cinema>> getAllCinemas();

    @GET("api/v1/cinemas")
    Call<List<Cinema>> getCinemas(@Query("location") String location);
}

