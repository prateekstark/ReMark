package com.example.remark.ui.game;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.HashMap;

import com.example.remark.R;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.PendingViewBehaviour;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.model.Player;


public class PendingFragment extends Fragment {
    private static FragmentTask fragmentTask;
    private HashMap<Player, View> waitingPlayers;
    private LinearLayout doneLinearLayout;
    private LinearLayout waitingLinearLayout;
    private MaterialRippleLayout nextButton;
    private PendingViewBehaviour pendingViewBehaviour;
    private boolean isHost;

    public static FragmentTask getFragmentTask() {
        return fragmentTask;
    }

    public static PendingFragment newInstance(PendingViewBehaviour pendingViewBehaviour) {
        Bundle args = new Bundle();
        PendingFragment fragment = new PendingFragment();
        pendingViewBehaviour.setFragment(fragment);
        args.putSerializable("pendingViewBehaviour", pendingViewBehaviour);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending, container, false);

        fragmentTask = new FragmentTask(this);
        waitingPlayers = new HashMap<>();
        pendingViewBehaviour = (PendingViewBehaviour)getArguments().getSerializable("pendingViewBehaviour");
        isHost = Bluetooth.getInstance().getServer() != null;

        doneLinearLayout = (LinearLayout)rootView.findViewById(R.id.doneLinearLayout);
        waitingLinearLayout = (LinearLayout)rootView.findViewById(R.id.waitingLinearLayout);
        nextButton = (MaterialRippleLayout)rootView.findViewById(R.id.nextButton);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //animate
        waitingLinearLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        animate(waitingLinearLayout);

        //set button is only visible to host
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(pendingViewBehaviour.getOnClickListener());

        //generate waiting players list
        populateWaitingPlayers();

        //generate done players list
        pendingViewBehaviour.populateDonePlayers();
    }

    private void animate(View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.layout_expanding);

        view.setVisibility(LinearLayout.VISIBLE);
        view.setAnimation(animation);
        view.animate();

        animation.setDuration(500);
        animation.start();
    }

    private void populateWaitingPlayers() {
        for (Player player : PlayerAPIClient.getInstance().getPlayers().values())
            waitingPlayers.put(player, addView(waitingLinearLayout, player));
    }

    public void showNextButton() {
        if (isHost && waitingPlayers.size() == 0)
            nextButton.setVisibility(View.VISIBLE);
    }

    public void exchangeView(Player player) {
        waitingLinearLayout.removeView(waitingPlayers.get(player));
        waitingPlayers.remove(player);
        addView(doneLinearLayout, player);
    }

    private View addView(LinearLayout linearLayout, Player player) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_player, linearLayout, false);

        ((TextView)rootView.findViewById(R.id.playerName)).setText(player.name);
        ((ImageView)rootView.findViewById(R.id.playerPicture)).setImageBitmap(player.picture);

        linearLayout.addView(rootView);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fragmentTask = null;
    }

    public static class FragmentTask {
        private PendingFragment fragment;

        FragmentTask(Fragment fragment) {
            this.fragment = (PendingFragment)fragment;
        }

        public void done(String address) {
            fragment.pendingViewBehaviour.done(address);
        }

        public void showNextFragment() {
            fragment.pendingViewBehaviour.showNextFragment();
        }
    }
}
