package os.com.chappiefamilylocator.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Omar on 22-Jan-17
 */

public class LocationModel {

    private double lon;
    private double lat;
    private String date;

    public LocationModel() {}

    public LocationModel(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        Calendar c = Calendar.getInstance();
        // Apr 21, 2017 at 1:17pm
        SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy 'at' h:ma", Locale.getDefault());
        date = df.format(c.getTime());
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
