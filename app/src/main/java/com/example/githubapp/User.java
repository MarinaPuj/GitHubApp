package com.example.githubapp;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("login")
    private  String nom;

    @SerializedName("public_repos")
    private String repositoris;

    @SerializedName("avatar_url")
    @Expose
    private String url;


    public String getNom() {
        return nom;
    }

    public String getRepositoris() {
        return repositoris;
    }

  //  public String getFollowers() {
    //    return followers; }

    public String getUrl(){return url;}
}