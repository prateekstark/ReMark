package com.example.remark.api;
import android.os.Handler;
import java.io.Serializable;
import com.example.remark.ui.game.PendingFragment;


abstract public class PendingViewBehaviour implements Serializable {
    private PendingFragment fragment;
    private Handler handler;

    public PendingViewBehaviour() {
        handler = new Handler();
    }

    abstract public PreventDoubleClickOnClickListener getOnClickListener();

    abstract public void showNextFragment();

    abstract public void populateDonePlayers();

    abstract public void done(final String address);

    public void setFragment(PendingFragment fragment) {
        this.fragment = fragment;
    }

    public PendingFragment getFragment() {
        return fragment;
    }

    public Handler getHandler() {
        return handler;
    }
}
