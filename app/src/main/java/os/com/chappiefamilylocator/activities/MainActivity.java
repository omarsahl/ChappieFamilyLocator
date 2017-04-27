package os.com.chappiefamilylocator.activities;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import os.com.chappiefamilylocator.LocationTrackingService;
import os.com.chappiefamilylocator.R;
import os.com.chappiefamilylocator.databinding.ActivityMainBinding;
import os.com.chappiefamilylocator.databinding.ContentMainBinding;
import os.com.chappiefamilylocator.fragments.AccountFragment;
import os.com.chappiefamilylocator.fragments.FamilyFragment;
import os.com.chappiefamilylocator.fragments.TimelineFragment;
import os.com.chappiefamilylocator.models.FamilyMember;
import os.com.chappiefamilylocator.utils.FirebaseUtils;
import pub.devrel.easypermissions.EasyPermissions;

import static os.com.chappiefamilylocator.utils.FirebaseUtils.getCurrentUserFamilyRef;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks,
        AccountFragment.OnSignOutListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_REQUEST_CODE = 10047;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;

    // UI
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private ActivityMainBinding mainBinding;
    private ContentMainBinding contentMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        contentMainBinding = mainBinding.contentMain;

        initUi();
        initGoogleApiClient();
        initFirebase();
        checkUserInDatabase();

        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            startService(new Intent(this, LocationTrackingService.class));
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location), LOCATION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.main_menu_settings:
                Snackbar.make(toolbar, "Coming soon.", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: granted");
        startService(new Intent(this, LocationTrackingService.class));
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    //Init methods
    public void initUi() {
        toolbar = mainBinding.toolbar;
        toolbar.setLogo(R.drawable.chappie_logo_text_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView = contentMainBinding.navigation;


        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment timelineFragment = TimelineFragment.newInstance();
        final Fragment familyFragment = FamilyFragment.newInstance();
        final Fragment accountFragment = AccountFragment.newInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_timeline:
                        Log.d(TAG, "onNavigationItemSelected: timeline");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, timelineFragment).commit();
                        return true;

                    case R.id.navigation_family:
                        Log.d(TAG, "onNavigationItemSelected: family");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, familyFragment).commit();
                        return true;
                    case R.id.navigation_profile:
                        Log.d(TAG, "onNavigationItemSelected: profile");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
                        return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_timeline);
    }

    private void initGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkUserInDatabase() {
        FirebaseUtils.getFamilyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.getUid())) {
                    Log.e(TAG, "User " + user.getDisplayName() + " doesn't exist");
                    Log.e(TAG, "Adding " + user.getDisplayName() + "'s info to the family database...");

                    String displayName = user.getDisplayName();
                    String[] name = displayName.split(" ");
                    String email = user.getEmail();
                    String imgUrl = user.getPhotoUrl().toString();

                    FamilyMember familyMember = new FamilyMember(user.getUid(), name[0], name[1], email, imgUrl);
                    getCurrentUserFamilyRef().setValue(familyMember);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignOut() {
        signOut();
    }
}
