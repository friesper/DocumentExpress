package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.Mac;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.utils.WIFIDirectBroadCastReceiver;

import static java.lang.Thread.*;

/**
 * Created by sion on 2016/5/25.
 */
public class WifiDriectExpressActivity extends Activity {
    private  WifiP2pManager  wifiP2pManager;
    private  WifiP2pManager.Channel mChannel;
    private WifiP2pInfo info;
    IntentFilter mIntentFilter;
    WIFIDirectBroadCastReceiver wifiDiretBroadcastReceiver=  null;
    public   boolean    flag=false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_directer);

        Log.d("debug", "WIFIDIR");
        //Intent intent = getIntent();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
       // String  Mac_Dress=intent.getStringExtra("MacDress");
         initReceiver();
       /*new Thread(new Runnable() {
            @Override
            public void run() {
                Discover();
                try {
                        for(;;){
                        Discover();
                        sleep(300);
                        Log.d("debug", "Thread+Discover");
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/


        Discover();

    }

    private void ExpressFile() {


            Intent serviceIntent = getIntent();

            serviceIntent.setClass(this,WifiDirectSendActivity.class);
            startActivity(serviceIntent);
            this.onPause();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiDiretBroadcastReceiver);
    }
    private  void  initReceiver(){
        final String Mac_Dress = "66:cc:2e:a2:16:dd";

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wifiP2pManager.initialize(this, Looper.myLooper(), null);
        wifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("debug","remove group success");
            }

            @Override
            public void onFailure(int i) {
                Log.d("debug","remove group failed");

            }
        });
        WifiP2pManager.PeerListListener mpeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                for(int  i=3;i>0;i++) {
                    CreateConnect(Mac_Dress);
                    Log.d("debug", "connect+++");
                }


            }


        };
        final WifiP2pManager.ConnectionInfoListener infoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable( final  WifiP2pInfo wifiP2pInfo) {
                Log.d("debug","infoAcailable is on");
                flag=false;

                    ExpressFile();
                }

        };



        wifiDiretBroadcastReceiver = new WIFIDirectBroadCastReceiver(wifiP2pManager, mChannel,this, mpeerListListener, infoListener);
        registerReceiver(wifiDiretBroadcastReceiver, mIntentFilter);
    }
    private void CreateConnect(String address) {
        WifiP2pConfig config = new WifiP2pConfig();
        Log.i("debug", address);

        config.deviceAddress = address;
        /*mac地址*/



        Log.i("debug", "lingyige youxianji" + String.valueOf(config.groupOwnerIntent));

        wifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("debug","connect success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("debug","connect failed");

            }
        });
    }
    private  void  Discover(){
        wifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            Log.d("debug","Discover success");
            }

            @Override
            public void onFailure(int i) {
                Log.d("debug","Discover failed");

            }
        });
    }
}
