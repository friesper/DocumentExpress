package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;


import wfu.com.documentexpress.R;
import wfu.com.documentexpress.Server.BluetoothTools;
import wfu.com.documentexpress.Server.TransmitBean;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.utils.FileSizeUtil;

/**
 * Created by yinxucun on 16-6-11.
 */
public class bluetoothClientActivity_express extends Activity {
    FileUpdate transfile ;
    public static final int RESULT_CODE = 1000;    //选择文件   请求码
    public static final String SEND_FILE_NAME = "sendFileName";
    private TextView serversText;
    private  List<FileUpdate>  files_list;
    private static ArrayList<String> fileList = new ArrayList<String>();
    private Button filesendBtn;
    private  List<String>  file_list;
    private ListView transList;
    private List<FileUpdate> transFiles;
    public    FileUpdateAdapter adapter;
    File file;
    Button mSelectFileBtn;
    TextView mSendFileNameTV;
    private ProgressDialog spDialog;
    private ProgressDialog rpDialog;
    @Override
    protected void onStart() {

        //开启后台service  因为之前关闭后台service，此处开启只是调用后台service的onStart方法，可以去掉这里的startService
//		Intent startService = new Intent(ClientActivity2.this, BluetoothClientService.class);
//		startService(startService);

        //注册BoradcasrReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);
        intentFilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
        intentFilter.addAction(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
        registerReceiver(broadcastReceiver, intentFilter);
        Log.d("debug","resiger  broad");
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_client_activity_express);
        initView();
        initEvent();



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_CODE){
            //请求为 "选择文件"
            try {fileList = new ArrayList<String>();
                FileUpdate  fileUpdate=new FileUpdate();
                file_list=(List<String>)data.getSerializableExtra("path_list");
                for(String  s:file_list){
                    fileList.add(s);
                }

                Log.d("debug",file_list.toString());
                //取得选择的文件名
               // String sendFileName = data.getStringExtra(SEND_FILE_NAME);
               // mSendFileNameTV.setText(sendFileName);
            } catch (Exception e) {
            }
        }
    }


    void initView(){
        transList = (ListView) findViewById(R.id.trans_file_list);
    }
   void initEvent(){
       transfile = new FileUpdate();
       transFiles = new ArrayList<FileUpdate>();
       adapter = new FileUpdateAdapter(bluetoothClientActivity_express.this,R.layout.file_progress_item,transFiles);
       transList.setAdapter(adapter);
       mSelectFileBtn = (Button) findViewById(R.id.cancelSearchBtn);
       mSelectFileBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(bluetoothClientActivity_express.this, blueToothSelectActivity.class);
               startActivityForResult(intent, bluetoothClientActivity_express.RESULT_CODE);
           }
       });
       filesendBtn = (Button)findViewById(R.id.fileSendBtn);
       filesendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FileUpdate transfile = new FileUpdate();
				/*发送文件  由于Intent无法传递很多数据，所以先将文件路径广播给BluetoothClientService，
				 * *由该Service读取文件后通过对象流发送给远程蓝牙设备
				 */
               if (file_list.size()==0) {
                   Toast.makeText(bluetoothClientActivity_express.this, "未选择文件", Toast.LENGTH_SHORT).show();
               } else if(file_list!=null) {
                   List<TransmitBean> list=new ArrayList<TransmitBean>();
                    Log.d("debug",file_list.toString());
                   Vector<Integer> vector = getRandom(fileList.size());
                  for(Integer integer : vector){
                       TransmitBean transmit = new TransmitBean();
                       String path = fileList.get(integer.intValue());
                       Log.d("debug",path);
                       String filename = path.substring(path.lastIndexOf("/") + 1, path.length());
                       transmit.setFilename(filename);
                       transmit.setFilepath(path);
                       list.add(transmit);
                   }Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
                   for (TransmitBean transmitBean:list) {
                       sendDataIntent.putExtra(BluetoothTools.DATA,  transmitBean);
                       sendBroadcast(sendDataIntent);
                   }
                }
           }
       });
   }
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothTools.ACTION_CONNECT_ERROR.equals(action)) {//连接失败

                Toast.makeText(bluetoothClientActivity_express.this, "通讯失败", Toast.LENGTH_LONG).show();
            }
            if (BluetoothTools.ACTION_DATA_TO_GAME.equals(action)) {//接收数据


            }
            if (BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)) {//发送文件百分比

                TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);


                if(!"0".equals(data.getTspeed())){
                    transfile.setCurrentSpeed(data.getTspeed()+"k/s");
                    adapter.notifyDataSetChanged();
                }
                transfile.setCurrentProgress(Integer.valueOf(data.getUppercent()));


            }
            if (BluetoothTools.ACTION_FILE_RECIVE_PERCENT.equals(action)) {//接收文件百分比
                FileUpdate transfile = new FileUpdate();
                TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
                transfile.setPath(data.getFilepath());
                transfile.setName(data.getFilename());
                transFiles.add(transfile);

                if(!"0".equals(data.getTspeed())){
                    transfile.setCurrentSpeed(data.getTspeed()+"k/s");

                }



            }
        }
    };
    private Vector<Integer> getRandom(int size){
        Vector<Integer> v = new Vector<Integer>();
        Random r = new Random();
        boolean b = true;
        while(b){
            int i = r.nextInt(size);
            if(!v.contains(i))
                v.add(i);
            if(v.size() == size)
                b = false;
        }
        return v;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}