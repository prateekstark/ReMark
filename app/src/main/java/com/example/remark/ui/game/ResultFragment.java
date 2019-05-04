package com.example.remark.ui.game;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.balysv.materialripple.MaterialRippleLayout;

import com.example.remark.R;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.api.PreventDoubleClickOnClickListener;

public class ResultFragment extends Fragment {
    private MaterialRippleLayout resultButton;
    private ImageView resultImageView;

    private String address;

    public static ResultFragment newInstance(String address) {
        Bundle args = new Bundle();
        ResultFragment fragment = new ResultFragment();
        args.putString("address", address);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        address = getArguments().getString("address");

        resultButton = (MaterialRippleLayout) view.findViewById(R.id.resultButton);
        resultImageView = (ImageView) view.findViewById(R.id.resultImageView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Animation
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in_image);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resultButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Image View
        if (address.equals("correct"))
            resultImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.congratulation));
        else
            resultImageView.setImageBitmap(PlayerAPIClient.getInstance().get(address).picture);
        resultImageView.startAnimation(animation);

        //Button
        resultButton.setVisibility(View.GONE);
        resultButton.setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View view) {
                showVoteFragment();
            }
        });
    }

    public void showVoteFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, VoteFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
