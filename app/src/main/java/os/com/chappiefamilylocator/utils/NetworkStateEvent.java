package os.com.chappiefamilylocator.utils;


public class NetworkStateEvent {

    private int networkState;

    public NetworkStateEvent(int networkState) {
        this.networkState = networkState;
    }

    public int getNetworkState() {
        return networkState;
    }

    public void setNetworkState(int networkState) {
        this.networkState = networkState;
    }
}
