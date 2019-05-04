package com.example.remark.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.remark.R;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.DeckAPIClient;
import com.example.remark.api.PreventDoubleClickOnClickListener;
import com.example.remark.ui.lobby.HostFragment;
import com.example.remark.ui.lobby.WaitingFragment;

public class DeckRecyclerViewAdapter extends RecyclerView.Adapter<DeckRecyclerViewAdapter.ViewHolder> {
    private HostFragment mFragment;

    public DeckRecyclerViewAdapter(Fragment fragment) {
        this.mFragment = (HostFragment)fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_deck, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ViewData deck = mFragment.getDecks().get(position);

        holder.deckTitleTextView.setText(deck.title);
        holder.deckCardView.setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View view) {
                //start a Bluetooth server
                Bluetooth.getInstance().newBluetoothServer();

                DeckAPIClient.getInstance().setCurrentDeck(deck.title);

                mFragment.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main_vg_fragment, WaitingFragment.newInstance(true))
                        .commit();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mFragment.getDecks().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView deckCardView;
        public TextView deckTitleTextView;

        public ViewHolder(View view) {
            super(view);

            deckCardView = (CardView)view.findViewById(R.id.deckCardView);
            deckTitleTextView = (TextView)view.findViewById(R.id.deckTitleTextView);
        }
    }

    public class ViewData {
        public String title;

        public ViewData(String title) {
            this.title = title;
        }
    }
}
