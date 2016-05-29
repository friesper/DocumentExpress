package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wfu.com.documentexpress.R;

/**
 * Created by sion on 2016/5/25.
 */
public class WifiDriectExpressActivity extends Activity {
    private  WifiP2pManager  wifiP2pManager;
   private  WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private InetAddress address;
    private List<String> path_list;
    private   WifiP2pDevice  mdevices;
    private String connectedIp=null;
    Collection<WifiP2pDevice>  collection;
    IntentFilter mIntentFilter;
    WIFIDIretBroadcastReceiver  wifidIretBroadcastReceiver;
    WifiP2pManager.PeerListListener  peerListListener;
    public   boolean    flag=false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_directer);
        wifiP2pManager=(WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=wifiP2pManager.initialize(this,getMainLooper(),null);
        Log.d("debug","WIFIDIR");
        Intent intent=getIntent();
        ListView  listView=(ListView) findViewById(R.id.devices_list);
        listView.setAdapter(new ArrayAdapter<WifiP2pDevice>(this,R.layout.abc_action_bar_title_item,(ArrayList)collection));
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //String  Mac_Dress=intent.getStringExtra("MacDress");
        wifidIretBroadcastReceiver=new WIFIDIretBroadcastReceiver(wifiP2pManager,mChannel,this);
        final String  Mac_Dress="28:e3:1f:a6:1e:59";

        peerListListener=new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        collection=wifiP2pDeviceList.getDeviceList();
                        for(WifiP2pDevice device:collection){


                                Log.d("debug",device.toString());
                        }


            }
        };
        registerReceiver(wifidIretBroadcastReceiver,mIntentFilter);
        wifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("debug","sucess");
            }

            @Override
            public void onFailure(int i) {
        Log.d("debug","failed" );
            }
        });

        final WifiP2pConfig config = new WifiP2pConfig();

           /* config.deviceAddress = mdevices.deviceAddress;

            wifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("debug", "success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("debug", "failed");
                }
            });
            wifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                    address = wifiP2pInfo.groupOwnerAddress;
                    //socket communication
                    if (address != null) {
                        Log.d("debug", address.toString());
                    }
                }
            });*/


    }
   /* private static String getMacFromWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String mResult = wifiInfo.getMacAddress();
        Log.i("debug","Mac address(wifi): "+mResult);
        return mResult;
    }*/
    class   WIFIDIretBroadcastReceiver extends BroadcastReceiver{/*

    WIFIP２P链接监听器

    */

        private WifiP2pManager  manager;
        private WifiP2pManager.Channel   channel;
        private   WifiDriectExpressActivity  activity;

        public   WIFIDIretBroadcastReceiver(WifiP2pManager  manager, WifiP2pManager.Channel  channel,WifiDriectExpressActivity activity){
            super();
            this.activity=activity;
            this.manager=manager;
            this.channel=channel;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String  action=intent.getAction();

            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (manager != null) {
                    manager.requestPeers(channel, peerListListener);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifidIretBroadcastReceiver);
    }
}
