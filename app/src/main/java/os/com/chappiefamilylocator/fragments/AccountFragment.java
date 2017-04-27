package os.com.chappiefamilylocator.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import os.com.chappiefamilylocator.R;
import os.com.chappiefamilylocator.models.FamilyMember;
import os.com.chappiefamilylocator.utils.FirebaseUtils;

public class AccountFragment extends Fragment {

    private CircleImageView familyMemberImage;
    private TextView familyMemberName;
    private TextView familyMemberEmail;
    private Button signOutButton;

    private FamilyMember familyMember;

    private OnSignOutListener listener;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnSignOutListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        signOutButton = (Button) root.findViewById(R.id.sign_out_btn);
        familyMemberImage = (CircleImageView) root.findViewById(R.id.family_member_img_account);
        familyMemberName = (TextView) root.findViewById(R.id.family_member_name_account);
        familyMemberEmail = (TextView) root.findViewById(R.id.family_member_email_account);

        FirebaseUtils.getCurrentUserFamilyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyMember = dataSnapshot.getValue(FamilyMember.class);
                updateUi();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignOut();
            }
        });


        return root;
    }

    private void updateUi() {
        Picasso.with(getActivity()).load(Uri.parse(familyMember.getImgUrl())).placeholder(R.drawable.account_image_placeholder).into(familyMemberImage);
        familyMemberName.setText(familyMember.getFirstName() + " " + familyMember.getLastName());
        familyMemberEmail.setText(familyMember.getEmail());

    }

    public interface OnSignOutListener {
        public void onSignOut();
    }

}
