package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.utils.TransterClient;
import wfu.com.documentexpress.utils.WIFIDirectBroadCastReceiver;


/**
 * Created by sion on 2016/5/25.
 */
public class WifiDirectExpressActivity extends Activity {
    private  WifiP2pManager  wifiP2pManager;
    private  WifiP2pManager.Channel mChannel;
    private WifiP2pInfo info;
    TransterClient wifiP2pTransterClient;

    private  List<String>  file_list;
    private  List<FileUpdate>  files_list;

    IntentFilter mIntentFilter;
    WIFIDirectBroadCastReceiver wifiDiretBroadcastReceiver=  null;

    private LinearLayout cancleTrans;
    private ListView transList;
    private Button interrupt_trans;
    private TextView title;
    private List<FileUpdate> transFiles;
    private FileUpdateAdapter adapter;



    public   boolean    flag=false;
    private String  MAc_Dress;

    private android.os.Handler myhandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            //用于更新ui
            switch (msg.what){
                case 0x12:
                    adapter.notifyDataSetChanged();
                    break;
                case   0x13:
                    title.setText("发送完成");
                    adapter.notifyDataSetChanged();
                    interrupt_trans.setText("继续发送");
                    interrupt_trans.setBackgroundColor(getResources().getColor(R.color.custom));
                    break;
                case  0x14:
                    title.setText("发送失败");
                    adapter.notifyDataSetChanged();
                    interrupt_trans.setText("继续发送");


            }

        }
    };
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendfile);

        Log.d("debug", "WIFIDIR");
        //Intent intent = getIntent();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
       // String  Mac_Dress=intent.getStringExtra("MacDress");

        initView();
        initEvent();
        initReceiver();
         Discover();

    }

    private void initView() {
        cancleTrans = (LinearLayout) findViewById(R.id.cancle_file_filesendlayout);
        transList = (ListView) findViewById(R.id.transing_filelist);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        title = (TextView) findViewById(R.id.trans_title);
        transFiles = new ArrayList<FileUpdate>();
        adapter = new FileUpdateAdapter(WifiDirectExpressActivity.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);


    }

    private void ExpressFile() {


       boolean   transFile= wifiP2pTransterClient.service();

        if(transFile)myhandler.sendEmptyMessage(0x13);
        else   myhandler.sendEmptyMessage(0x14);



    }
    public void  onBackPressed(){
        removeWIFIP2p();

        unregisterReceiver(wifiDiretBroadcastReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeWIFIP2p();
        unregisterReceiver(wifiDiretBroadcastReceiver);

    }
    private  void initEvent(){
        Intent  intent=getIntent();
        files_list = new ArrayList<FileUpdate>();
        file_list=(List<String>)intent.getSerializableExtra("path_list");

        wifiP2pTransterClient=new TransterClient(file_list,transFiles,myhandler);
    }
    private  void  initReceiver(){
        final String Mac_Dress = "66:cc:2e:a2:16:dd";
        MAc_Dress=getIntent().getStringExtra("mac_dress");
       //final String Mac_Dress="2a:e3:1f:a6:1e:59";


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
                    CreateConnect(Mac_Dress);
                myhandler.sendEmptyMessage(0x13);
                    Log.d("debug", "connect+++");
            }
        };
        final WifiP2pManager.ConnectionInfoListener infoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable( final  WifiP2pInfo wifiP2pInfo) {
                Log.d("debug","infoAcailable is on");
                    flag=true;
                info=wifiP2pInfo;
               // if(info!=null&info.groupOwnerAddress.getHostAddress().equals("192.168.49.1")){
                    myhandler.sendEmptyMessage(0x12);
                    ExpressFile();
               // }
                }

        };
        wifiDiretBroadcastReceiver = new WIFIDirectBroadCastReceiver(wifiP2pManager, mChannel,this, mpeerListListener, infoListener);
        registerReceiver(wifiDiretBroadcastReceiver, mIntentFilter);
    }
    private void CreateConnect(String address) {
        WifiP2pConfig config = new WifiP2pConfig();
        Log.i("debug", address);
        config.deviceAddress = address;
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
    private   void   removeWIFIP2p(){
        wifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("debug","  stopDiscovery success");

            }

            @Override
            public void onFailure(int i) {
                Log.d("debug","  stopDiscovery  failed");
            }
        });
        wifiP2pManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("debug","  cledarLocalServices success");
            }

            @Override
            public void onFailure(int i) {
                Log.d("debug","clear LocalServices  failed");

            }
        });
        wifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

    }
}
