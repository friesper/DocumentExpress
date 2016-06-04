package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;

import wfu.com.documentexpress.R;

/**
 * Created by yinxucun on 16-6-4.
 */
public class BluetoothSendActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void sendFile(String macAddress, String path) {
        BluetoothDevice bluetoothDevice = this.adapter.getRemoteDevice(macAddress);
        try {
            Method method = bluetoothDevice.getClass().getMethod("createRfcommSocket",
                    new Class[] {int.class});
            // this.btSocket = (BluetoothSocket) method.invoke(bluetoothDevice, 1);
            method.invoke(bluetoothDevice, 1);
            ContentValues cv = new ContentValues();
            // 文件名字是 file:// + 文件名，这个地方需要注意 多加 /
            // eg: cv.put("uri", "file:///system/app/Contacts.apk");
            // socket可以不用连接
                /* this.btSocket.connect(); */
            cv.put("uri", PATHK + path);
            cv.put("destination", macAddress);
            cv.put("direction", 0);
            cv.put("timestamp", System.currentTimeMillis());
            this.context.getContentResolver().insert(
                    Uri.parse("content://com.android.bluetooth.opp/btopp"), cv);
            // 发送完毕取消连接
            // btSocket.close();
        } catch (Exception e) {
            // 其他错误
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getString(R.string.title_send_fail),
                    Toast.LENGTH_LONG).show();
        } finally {
            Toast.makeText(context, context.getResources().getString(R.string.title_sending),
                    Toast.LENGTH_LONG).show();
            if(btSocket != null) {
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
