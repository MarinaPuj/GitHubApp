package com.example.githubapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubApiHolder {
    @GET("users/{gitName}/followers")   //relative URL
    Call<List<User>> getFollowers(@Path("gitName")String gitName);

    @GET("users/{gitName}")
    Call<User> getUser(@Path("gitName")String gitName);
}
