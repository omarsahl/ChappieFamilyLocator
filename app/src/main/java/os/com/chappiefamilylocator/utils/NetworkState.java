package os.com.chappiefamilylocator.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.net.ConnectivityManager.TYPE_WIFI;


public class NetworkState {

    public static int TYPE_CONNECTED = 1;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == TYPE_WIFI)
                return TYPE_CONNECTED;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_CONNECTED;
        }
        return TYPE_NOT_CONNECTED;
    }

}

