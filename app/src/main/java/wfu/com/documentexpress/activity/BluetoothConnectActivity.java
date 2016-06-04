package wfu.com.documentexpress.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.BlueTooth_list_adapter;


/**
 * Created by Lenovo on 2016/5/19.
 */
public class BluetoothConnectActivity extends Activity implements Serializable{
    private BluetoothAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    ArrayList<BluetoothDevice>  devices_list;
    BlueTooth_list_adapter madapter;
    BluetoothDevice   device;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_send);
        // 检查设备是否支持蓝牙
       devices_list=new ArrayList<BluetoothDevice>();
        ListView  bluetoothList=(ListView)findViewById(R.id.bluetoot_list);
         madapter=new BlueTooth_list_adapter(this,R.layout.blutooth_adapter_info,devices_list);
            bluetoothList.setAdapter(madapter);
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {

            // 设备不支持蓝牙
        }
// 打开蓝牙
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒

            startActivityForResult(intent, 200);
        }
        /*Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (int i = 0; i < devices.size(); i++) {
            BluetoothDevice device = (BluetoothDevice) devices.iterator().next();
            Log.d("debug",device.getName());
        } */ // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(receiver, intentFilter);
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        adapter.startDiscovery();
        Log.d("debug","Discovery");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        bluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             final    BluetoothDevice  mdevice=(BluetoothDevice)madapter.getItem(i);
                int connectState = mdevice.getBondState();
                switch (connectState) {
                    // 未配对
                    case BluetoothDevice.BOND_NONE:
                        // 配对
                        try {
                            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                            createBondMethod.invoke(mdevice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    // 已配对
                    case BluetoothDevice.BOND_BONDED:
                            Log.d("debug","connect");
                            // 连接
                        try {
                            connect(mdevice);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        adapter.cancelDiscovery();
                            Log.d("debug","cancel discovery");

                        break;
                }
            }
        });
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取查找到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("debug", device.getName());
                devices_list.add(device);
                madapter.notifyDataSetChanged();
                // 如果查找到的设备符合要连接的设备，处理
                // 搜索蓝牙设备的过程占用资源比较多，一旦找到需要连接的设备后需要及时关闭搜索
                //adapter.cancelDiscovery();
                // 获取蓝牙设备的连接状态
                int connectState = device.getBondState();
              /*  switch (connectState) {
                    // 未配对
                    case BluetoothDevice.BOND_NONE:
                        // 配对
                        try {
                            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                            createBondMethod.invoke(device);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    // 已配对
                    case BluetoothDevice.BOND_BONDED:
                        try {
                            // 连接
                            connect(device);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }*/
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 状态改变的广播
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                madapter.notifyDataSetChanged();
               /* int connectState = device.getBondState();
                switch (connectState) {
                    case BluetoothDevice.BOND_NONE:
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        try {
                            // 连接
                            connect(device);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }*/
            }
        }
    };

    private void connect(BluetoothDevice sdevice) throws IOException {
        // 固定的UUID
        final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
                                         UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothSocket socket = sdevice.createRfcommSocketToServiceRecord(uuid);
        Log.d("Debug","socket  connect");
        socket.connect();
                if(socket.isConnected()){    Log.d("debug","connect successful ");
                device=sdevice;
                    setContentView(R.layout.activity_sendfile);
                Intent  intent=getIntent();
                    intent.putExtra("bluetooth",device.getAddress());

                    intent.setClass(getApplicationContext(),BluetoothSendActivity.class);

                }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
