package com.example.remark.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.remark.GameActivity;
import com.example.remark.R;
import com.example.remark.adapter.PlayerRecyclerViewAdapter;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.api.PreventDoubleClickOnClickListener;

public class WaitingFragment extends Fragment {
    private static PlayerRecyclerViewAdapter adapter;
    private static FragmentTask fragmentTask;
    private boolean isHost;
    private boolean isStartGame;

    public static WaitingFragment newInstance(boolean isHost) {
        Bundle args = new Bundle();
        WaitingFragment fragment = new WaitingFragment();
        args.putBoolean("isHost", isHost);
        fragment.setArguments(args);

        return fragment;
    }

    public static PlayerRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public static FragmentTask getFragmentTask() {
        return fragmentTask;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiting, container, false);

        fragmentTask = new FragmentTask(this);
        isHost = getArguments().getBoolean("isHost");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Recycler View
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.playerRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adapter
        adapter = new PlayerRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        view.findViewById(R.id.startGame).setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View view) {
                //at least two players
                if (PlayerAPIClient.getInstance().getPlayers().size() == 1) {
                    Toast.makeText(getContext(), getString(R.string.requires_at_least_two_players), Toast.LENGTH_SHORT).show();
                    return;
                }

                //send to other players game started
                try {
                    Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_START_GAME, new JSONObject()));
                } catch (JSONException ignore) {
                }

                startGame();
            }
        });

        if (!isHost)
            view.findViewById(R.id.startGame).setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!isStartGame) {
            Bluetooth.getInstance().closeClient();
            Bluetooth.getInstance().closeServer();
        }

        //clear player list
        PlayerAPIClient.getInstance().clearPlayer();
    }

    public void startGame() {
        isStartGame = true;

        Intent intent = new Intent(getActivity(), GameActivity.class);
        startActivity(intent);
    }

    public static class FragmentTask {
        private WaitingFragment fragment;

        FragmentTask(Fragment fragment) {
            this.fragment = (WaitingFragment)fragment;
        }

        public void startGame() {
            fragment.startGame();
        }
    }
}
