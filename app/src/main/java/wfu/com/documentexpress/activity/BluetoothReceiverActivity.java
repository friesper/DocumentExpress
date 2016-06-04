package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import wfu.com.documentexpress.R;

/**
 * Created by Lenovo on 2016/5/19.
 */
public class BluetoothReceiverActivity extends Activity {
    private BluetoothAdapter adapter;
    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    public static final String PROTOCOL_SCHEME_BT_OBEX ="btobex";

    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
    BluetoothAdapter bluetooth;
    final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    UUID uuid;
    BluetoothServerSocket serverSocket;
    BluetoothSocket socket;
    private Thread serverWorker = new Thread(){

        public void run(){
            while(socket==null|!socket.isConnected()) {
                connected();
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    };




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_receiver);
        adapter = BluetoothAdapter.getDefaultAdapter();
         bluetooth = BluetoothAdapter.getDefaultAdapter();
         uuid = UUID.fromString(SPP_UUID);

        if (adapter == null)
        {
            // 设备不支持蓝牙
        }
// 打开蓝牙
        if (!adapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒

            startActivityForResult(intent,30);


        }
        serverWorker.start();



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
    private void connected()  {

        try {
            Log.d("debug","server socket  start");
            serverSocket = bluetooth.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket = serverSocket.accept();
            Log.d("debug","socket accept");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
