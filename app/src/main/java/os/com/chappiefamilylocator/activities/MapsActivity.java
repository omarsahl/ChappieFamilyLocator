package os.com.chappiefamilylocator.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import os.com.chappiefamilylocator.R;
import os.com.chappiefamilylocator.models.FamilyMember;
import os.com.chappiefamilylocator.models.LocationModel;
import os.com.chappiefamilylocator.utils.Constants;
import os.com.chappiefamilylocator.utils.FirebaseUtils;

import static os.com.chappiefamilylocator.R.id.fab;
import static os.com.chappiefamilylocator.R.id.family_location_recycler_view;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private boolean isToolbarHidden;

    private DatabaseReference reference;
    private String familyMemberUid;
    private FamilyMember familyMember;
    private BitmapDescriptor marker;

    private FloatingActionMenu menu;
    private FloatingActionButton zoomIn;
    private FloatingActionButton zoomOut;
    private int zoomLevel;
    private CameraUpdate camera;
    private LatLng location;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        isToolbarHidden = false;
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        menu = (FloatingActionMenu) findViewById(R.id.maps_fab_menu);
        zoomIn = (FloatingActionButton) findViewById(R.id.menu_item_zoom_in);
        zoomOut = (FloatingActionButton) findViewById(R.id.menu_item_zoom_out);

        familyMemberUid = getIntent().getStringExtra(Constants.FAMILY_MEMBER_UID_STRING_EXTRA);
        reference = FirebaseUtils.getFamilyRef();
        getCurrentFamilyMember(familyMemberUid);
        marker = getBitmapDescriptor(R.drawable.marker);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        zoomLevel = 18;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_marker_info_window, null);
                TextView name = (TextView) v.findViewById(R.id.info_window_name);
                if (familyMember != null) {
                    String memberName = familyMember.getFirstName() + "'s Location";
                    name.setText(memberName);
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isToolbarHidden) {
                    ObjectAnimator ABLTranslationOut = ObjectAnimator.ofFloat(appBarLayout, "translationY", 0f, -toolbar.getBottom());
                    ObjectAnimator FABAlphaOut = ObjectAnimator.ofFloat(menu, "alpha", 1.0f, 0.0f);

                    AnimatorSet outAnimatorSet = new AnimatorSet();
                    outAnimatorSet.playTogether(ABLTranslationOut);
                    outAnimatorSet.setInterpolator(new DecelerateInterpolator());
                    outAnimatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            menu.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    outAnimatorSet.start();
                    isToolbarHidden = true;
                } else {
                    ObjectAnimator ABLTranslationIn = ObjectAnimator.ofFloat(appBarLayout, "translationY", -toolbar.getBottom(), 0f);
                    ObjectAnimator FABAlphaIn = ObjectAnimator.ofFloat(fab, "alpha", 0.0f, 1.0f);

                    AnimatorSet inAnimatorSet = new AnimatorSet();
                    inAnimatorSet.playTogether(ABLTranslationIn, FABAlphaIn);
                    inAnimatorSet.setInterpolator(new DecelerateInterpolator());
                    inAnimatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            menu.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    inAnimatorSet.start();
                    isToolbarHidden = false;
                }
            }
        });

        reference.child(familyMemberUid).child(Constants.LOCATIONS_NODE_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);

                if (locationModel != null) {
                    Log.e(TAG, "Lat: " + locationModel.getLat());
                    Log.e(TAG, "Lon: " + locationModel.getLon());

                    mMap.clear();

                    location = new LatLng(locationModel.getLat(), locationModel.getLon());
                    mMap.addMarker(new MarkerOptions().position(location).icon(marker)).showInfoWindow();
                    // TODO: 26-Apr-17 implement zoom level control.
                    camera = CameraUpdateFactory.newLatLngZoom(location, zoomLevel);
                    mMap.animateCamera(camera);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Database Error: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomLevel++;
                camera = CameraUpdateFactory.newLatLngZoom(location, zoomLevel);
                mMap.animateCamera(camera);
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomLevel--;
                camera = CameraUpdateFactory.newLatLngZoom(location, zoomLevel);
                mMap.animateCamera(camera);
            }
        });

    }

    public void getCurrentFamilyMember(String uid) {
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyMember = dataSnapshot.getValue(FamilyMember.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Context context = MapsActivity.this;
        Bitmap marker = getBitmapFromVectorDrawable(context, id);
        return BitmapDescriptorFactory.fromBitmap(marker);
    }
}
