package wfu.com.documentexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.socketoperation.Constant;
import wfu.com.documentexpress.utils.FileSizeUtil;
import wfu.com.documentexpress.utils.LogUtil;

/**
 * Created by Lenovo on 2016/5/9.
 */
public class SendActivity extends BaseActivity {
    //发送文件的activity，包括发送文件的socket
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
        initEvent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(targetIp!=null){
                    LogUtil.e("0", targetIp);
                    service(targetIp);
                }
            }
        }).start();

    }

    private void initView() {
        cancleTrans = (LinearLayout) findViewById(R.id.cancle_file_filesendlayout);
        transList = (ListView) findViewById(R.id.transing_filelist);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        title = (TextView) findViewById(R.id.trans_title);
        transFiles = new ArrayList<FileUpdate>();
        adapter = new FileUpdateAdapter(SendActivity.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);
    }

    private void initEvent() {
        Intent intent=getIntent();
        fileList.clear();
        path_list = (List<String>)intent.getSerializableExtra("path_list");
        targetIp = intent.getStringExtra("targetIp");
        for(int i = 0 ; i < path_list.size() ;i++){
            fileList.add(path_list.get(i));
        }

    }

    public void service(String ip){
        ExecutorService executorService = Executors.newCachedThreadPool();
        Vector<Integer> vector = getRandom(fileList.size());
        for(Integer integer : vector){
            String filePath = fileList.get(integer.intValue());
            executorService.execute(sendFile(filePath,ip));
        }
    }

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


    private  Runnable sendFile(final String filePath,final String targetIp){
        return new Runnable(){

            private Socket socket = null;
            private String ip =targetIp;
            private int port = Constant.DEFAULT_BIND_PORT;
            FileUpdate transfile = new FileUpdate();
            public void run() {
                System.out.println("开始发送文件:" + filePath);
                File file = new File(filePath);
                transfile.setPath(filePath);
                transfile.setName(file.getName());
                transfile.setTotalSize("/" + FileSizeUtil.FormetFileSize(file.length()));
                transFiles.add(transfile);

                if(createConnection()){
                    int bufferSize = 8192;
                    byte[] buf = new byte[bufferSize];
                    try {
                        DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

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
                        socket.close();
                        handler.sendEmptyMessage(0x127);
                        System.out.println("文件 " + filePath + "传输完成!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private boolean createConnection() {
                try {
                    socket = new Socket(ip, port);
                    System.out.println("连接服务器成功！");
                    return true;
                } catch (Exception e) {
                    System.out.println("连接服务器失败！");
                    return false;
                }
            }

        };
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
