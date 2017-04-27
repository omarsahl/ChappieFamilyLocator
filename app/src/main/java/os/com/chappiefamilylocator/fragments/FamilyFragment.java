package os.com.chappiefamilylocator.fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import os.com.chappiefamilylocator.R;
import os.com.chappiefamilylocator.activities.MapsActivity;
import os.com.chappiefamilylocator.adapters.FamilyLocationItemViewHolder;
import os.com.chappiefamilylocator.models.FamilyMember;
import os.com.chappiefamilylocator.receivers.NetworkStateReceiver;
import os.com.chappiefamilylocator.utils.Constants;
import os.com.chappiefamilylocator.utils.FirebaseUtils;
import os.com.chappiefamilylocator.utils.NetworkStateEvent;

public class FamilyFragment extends Fragment {

    private final static String TAG = FamilyFragment.class.getSimpleName();

    private RecyclerView familyLocationRecyclerView;
    private FirebaseRecyclerAdapter<FamilyMember, FamilyLocationItemViewHolder> adapter;
    private DatabaseReference familyRef;
    private Snackbar snackbar;

    private NetworkStateReceiver receiver;

    public FamilyFragment() {
        // Required empty public constructor
    }

    public static FamilyFragment newInstance() {
        FamilyFragment fragment = new FamilyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        receiver = new NetworkStateReceiver();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_family, container, false);
        familyLocationRecyclerView = (RecyclerView) root.findViewById(R.id.family_location_recycler_view);

        familyRef = FirebaseUtils.getFamilyRef();

        adapter = new FirebaseRecyclerAdapter<FamilyMember, FamilyLocationItemViewHolder>(
                FamilyMember.class, R.layout.family_location_item, FamilyLocationItemViewHolder.class, familyRef
        ) {

            @Override
            protected void populateViewHolder(FamilyLocationItemViewHolder viewHolder, FamilyMember model, int position) {
                viewHolder.bindView(model, getActivity());
            }
        };

        familyLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        familyLocationRecyclerView.setAdapter(adapter);
        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetworkStateEvent event) {
        Log.d(TAG, "onEvent: heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeey");
        // your implementation
        int state = event.getNetworkState();
        switch (state) {
            case 0:
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                snackbar = Snackbar.make(familyLocationRecyclerView, getString(R.string.waiting_network), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                break;
            case 1:
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }
}
