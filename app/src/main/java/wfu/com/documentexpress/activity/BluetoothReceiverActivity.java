package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.socketoperation.Constant;
import wfu.com.documentexpress.utils.BluetoothTrancerService;
import wfu.com.documentexpress.view.WaitDialog;

/**
 * Created by Lenovo on 2016/5/19.
 */
public class BluetoothReceiverActivity extends Activity {
    private BluetoothAdapter madapter;
    private BluetoothDevice bluetoothDevice;
    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    public static final String PROTOCOL_SCHEME_BT_OBEX ="btobex";

    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
    final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    UUID uuid;
    BluetoothServerSocket bluetoothServerSocket=null;
    BluetoothSocket bluetoothSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private  BluetoothTrancerService bluetoothTrancerService=null;
    public ListenerThread StartListenThread;


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
    String     recevicePath = Environment.getExternalStorageDirectory().getPath()+"/";


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
        uuid = UUID.fromString(SPP_UUID);
        myhandler.sendEmptyMessage(0x127);
        if (madapter == null)
        {
            // 设备不支持蓝牙
            Log.d("debug","madapter=null");
        }
// 打开蓝牙
        else if (!madapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒

            startActivityForResult(intent,30);



        }else {
            Intent diss = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            diss.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(diss);
        }




    }
     protected  void onActivityResult(int requestCode, int resultCode, Intent data){
         switch (requestCode){
             case  30:

                 break;
             default:
                 break;
         }

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
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e("debug", "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothTrancerService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothTrancerService.getState() == BluetoothTrancerService.STATE_NONE)
            {
                // Start the Bluetooth chat services
                StartListenThread = new ListenerThread();
                StartListenThread.start();


            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("debug", "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult

        // Initialize the BluetoothChatService to perform bluetooth connections
        if(null == bluetoothTrancerService)
            bluetoothTrancerService =new BluetoothTrancerService(this,myhandler,transFiles,recevicePath);
    }
    private class ListenerThread extends Thread {
        // The local server socket

        public void run()
        {
            // Listen to the server socket if we're not connected
            for(int i =0;i<100;i++)
            {
                if(mBluetoothAdapter.getState()== BluetoothAdapter.STATE_ON)
                {
                    bluetoothTrancerService.start();
                    break;
                }
                else
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }

        }

    }
}
