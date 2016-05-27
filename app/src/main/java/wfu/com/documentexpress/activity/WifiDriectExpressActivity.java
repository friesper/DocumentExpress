package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import java.net.InetAddress;

import wfu.com.documentexpress.R;

/**
 * Created by sion on 2016/5/25.
 */
public class WifiDriectExpressActivity extends Activity {
    private  WifiP2pManager  wifiP2pManager;
   private  WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private InetAddress address;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_directer);
        Intent intent=getIntent();
        String  Mac_Dress=intent.getStringExtra("MacDress");
        wifiP2pManager=(WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=wifiP2pManager.initialize(this,getMainLooper(),null);
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress=Mac_Dress;
        wifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        wifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                 address = wifiP2pInfo.groupOwnerAddress;
                //socket communication
            }
        });

    }
}
