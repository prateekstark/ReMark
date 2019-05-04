package com.example.remark.ui.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.remark.GameActivity;
import com.example.remark.R;
import com.example.remark.adapter.LeaderboardPlayerRecyclerViewAdapter;
import com.example.remark.api.AnswerAPIClient;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.DeckAPIClient;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.api.PreventDoubleClickOnClickListener;
import com.example.remark.api.ResultAPIClient;


public class LeaderboardFragment extends Fragment {
    private static FragmentTask fragmentTask;
    private static LeaderboardPlayerRecyclerViewAdapter adapter;
    private boolean isHost;

    public static FragmentTask getFragmentTask() {
        return fragmentTask;
    }

    public static LeaderboardPlayerRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public static LeaderboardFragment newInstance() {
        Bundle args = new Bundle();
        LeaderboardFragment fragment = new LeaderboardFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        fragmentTask = new FragmentTask(this);
        isHost = Bluetooth.getInstance().getServer() != null;

        PlayerAPIClient.getInstance().sort();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Recycler View
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adapter
        adapter = new LeaderboardPlayerRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        if (!isHost) {
            view.findViewById(R.id.nextRound).setVisibility(View.GONE);
            view.findViewById(R.id.endGame).setVisibility(View.GONE);
        }

        if (isHost && !DeckAPIClient.getInstance().isRemaining())
            view.findViewById(R.id.nextRound).setVisibility(View.GONE);

        view.findViewById(R.id.nextRound).setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View view) {
                nextRound();
                try {
                    Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_START_NEXT_ROUND, new JSONObject()));
                } catch (JSONException ignore) {
                }
            }
        });

        view.findViewById(R.id.endGame).setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View view) {
                GameActivity.getActivityTask().endGame();
                try {
                    Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_END_GAME, new JSONObject()));
                } catch (JSONException ignore) {
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fragmentTask = null;
    }

    public void nextRound() {
        showQuestionFragment();

        AnswerAPIClient.getInstance().getAnswers().clear();
        ResultAPIClient.getInstance().getAddresses().clear();
    }

    public void showQuestionFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, QuestionFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public static class FragmentTask {
        private LeaderboardFragment fragment;

        FragmentTask(Fragment fragment) {
            this.fragment = (LeaderboardFragment)fragment;
        }

        public void nextRound() {
            fragment.nextRound();
        }
    }
}
