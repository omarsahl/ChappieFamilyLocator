package os.com.chappiefamilylocator.fragments;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import os.com.chappiefamilylocator.R;
import os.com.chappiefamilylocator.adapters.TimeLineAdapter;
import os.com.chappiefamilylocator.adapters.TimelineItemViewHolder;
import os.com.chappiefamilylocator.models.FamilyMember;
import os.com.chappiefamilylocator.models.TimelineItem;
import os.com.chappiefamilylocator.receivers.NetworkStateReceiver;
import os.com.chappiefamilylocator.utils.FirebaseUtils;
import os.com.chappiefamilylocator.utils.NetworkStateEvent;

public class TimelineFragment extends Fragment {

    private static final String TAG = TimelineFragment.class.getSimpleName();

    private FloatingActionButton fab;
    private RecyclerView timeLineRecyclerView;
    private TimeLineAdapter adapter;
    private DatabaseReference timelineRef;

    private View positiveAction;
    private TextInputLayout inputLayout;
    private Snackbar snackbar;

    private NetworkStateReceiver receiver;

    public TimelineFragment() {
        // Required empty public constructor
    }


    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new NetworkStateReceiver();
        timelineRef = FirebaseUtils.getTimelineRef();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_timeline, container, false);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        timeLineRecyclerView = (RecyclerView) root.findViewById(R.id.time_line_recycler_view);


//        adapter = new FirebaseRecyclerAdapter<TimelineItem, TimelineItemViewHolder>(TimelineItem.class,
//                R.layout.time_line_item, TimelineItemViewHolder.class, timelineRef.orderByChild("timestamp")) {
//
//            @Override
//            protected void populateViewHolder(TimelineItemViewHolder viewHolder, TimelineItem model, int position) {
//                viewHolder.bindView(model, getActivity());
//            }
//
//        };

        adapter = new TimeLineAdapter(TimelineItem.class, R.layout.time_line_item, TimelineItemViewHolder.class,
                timelineRef.orderByChild("timestamp"), timeLineRecyclerView) {
            @Override
            protected void populateViewHolder(TimelineItemViewHolder viewHolder, TimelineItem model, int position) {
                viewHolder.bindView(model, getActivity());
            }
        };


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        timeLineRecyclerView.setLayoutManager(layoutManager);
        timeLineRecyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.status_dialog_title)
                        .customView(R.layout.status_dialog_view, true)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                updateStatus(inputLayout.getEditText().getText().toString());
                            }
                        }).build();

                positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                inputLayout = (TextInputLayout) dialog.getCustomView().findViewById(R.id.dialog_input);
                inputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                dialog.show();
                positiveAction.setEnabled(false);
            }
        });


        return root;
    }

    private void updateStatus(final String status) {
        FirebaseUtils.getCurrentUserFamilyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final FamilyMember familyMember = dataSnapshot.getValue(FamilyMember.class);

                if (familyMember != null) {
                    familyMember.setStatus(status);
                    FirebaseUtils.getCurrentUserFamilyRef().child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                commitStatusUpdate(familyMember);
                            else
                                Toast.makeText(getActivity(), "Error updating status", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Error updating status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void commitStatusUpdate(FamilyMember familyMember) {
        FirebaseUtils.getTimelineRef().push().setValue(new TimelineItem(familyMember)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(fab, "Status updated", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(fab, "Status update failed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetworkStateEvent event) {
        Log.d(TAG, "onEvent: heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeey");
        // your implementation
        int state = event.getNetworkState();
        switch (state) {
            case 0:
                if (snackbar != null && snackbar.isShown()){
                    snackbar.dismiss();
                }
                snackbar = Snackbar.make(fab, getString(R.string.waiting_network), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                break;
            case 1:
                if (snackbar != null && snackbar.isShown()){
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
