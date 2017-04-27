package os.com.chappiefamilylocator.utils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getFamilyRef() {
        return FirebaseUtils.getBaseRef().child(Constants.FAMILY_NODE_KEY);
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }


    public static DatabaseReference getCurrentUserFamilyRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child(Constants.FAMILY_NODE_KEY).child(uid);
        }
        return null;
    }

    public static DatabaseReference getTimelineRef(){
        return FirebaseUtils.getBaseRef().child(Constants.TIMELINE_NODE_KEY);
    }
}
