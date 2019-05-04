package com.example.remark.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remark.R;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.model.Player;

public class PlayerRecyclerViewAdapter extends RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_player, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = PlayerAPIClient.getInstance().get(position);

        holder.playerPictureImageView.setImageBitmap(player.picture);
        holder.playerNameTextView.setText(player.name);
    }

    @Override
    public int getItemCount() {
        return PlayerAPIClient.getInstance().getPlayers().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView playerPictureImageView;
        public TextView playerNameTextView;

        public ViewHolder(View view) {
            super(view);

            playerPictureImageView = (ImageView)view.findViewById(R.id.playerPicture);
            playerNameTextView = (TextView)view.findViewById(R.id.playerName);
        }
    }
}
