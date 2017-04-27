package os.com.chappiefamilylocator.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import os.com.chappiefamilylocator.R;
import os.com.chappiefamilylocator.activities.MapsActivity;
import os.com.chappiefamilylocator.models.FamilyMember;
import os.com.chappiefamilylocator.utils.Constants;

public class FamilyLocationItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private final static String TAG = FamilyLocationItemViewHolder.class.getSimpleName();

    private final CircleImageView familyMemberImg;
    private final TextView familyMemberName;
    private final ImageButton viewOnMapBtn;

    private Context context;
    private FamilyMember familyMember;

    public FamilyLocationItemViewHolder(View itemView) {
        super(itemView);
        familyMemberImg = (CircleImageView) itemView.findViewById(R.id.family_member_img);
        familyMemberName = (TextView) itemView.findViewById(R.id.family_member_name);
        viewOnMapBtn = (ImageButton) itemView.findViewById(R.id.view_on_map_btn);
        viewOnMapBtn.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void bindView(FamilyMember familyMember, Context context) {
        this.context = context;
        this.familyMember = familyMember;
        Picasso.with(context).load(Uri.parse(familyMember.getImgUrl())).into(familyMemberImg);
        familyMemberName.setText(familyMember.getFirstName() + " " + familyMember.getLastName());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(Constants.FAMILY_MEMBER_UID_STRING_EXTRA, familyMember.getUid());
        context.startActivity(intent);
    }
}
