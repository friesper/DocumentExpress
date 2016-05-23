package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import wfu.com.documentexpress.R;

/**
 * Created by Lenovo on 2016/5/19.
 */
public class BluetoothReceiverActivity extends Activity {
    private BluetoothAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_receiver);
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null)
        {
            // 设备不支持蓝牙
        }
// 打开蓝牙
        if (!adapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }

    }
}
