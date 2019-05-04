package com.example.remark.ui.game;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.remark.MainActivity;
import com.example.remark.R;
import com.example.remark.api.AnswerAPIClient;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.GameAPIClient;
import com.example.remark.api.PendingViewBehaviour;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.api.PreventDoubleClickOnClickListener;
import com.example.remark.api.ResultAPIClient;

public class VoteFragment extends Fragment {
    private LinearLayout voteAnswerLinearLayout;
    private boolean isHost;

    public static VoteFragment newInstance() {
        Bundle args = new Bundle();
        VoteFragment fragment = new VoteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote, container, false);

        voteAnswerLinearLayout = (LinearLayout)view.findViewById(R.id.voteAnswerLinearLayout);
        isHost = Bluetooth.getInstance().getServer() != null;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAnswers();
    }


    public void populateAnswers() {
        for (String address : AnswerAPIClient.getInstance().getAnswers().keySet())
            if (!address.equals("correct") && !address.equals(MainActivity.getBluetoothAddress()))
                addPendingViewFromLayoutResource(voteAnswerLinearLayout, address, AnswerAPIClient.getInstance().getAnswers().get(address));
    }

    public View addPendingViewFromLayoutResource(LinearLayout linearLayout, final String address, String answer) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_answer, linearLayout, false);

        Button answerButton = (Button)view.findViewById(R.id.view_btn_answer);
        answerButton.setAllCaps(true);
        answerButton.setText(answer);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHost) {
                    PlayerAPIClient.getInstance().get(address).addingScore += GameAPIClient.VOTE_SCORE;
                }
                else {
                    try {
                        JSONObject data = new JSONObject();
                        data.put("address", MainActivity.getBluetoothAddress());
                        data.put("vote", address);
                        Bluetooth.getInstance().getClient().send(Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_VOTE, data));
                    } catch (JSONException ignore) {
                    }
                }

                showLeaderboardFragment();
            }
        });

        linearLayout.addView(view);

        return view;
    }

    public void showLeaderboardFragment() {
        //add self
        ResultAPIClient.getInstance().getAddresses().add(MainActivity.getBluetoothAddress());

        //send to other players
        try {
            JSONObject data = new JSONObject();
            data.put("address", MainActivity.getBluetoothAddress());
            if (isHost)
                Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_VOTE, data));
            else
                Bluetooth.getInstance().getClient().send(Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_VOTE, data));
        } catch (JSONException ignore) {
        }

        VoteFragment.this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, PendingFragment.newInstance(new PendingViewBehaviour() {
                    @Override
                    public PreventDoubleClickOnClickListener getOnClickListener() {
                        return new PreventDoubleClickOnClickListener() {
                            @Override
                            public void preventDoubleClickOnClick(View v) {
                                try {
                                    JSONObject data = new JSONObject();
                                    JSONArray addingScore = new JSONArray();
                                    for (String address : PlayerAPIClient.getInstance().getPlayers().keySet()) {
                                        JSONObject player = new JSONObject();
                                        player.put("address", address);
                                        player.put("addingScore", PlayerAPIClient.getInstance().get(address).addingScore);
                                        addingScore.put(player);
                                    }
                                    data.put("addingScore", addingScore);
                                    Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_START_LEADERBOARD, data));
                                } catch (JSONException ignore) {
                                }

                                showNextFragment();
                            }
                        };
                    }

                    @Override
                    public void showNextFragment() {
                        getFragment().getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.activity_main_vg_fragment, LeaderboardFragment.newInstance())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                    }

                    @Override
                    public void populateDonePlayers() {
                        for (String address : ResultAPIClient.getInstance().getAddresses())
                            getFragment().exchangeView(PlayerAPIClient.getInstance().get(address));

                        getFragment().showNextButton();
                    }

                    @Override
                    public void done(final String address) {
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                getFragment().exchangeView(PlayerAPIClient.getInstance().get(address));

                                getFragment().showNextButton();
                            }
                        });
                    }
                }))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
