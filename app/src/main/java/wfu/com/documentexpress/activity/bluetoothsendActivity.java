package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.utils.BluetoothSendService;

/**
 * Created by yinxucun on 16-6-7.
 */
public class bluetoothsendActivity extends Activity {
    private   BluetoothAdapter  mBluetoothAdapter;
    BluetoothSendService  bluetoothSendService;
    String   address;
    public ListenerThread StartListenThread;
    private LinearLayout cancleTrans;
    private ListView transList;
    private Button interrupt_trans;
    private TextView title;
    private List<FileUpdate> transFiles;
    private FileUpdateAdapter adapter;

    private String targetIp;
    private String progress = null;
    private List<String> path_list;
    private static ArrayList<String> fileList = new ArrayList<String>();
    BluetoothAdapter  madapter;
    BluetoothSocket btSocket;
    String  bluetooth_mac;
    BluetoothDevice bluetoothDevice;

    Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x126:
                    adapter.notifyDataSetChanged();
                    break;
                case 0x127:
                    title.setText("发送完成");
                    adapter.notifyDataSetChanged();
                    interrupt_trans.setText("继续发送");
                    interrupt_trans.setBackgroundColor(getResources().getColor(R.color.custom));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendfile);
        initView();
        Log.d("debug","initView");
        // Get local Bluetooth adapter
        Intent  intent=getIntent();
        address=intent.getStringExtra("mac_dress");
        path_list=(List<String>)intent.getSerializableExtra("path_list");

        if(path_list==null|path_list.size()==0){
            Log.d("debug","null");
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter==null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent intent1 = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒

            startActivityForResult(intent1, 200);
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
        bluetoothSendService = new BluetoothSendService(this,handler);
        Log.d("debug","bluetoothSendService");


            if(bluetoothSendService.getState() == BluetoothSendService.STATE_CONNECTED)
            {
                for(String name:path_list){
                    bluetoothSendService.write(name);
                }

            }

    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e("debug", "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothSendService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothSendService.getState() == BluetoothSendService.STATE_NONE) {
                // Start the Bluetooth chat services
                // Start the Bluetooth chat services
                StartListenThread = new ListenerThread();
                StartListenThread.start();

            }
        }
    }
    protected void onDestroy(){
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (bluetoothSendService != null)
            bluetoothSendService.stop();
        Log.e("debug", "--- ON DESTROY ---");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 200:
                Intent dis = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                dis.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(dis);
                break;
            default:
                break;
        }
    }
    private void connectDevice(String address ) {
        // Get the device MAC address

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothSendService.connect(device, false);
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
                    //mChatService.start();

                    if(bluetoothSendService.getState() != BluetoothSendService.STATE_CONNECTED)
                        connectDevice(address);
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
    private void initView() {
        cancleTrans = (LinearLayout) findViewById(R.id.cancle_file_filesendlayout);
        transList = (ListView) findViewById(R.id.transing_filelist);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        title = (TextView) findViewById(R.id.trans_title);
        transFiles = new ArrayList<FileUpdate>();
        adapter = new FileUpdateAdapter(bluetoothsendActivity.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);
    }

}



