package os.com.chappiefamilylocator.models;



public class FamilyMember {

    private String firstName;
    private String lastName;
    private String uid;
    private String email;
    private String imgUrl;
    private LocationModel realTimeLocation;
    private String status;

    public FamilyMember() {}

    public FamilyMember(String uid, String firstName, String lastName, String email, String imgUrl) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imgUrl = imgUrl;
        realTimeLocation = null;
        status = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LocationModel getRealTimeLocation() {
        return realTimeLocation;
    }

    public void setRealTimeLocation(LocationModel realTimeLocation) {
        this.realTimeLocation = realTimeLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
