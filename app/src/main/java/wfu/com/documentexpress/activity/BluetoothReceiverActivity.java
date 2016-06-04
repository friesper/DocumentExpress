package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.socketoperation.AcceptScanOk;
import wfu.com.documentexpress.socketoperation.Constant;
import wfu.com.documentexpress.utils.LogUtil;
import wfu.com.documentexpress.utils.TransterServer;
import wfu.com.documentexpress.utils.TransterServerwithBluetooth;
import wfu.com.documentexpress.view.WaitDialog;
import wfu.com.documentexpress.wifioperation.WifiAdmin;

/**
 * Created by Lenovo on 2016/5/19.
 */
public class BluetoothReceiverActivity extends Activity {
    private BluetoothAdapter madapter;
    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    public static final String PROTOCOL_SCHEME_BT_OBEX ="btobex";

    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
    BluetoothAdapter bluetooth;
    final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    UUID uuid;
    BluetoothServerSocket bluetoothServerSocket;
    BluetoothSocket bluetoothSocket;
    private Thread serverWorker = new Thread(){

        public void run(){
            while(bluetoothSocket==null|!bluetoothSocket.isConnected()) {
                connected();
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    };

    private int defaultBindPort = Constant.DEFAULT_BIND_PORT;    //默认监听端口号为10000
    private int tryBindTimes = 0;           //初始的绑定端口的次数设定为0
    private ExecutorService executorService;    //线程池
    private final int POOL_SIZE = 4;//单个CPU的线程池大小

    private List<FileUpdate> transFiles;
    private static final String REC_OK = "REC_OK";
    private static final String SCAN_OK = "SCAN_OK";
    private WaitDialog waitDialog;
    private FileUpdateAdapter adapter;
    private ListView transList;
    private Button interrupt_trans;
    private TextView title;


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




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recefile);
        initView();
        madapter = BluetoothAdapter.getDefaultAdapter();
         bluetooth = BluetoothAdapter.getDefaultAdapter();
         uuid = UUID.fromString(SPP_UUID);

        if (adapter == null)
        {
            // 设备不支持蓝牙
        }
// 打开蓝牙
        if (!madapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒

            startActivityForResult(intent,30);


        }
        serverWorker.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String ssid = intent.getStringExtra("ssid");
                String password = intent.getStringExtra("password");
                String recevicePath = Environment.getExternalStorageDirectory().getPath()+"/";
                myhandler.sendEmptyMessage(0x127);
                while( connected()){
                    try {
                        new TransterServerwithBluetooth(bluetoothSocket,bluetoothServerSocket,recevicePath,transFiles,myhandler).service();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                myhandler.sendEmptyMessage(0x128);

            }
        }).start();



    }
     protected  void onActivityResult(int requestCode, int resultCode, Intent data){
         switch (requestCode){
             case  30:
                 Intent dis = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                 dis.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                 startActivity(dis);
                 break;
             default:
                 break;
         }

     }
    private boolean connected()  {

        try {
            Log.d("debug","server socket  start");
            bluetoothServerSocket = bluetooth.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bluetoothSocket = bluetoothServerSocket.accept();

            Log.d("debug","socket accept");
        } catch (IOException e) {

            e.printStackTrace();
        } if(bluetoothSocket.isConnected()){
            return true;
        }else return false;
    }
    private void initView() {
        title = (TextView) findViewById(R.id.trans_title);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        transList = (ListView) findViewById(R.id.transing_filelist);
        waitDialog = new WaitDialog(BluetoothReceiverActivity.this);
        waitDialog.setContent("正在连接...");
        transFiles = new ArrayList<FileUpdate>();
        adapter = new FileUpdateAdapter(BluetoothReceiverActivity.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);
    }

}
