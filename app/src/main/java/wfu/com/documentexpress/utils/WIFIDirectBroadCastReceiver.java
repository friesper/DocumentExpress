package wfu.com.documentexpress.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yinxucun on 16-5-30.
 */
public class WIFIDirectBroadCastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private WifiP2pManager.ConnectionInfoListener mInfoListener;
    public   boolean  flag=false;

    public WIFIDirectBroadCastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Activity activity,
                                       WifiP2pManager.PeerListListener peerListListener,
                                       WifiP2pManager.ConnectionInfoListener infoListener
    ) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mPeerListListener = peerListListener;
        this.mActivity = activity;
        this.mInfoListener = infoListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        /*check if the wifi is enable*/
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {
                Toast.makeText(mActivity,"weikaiqi",Toast.LENGTH_LONG);
            }
        }
        /*get the list*/
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            mManager.requestPeers(mChannel, mPeerListListener);
        }
        /*查看当前是否处于查找状态
        * get the state of discover*/
        else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

            int State = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);

            if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                Toast.makeText(mActivity, "搜索开启", Toast.LENGTH_SHORT).show();
                mManager.discoverPeers(mChannel, (WifiP2pManager.ActionListener) mPeerListListener);
            }
            else if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED)
                Toast.makeText(mActivity, "搜索已关闭", Toast.LENGTH_SHORT).show();

        }
        /*Respond to new connection or disconnections
        *查看是否创建连接*/
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                flag=true;
                Log.i("debug", "已连接");
                mManager.requestConnectionInfo(mChannel, mInfoListener);
            } else {
                Log.i("debug", "断开连接");
                return;
            }
        }

        /*Respond to this device's wifi state changing*/
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }

    }
    public  boolean isConnect(){
        return flag;
    }
}
