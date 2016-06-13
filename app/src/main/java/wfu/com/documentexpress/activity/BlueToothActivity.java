package wfu.com.documentexpress.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.Server.BluetoothClientService;
import wfu.com.documentexpress.Server.BluetoothTools;
import wfu.com.documentexpress.utils.ClientListListener;

/**
 * Created by yinxucun on 16-6-11.
 */
public class BlueToothActivity extends BaseActivity {
    private Button SearchBtn;
    private static final String tag = "debug";
    private static final int  REQUEST_DISCOVERABLE_BLUETOOTH = 3;
    private BluetoothAdapter bluetooth=BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> unbondDevices=new ArrayList<BluetoothDevice>(); // 用于存放未配对蓝牙设备
    private ArrayList<BluetoothDevice> bondDevices=new ArrayList<BluetoothDevice>();  // 用于存放已配对蓝牙设备
    private ListView unbondDevicesListView ;
    private ListView bondDevicesListView;
    ProgressDialog progressDialog = null;
    private ClientListListener clientListListener;
    @Override
    protected void onStart() {
        //开启后台service
        Log.v("debug" , "ClientActivity onStart开启后台Servic");
        Intent startService = new Intent(BlueToothActivity.this, BluetoothClientService.class);
        startService(startService);

        // 注册Receiver来获取蓝牙设备相关的结果 将action指定为：ACTION_FOUND
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        //    intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //    intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //注册广播接收器
        registerReceiver(searchDevices, intent);
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置这个窗口
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue_tooth_activity_layout);
        unbondDevicesListView = (ListView) this.findViewById(R.id.unbondDevices);
        bondDevicesListView = (ListView) this.findViewById(R.id.bondDevices);
        SearchBtn = (Button)findViewById(R.id.searchDevices);
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始搜索
                unbondDevices.clear();
                //	bondDevices.clear();
                Log.v("debug", "开始搜索");
                setTitle("本机蓝牙地址：" + bluetooth.getAddress());
                //扫描蓝牙设备 最少要12秒，功耗也非常大（电池等） 是异步扫描意思就是一调用就会扫描
                bluetooth.startDiscovery();
            }
        });
        operbluetooth();
        clientListListener=new ClientListListener (this, bluetooth, unbondDevices, bondDevices, unbondDevicesListView, bondDevicesListView);
        clientListListener.addBondDevicesToListView();
    }
    public void operbluetooth() {
        //判断是否有Bluetooth设备
        if (bluetooth == null) {
            Toast.makeText(this, "没有检测到蓝牙设备", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.v(tag , "检测到蓝牙设备!");
        //判断当前设备中的蓝牙设备是否已经打开（调用isEnabled()来查询当前蓝牙设备的状态，如果返回为false，则表示蓝牙设备没有开启）
        boolean originalBluetooth = (bluetooth != null && bluetooth.isEnabled());
        if(originalBluetooth){
            Log.d(tag , "蓝牙设备已经开启!");
            return;
        }else if (originalBluetooth == false) {
            //打开Bluetooth设备 这个无提示效果
            //bluetooth.enable();
            //也可以这样,这个有提示效果
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
           /*确保蓝牙被发现*/
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //设置可见状态的持续时间为500秒，但是最多是300秒
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BLUETOOTH);
    }
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // progressDialog = ProgressDialog.show(context, "请稍等...","搜索蓝牙设备中...", true,true);
                setProgressBarIndeterminateVisibility(true);
                setTitle("扫描设备...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("debug", "设备搜索完毕");
                setProgressBarIndeterminateVisibility(false);
                setTitle("选择连接的设备");
                //   clientListListener.addUnbondDevicesToListView();
                clientListListener.addBondDevicesToListView();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获取到设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str= device.getName() + "|" + device.getAddress();
                System.out.println(str);

                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

                    if (!bondDevices.contains(device)) {
                        bondDevices.add(device);
                    }
                } else {
                    if (!unbondDevices.contains(device)) {
                        unbondDevices.add(device);
                        clientListListener.addUnbondDevicesToListView();
                    }
                }
            }
        }
    };
    @Override
    public void onBackPressed() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
        bluetooth.cancelDiscovery();
        // 这里处理逻辑代码，注意：该方法仅适用于2.0或更新版的sdk
        //关闭后台Service
        Intent stopService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
        sendBroadcast(stopService);
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(searchDevices);
        super.onDestroy();
    }

}
