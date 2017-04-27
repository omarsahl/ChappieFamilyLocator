package os.com.chappiefamilylocator.adapters;

import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import os.com.chappiefamilylocator.models.TimelineItem;

public abstract class TimeLineAdapter extends FirebaseRecyclerAdapter<TimelineItem, TimelineItemViewHolder> {

    private RecyclerView recyclerView;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public TimeLineAdapter(Class<TimelineItem> modelClass, int modelLayout, Class<TimelineItemViewHolder> viewHolderClass, Query ref, RecyclerView recyclerView) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onChildChanged(ChangeEventListener.EventType type, int index, int oldIndex) {
        super.onChildChanged(type, index, oldIndex);
        if (type == ChangeEventListener.EventType.ADDED){
            recyclerView.smoothScrollToPosition(0);
        }
    }
}
