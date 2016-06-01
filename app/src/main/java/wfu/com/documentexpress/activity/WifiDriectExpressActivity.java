package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    WifiP2pManager.PeerListListener  peerListListener;
    public   boolean    flag;
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
       new Thread(new Runnable() {
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
        }).start();


        Discover();

    }

    private void ExpressFile() {

        if(info.groupOwnerAddress.getHostAddress()!=null) {
            Intent serviceIntent = getIntent();
            serviceIntent.putExtra("targetIp",
                    info.groupOwnerAddress.getHostAddress());
            Log.d("debug","no IP");
            //serviceIntent.putExtra("port",
            //         8988);
            serviceIntent.setAction("SEND_FILE");
            startActivity(serviceIntent);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiDiretBroadcastReceiver);
    }
    private void BeGroupOwener() {
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
        wifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("debug","createGroupSuccess");

            }

            @Override
            public void onFailure(int reason) {
                Log.d("debug","create Group failed");
            }
        });
    }
    private  void  initReceiver(){
        final String Mac_Dress = "28:e3:1f:a6:1e:59";

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wifiP2pManager.initialize(this, Looper.myLooper(), null);
        WifiP2pManager.PeerListListener mpeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                BeGroupOwener();
                CreateConnect(Mac_Dress);
                Log.d("debug","connect+++");
                NetworkInfo  networkInfo=getIntent().getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            /*    if(networkInfo.isConnected())
                if(info.groupFormed&&info.isGroupOwner) {
                    Log.d("debug", info.groupOwnerAddress.getHostAddress());
                }*/
               // ExpressFile();

            }


        };
        final WifiP2pManager.ConnectionInfoListener infoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable( final  WifiP2pInfo wifiP2pInfo) {
                Log.d("debug","infoAcailable is on");
               // info=wifiP2pInfo;
            }
        };



        wifiDiretBroadcastReceiver = new WIFIDirectBroadCastReceiver(wifiP2pManager, mChannel,this, mpeerListListener, infoListener);
        registerReceiver(wifiDiretBroadcastReceiver, mIntentFilter);
    }
    private boolean CreateConnect(String address) {
        WifiP2pConfig config = new WifiP2pConfig();
        Log.i("xyz", address);

        config.deviceAddress = address;
        /*mac地址*/

        //config.wps.setup = WpsInfo.PBC;
        Log.i("debug", "MAC IS " + address);
        if (address.equals("9a:ff:d0:23:85:97")) {
            config.groupOwnerIntent = 0;
            Log.i("address", "lingyige shisun");
        }
        if (address.equals("36:80:b3:e8:69:a6")) {
            config.groupOwnerIntent = 15;
            Log.i("address", "lingyigeshiwo");

        }

        Log.i("debug", "lingyige youxianji" + String.valueOf(config.groupOwnerIntent));

        wifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                flag=true;
            }

            @Override
            public void onFailure(int reason) {
                flag=false;

            }
        });
    return flag;
    }
    private  void  Discover(){
        wifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }
}
