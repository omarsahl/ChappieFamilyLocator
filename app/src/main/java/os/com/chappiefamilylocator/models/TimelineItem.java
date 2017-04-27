package os.com.chappiefamilylocator.models;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimelineItem {

    private FamilyMember familyMember;
    String date;
    Long timestamp;

    public TimelineItem() {
    }

    public TimelineItem(FamilyMember familyMember) {
        this.familyMember = familyMember;
        Calendar c = Calendar.getInstance();
        // Apr 21, 2017 at 1:17pm
        SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy 'at' h:ma", Locale.getDefault());
        date = df.format(c.getTime());
        // negative to allow firebase to order i descending order
        timestamp = -1 * System.currentTimeMillis();
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
