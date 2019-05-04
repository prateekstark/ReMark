package com.example.remark.ui.lobby;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.example.remark.R;
import com.example.remark.adapter.HostRecyclerViewAdapter;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.PlayerAPIClient;

public class JoinFragment extends Fragment {
    private ArrayList<HostRecyclerViewAdapter.ViewData> bluetooths;
    private BroadcastReceiver receiver;
    private HostRecyclerViewAdapter adapter;

    public static JoinFragment newInstance() {
        Bundle args = new Bundle();
        JoinFragment fragment = new JoinFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join, container, false);

        configDiscovery();

        //init self information
        PlayerAPIClient.getInstance().addSelf(getResources());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Recycler View
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.hostRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adapter
        adapter = new HostRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        //Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //Swipe Refresh Layout
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.hostSwipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.argb(90, 102, 204, 255));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        stopDiscovery();
                        configDiscovery();
                        startDiscovery();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        startDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopDiscovery();
    }

    private void configDiscovery() {
        bluetooths = new ArrayList<>();

        //Bluetooth discovery receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String name = device.getName();
                    if (name == null)
                        name = "Unknown";
                    String address = device.getAddress();
                    bluetooths.add(adapter.new ViewData(name, address));

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(bluetooths.size() - 1);
                        }
                    });
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private void startDiscovery() {
        Bluetooth.getInstance().startDiscovery();
    }

    private void stopDiscovery() {
        Bluetooth.getInstance().stopDiscovery();
        getActivity().unregisterReceiver(receiver);
    }

    public ArrayList<HostRecyclerViewAdapter.ViewData> getBluetooths() {
        return bluetooths;
    }
}
