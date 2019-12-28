package com.example.githubapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GitAdapter extends RecyclerView.Adapter<GitAdapter.UserHolder> {

    private List<User> userList = new ArrayList<>();
    private User user=new User();

    public void setUser(User body) {
        this.user=body;
        notifyDataSetChanged();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        private TextView textNom;
        private ImageView imageIcon;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            textNom=itemView.findViewById(R.id.nom);
            imageIcon=itemView.findViewById(R.id.icon);
        }
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items, parent, false);
        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User currentFollower=userList.get(position);
        holder.textNom.setText(currentFollower.getNom());
        Picasso.with(holder.imageIcon.getContext()).load(currentFollower.getUrl()).into(holder.imageIcon);
    }

    @Override
    //how many items it will display (size = all items)
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }


    public User getUserAt(int position){
        return userList.get(position);
    }

    public void remove(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
    }

}
