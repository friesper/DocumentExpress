package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.socketoperation.Constant;
import wfu.com.documentexpress.utils.TransterServer;
import wfu.com.documentexpress.utils.WIFIDirectBroadCastReceiver;
import wfu.com.documentexpress.view.WaitDialog;

/**
 * Created by yinxucun on 16-5-31.
 */
public class WiFiDirectReceiverActivity extends Activity {
    //接收文件的activity，包括接受文件的seversocket
    //给二维码显示界面反馈信息
    private int defaultBindPort = Constant.DEFAULT_BIND_PORT;    //默认监听端口号为10000
    private int tryBindTimes = 0;           //初始的绑定端口的次数设定为0
    private ServerSocket serverSocket;      //服务套接字等待对方的连接和文件发送
    private ExecutorService executorService;    //线程池
    private final int POOL_SIZE = 4;//单个CPU的线程池大小

    private List<FileUpdate> transFiles;
    private WaitDialog waitDialog;
    private FileUpdateAdapter adapter;
    private ListView transList;
    private Button interrupt_trans;
    private TextView title;

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver wifiDiretBroadcastReceiver;
    private WifiP2pInfo info;
    private  boolean wifip2pconnect=false;

    IntentFilter mIntentFilter;

    private android.os.Handler myhandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            //用于更新ui
            switch (msg.what){
                case 0x126:
                    adapter.notifyDataSetChanged();
                    break;
                case 0x127:
                    waitDialog.show();
                    break;
                case 0x128:
                    waitDialog.dismiss();
                    adapter.notifyDataSetChanged();
                    break;
                case 0x129:
                    title.setText("接收完成");
                    adapter.notifyDataSetChanged();
                    interrupt_trans.setText("我也要发");
                    interrupt_trans.setBackgroundColor(getResources().getColor(R.color.custom));
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recefile);
        initView();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        initReceiver();

        Discover();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();

                String recevicePath = Environment.getExternalStorageDirectory().getPath()+"/";
                myhandler.sendEmptyMessage(0x127);
                while(!wifip2pconnect){

                }
                myhandler.sendEmptyMessage(0x128);
                Log.d("debug","myhandler");try {
                    TransterServer wifiP2pTransterServer=new TransterServer( Constant.DEFAULT_BIND_PORT,recevicePath,transFiles,myhandler);
                    wifiP2pTransterServer.service();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.trans_title);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        transList = (ListView) findViewById(R.id.transing_filelist);
        waitDialog = new WaitDialog(WiFiDirectReceiverActivity.this);
        waitDialog.setContent("正在连接...");
        transFiles = new ArrayList<FileUpdate>();
        Log.d("debug","initView");
        adapter = new FileUpdateAdapter(WiFiDirectReceiverActivity.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);
        transFiles.clear();
    }

    private  void  initReceiver(){
        final String Mac_Dress = "65:cc2e:a2:16:dd";
        Log.d("debug","initReceiver");
        wifip2pconnect=false;

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
        BeGroupOwener();
        WifiP2pManager.PeerListListener mpeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
               // CreateConnect(Mac_Dress);
                Log.d("debug","connect+++");



            }


        };
        final WifiP2pManager.ConnectionInfoListener infoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable( final  WifiP2pInfo wifiP2pInfo) {
                Log.d("debug","infoAcailable is on");
                info=wifiP2pInfo;
                if(info.groupFormed&&info.isGroupOwner) {
                    Log.d("debug", info.groupOwnerAddress.getHostAddress());
                    wifip2pconnect=true;

                }
            }
        };



        wifiDiretBroadcastReceiver = new WIFIDirectBroadCastReceiver(wifiP2pManager, mChannel,this, mpeerListListener, infoListener);
        registerReceiver(wifiDiretBroadcastReceiver, mIntentFilter);

    }
    // 是否连接WIFI
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //IP转换
    private void BeGroupOwener() {

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


    private void CreateConnect(String address) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        wifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("debug","connect success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("debug","connect success");

            }
        });
    }


    @Override
    public void onBackPressed() {
        wifiP2pManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        unregisterReceiver(wifiDiretBroadcastReceiver);
        transFiles.clear();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        onDestroy();
    }
    private  void  Discover(){
        wifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
    Log.d("debug","DIscover");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

}

