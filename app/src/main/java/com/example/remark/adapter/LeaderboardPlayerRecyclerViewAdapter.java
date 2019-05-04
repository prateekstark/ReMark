package com.example.remark.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remark.R;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.model.Player;
import com.example.remark.ui.game.LeaderboardFragment;

public class LeaderboardPlayerRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardPlayerRecyclerViewAdapter.ViewHolder> {
    LeaderboardFragment fragment;

    public LeaderboardPlayerRecyclerViewAdapter(Fragment fragment) {
        this.fragment = (LeaderboardFragment)fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeaderboardPlayerRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_leaderboard_player, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = PlayerAPIClient.getInstance().get(position);

        holder.playerPicture.setImageBitmap(player.picture);
        holder.playerName.setText(player.name);
        holder.playerScore.setText(player.score + "");
        holder.playerAddingScore.setText("+" + player.addingScore);
        player.score += player.addingScore;
        player.addingScore = 0;
    }

    @Override
    public int getItemCount() {
        return PlayerAPIClient.getInstance().getPlayers().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView playerPicture;
        public TextView playerName;
        public TextView playerScore;
        public TextView playerAddingScore;

        public ViewHolder(View view) {
            super(view);

            playerPicture = (ImageView)view.findViewById(R.id.playerPicture);
            playerName = (TextView)view.findViewById(R.id.playerName);
            playerScore = (TextView)view.findViewById(R.id.playerScore);
            playerAddingScore = (TextView)view.findViewById(R.id.playerAddingScore);
        }
    }
}
