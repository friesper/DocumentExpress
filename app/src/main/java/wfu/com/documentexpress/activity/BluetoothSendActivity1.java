package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.utils.FileSizeUtil;

/**
 * Created by yinxucun on 16-6-4.
 */
public class BluetoothSendActivity1 extends Activity {
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
    BluetoothSocket  btSocket;
    String  bluetooth_mac;
    BluetoothDevice bluetoothDevice;

    Context  context;

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
        Intent intent  =getIntent();
        Log.d("debug","intent");
        List<String>  alit=(List<String>)intent.getSerializableExtra("path_list");

        if(alit==null|alit.size()==0){
        Log.d("debug","null");
        }
        madapter=BluetoothAdapter.getDefaultAdapter();
        bluetooth_mac=getIntent().getStringExtra("bluetooth");
        context=getApplicationContext();
        ExecutorService executorService = Executors.newCachedThreadPool();

            for (String name : alit) {
                getFilePath(name);
                sendFiles(bluetooth_mac,name);
                 }
    }
    public void sendFiles(String macAddress, String path) {
         bluetoothDevice = this.madapter.getRemoteDevice(macAddress);
            try {
            Method method = bluetoothDevice.getClass().getMethod("createRfcommSocket",
                    new Class[] {int.class});
            this.btSocket = (BluetoothSocket) method.invoke(bluetoothDevice, 1);
            method.invoke(bluetoothDevice, 1);
            ContentValues cv = new ContentValues();
            // 文件名字是 file:// + 文件名，这个地方需要注意 多加 /
            // eg: cv.put("uri", "file:///system/app/Contacts.apk");
            // socket可以不用连接
                /* this.btSocket.connect(); */
            cv.put("uri",   path);
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

        } finally {

            if(btSocket != null) {
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void getFilePath(String dirPath){
                                               fileList.add(dirPath);
    }
    private  Runnable sendFile(final String filePath,final String macAddress){
        return new Runnable(){

            FileUpdate transfile = new FileUpdate();
            public void run() {
                Log.d("debug","开始发送文件");
                System.out.println("开始发送文件:" + filePath);
                File file = new File(filePath);
                transfile.setPath(filePath);
                transfile.setName(file.getName());
                transfile.setTotalSize("/" + FileSizeUtil.FormetFileSize(file.length()));
                transFiles.add(transfile);

                if(createConnection()){
                    Log.d("debug","transfile");
                    int bufferSize = 8192;
                    byte[] buf = new byte[bufferSize];
                    try {
                        DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
                        DataOutputStream dos = new DataOutputStream(btSocket.getOutputStream());

                        dos.writeUTF(file.getName());
                        dos.flush();
                        dos.writeLong(file.length());
                        dos.flush();

                        int read = 0;
                        int passedlen = 0;
                        long length = file.length();      //获得要发送文件的长度
                        long startTime = System.currentTimeMillis(); // 开始下载时获取开始时间
                        while ((read = fis.read(buf)) != -1) {
                            passedlen += read;
                            dos.write(buf, 0, read);
                            transfile.setCurrentSize(FileSizeUtil.FormetFileSize(passedlen));
                            transfile.setCurrentProgress((int) (passedlen * 100L / length));
                            handler.sendEmptyMessage(0x126);
                        }
                        long curTime = System.currentTimeMillis();
                        int usedTime = (int) ((curTime-startTime)/1000);
                        if(usedTime==0)usedTime = 1;
                        double downloadSpeed = (passedlen / usedTime) / 1024/1024; // 下载速度
                        transfile.setCurrentSpeed(downloadSpeed+"M/S");
                        dos.flush();
                        dos.flush();
                        fis.close();
                        dos.close();
                        btSocket.close();
                        handler.sendEmptyMessage(0x127);
                        Log.d("debug","文件 " + filePath + "传输完成!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private boolean createConnection() {
                 bluetoothDevice = madapter.getRemoteDevice(macAddress);
                try {

                    Method method = bluetoothDevice.getClass().getMethod("createRfcommSocket",
                            new Class[] {int.class});
                    btSocket = (BluetoothSocket) method.invoke(bluetoothDevice, 1);
                    method.invoke(bluetoothDevice, 1);
                    Log.d("debug","连接服务器成功");
                     return true;
                } catch (Exception e) {
                    Log.d("debug","连接服务器失败");
                    return false;
                }
            }

        };
    }
    private void initView() {
        cancleTrans = (LinearLayout) findViewById(R.id.cancle_file_filesendlayout);
        transList = (ListView) findViewById(R.id.transing_filelist);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        title = (TextView) findViewById(R.id.trans_title);
        transFiles = new ArrayList<FileUpdate>();
        adapter = new FileUpdateAdapter(BluetoothSendActivity1.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);
    }


}
