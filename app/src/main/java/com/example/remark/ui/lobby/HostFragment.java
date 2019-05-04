package com.example.remark.ui.lobby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.example.remark.R;
import com.example.remark.adapter.DeckRecyclerViewAdapter;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.PlayerAPIClient;

public class HostFragment extends Fragment {
    private ArrayList<DeckRecyclerViewAdapter.ViewData> decks;
    private DeckRecyclerViewAdapter adapter;

    public static HostFragment newInstance() {
        Bundle args = new Bundle();
        HostFragment fragment = new HostFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host, container, false);

        decks = new ArrayList<>();

        //set bluetooth discoverable
        Bluetooth.getInstance().setDiscoverable(getActivity());

        //init self information
        PlayerAPIClient.getInstance().addSelf(getResources());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Recycler View
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.deckRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adapter
        adapter = new DeckRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        initDecks();
    }

    private void initDecks() {
        decks.add(adapter.new ViewData("Word Up!"));
        decks.add(adapter.new ViewData("Is That A Fact?"));
        decks.add(adapter.new ViewData("Movie Bluff!"));
        decks.add(adapter.new ViewData("It's The Law"));
        decks.add(adapter.new ViewData("The Plot Thickens"));
        decks.add(adapter.new ViewData("Name That Show!"));
        decks.add(adapter.new ViewData("Poetry"));
        decks.add(adapter.new ViewData("Say My Name"));
        decks.add(adapter.new ViewData("Proverbs"));
        decks.add(adapter.new ViewData("Adults Only"));
        decks.add(adapter.new ViewData("Animals"));
        decks.add(adapter.new ViewData("Customize Deck"));
    }

    public ArrayList<DeckRecyclerViewAdapter.ViewData> getDecks() {
        return decks;
    }
}
