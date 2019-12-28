package com.example.githubapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textFollowers;
    private GitHubApiHolder gitHubApiHolder;

    private int connect=0;

    TextView textViewNombre;
    TextView textViewDescription;
    TextView textViewRepos;
    ImageView imageViewIcon;
    EditText textSearch;
    GitAdapter myadapter;
    ProgressDialog progressDialog;

    public RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String nom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isNetworkConnectionAvailable();

        textFollowers= findViewById(R.id.textView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubApiHolder = retrofit.create(GitHubApiHolder.class);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textViewNombre = findViewById(R.id.nom_user);
        textViewDescription = findViewById(R.id.description_user);
        imageViewIcon = findViewById(R.id.icon_user);
        textViewRepos = findViewById(R.id.textView2);
        textSearch = findViewById(R.id.git_name);

        textViewNombre.setVisibility(View.INVISIBLE);
        textViewDescription.setVisibility(View.INVISIBLE);
        imageViewIcon.setVisibility(View.INVISIBLE);
        textFollowers.setVisibility(View.INVISIBLE);
        textViewRepos.setVisibility(View.INVISIBLE);

        //Set the adapter
        myadapter  = new GitAdapter();
        recyclerView.setAdapter(myadapter);

    }

    public void buscarButton(View view){


        textViewNombre.setVisibility(View.INVISIBLE);
        textViewDescription.setVisibility(View.INVISIBLE);
        imageViewIcon.setVisibility(View.INVISIBLE);
        textFollowers.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        textViewRepos.setVisibility(View.INVISIBLE);
        nom=textSearch.getText().toString();
        isNetworkConnectionAvailable();
        if(connect==0) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Loading GitHub User");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            getUser(nom);
            getFollowers(nom);
        }
    }

    public void checkNetworkConnection(){
        //progressDialog.hide();
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            connect=0;
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            connect=1;
            return false;
        }
    }

    private void getUser(String name) {
        Call<User> call = gitHubApiHolder.getUser(name);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    progressDialog.hide();
                    Toast.makeText(MainActivity.this, "Code: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setProgress(50);
                User user = response.body();
                setUser(user);
                }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.hide();
                String msg = "Error in retrofit: "+t.toString();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder
                        .setTitle("Error")
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}});

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUser(User user){
        textViewNombre.setText(user.getNom().toString());
        textViewDescription.setText(user.getRepositoris().toString());
        Picasso.with(this).load(user.getUrl()).into(imageViewIcon);

        textFollowers.setVisibility(View.VISIBLE);
        textViewNombre.setVisibility(View.VISIBLE);
        textViewDescription.setVisibility(View.VISIBLE);
        imageViewIcon.setVisibility(View.VISIBLE);
        textViewRepos.setVisibility(View.VISIBLE);
    }

    private void getFollowers(String name) {
        Call<List<User>> call = gitHubApiHolder.getFollowers(name);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(!response.isSuccessful()){
                    progressDialog.hide();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    alertDialogBuilder
                            .setTitle("Error")
                            .setMessage("Code: " + response.code())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}});

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    Toast.makeText(MainActivity.this, "Code: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setProgress(100);
                progressDialog.hide();
                List<User> followers = response.body();
                myadapter.setUserList(followers);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progressDialog.hide();
                String msg = "Error in retrofit: "+t.toString();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder
                        .setTitle("Error")
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}});

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
