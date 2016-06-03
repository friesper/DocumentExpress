package wfu.com.documentexpress.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.socketoperation.AcceptScanOk;
import wfu.com.documentexpress.socketoperation.Constant;
import wfu.com.documentexpress.utils.FileSizeUtil;
import wfu.com.documentexpress.utils.LogUtil;
import wfu.com.documentexpress.view.WaitDialog;
import wfu.com.documentexpress.wifioperation.WifiAdmin;

/**
 * Created by Lenovo on 2016/5/9.
 */
public class ReceiveActivity extends BaseActivity {
    //接收文件的activity，包括接受文件的seversocket
    //给二维码显示界面反馈信息
    private int defaultBindPort = Constant.DEFAULT_BIND_PORT;    //默认监听端口号为10000
    private int tryBindTimes = 0;           //初始的绑定端口的次数设定为0
    private ServerSocket serverSocket;      //服务套接字等待对方的连接和文件发送
    private ExecutorService executorService;    //线程池
    private final int POOL_SIZE = 4;//单个CPU的线程池大小

    private List<FileUpdate> transFiles;
    private WifiAdmin wifiAdmin;
    private static final String REC_OK = "REC_OK";
    private static final String SCAN_OK = "SCAN_OK";
    private String targetIp;
    private WaitDialog waitDialog;
    private FileUpdateAdapter adapter;
    private ListView transList;
    private Button interrupt_trans;
    private TextView title;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recefile);
        initView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String ssid = intent.getStringExtra("ssid");
                String password = intent.getStringExtra("password");
                wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
                wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
                String recevicePath = Environment.getExternalStorageDirectory().getPath()+"/";
                myhandler.sendEmptyMessage(0x127);
                while(!isWifiConnected(ReceiveActivity.this)){



                }
                myhandler.sendEmptyMessage(0x128);
                targetIp = intToIp(wifiAdmin.getDhcpInfo().serverAddress);
                LogUtil.e("1", targetIp);
                AcceptScanOk.sendScanOkByUDP(targetIp);
                startSendSever(recevicePath);

            }
        }).start();



    }

    private void initView() {
        title = (TextView) findViewById(R.id.trans_title);
        interrupt_trans = (Button) findViewById(R.id.interrupt_trans);
        transList = (ListView) findViewById(R.id.transing_filelist);
        wifiAdmin = new WifiAdmin(ReceiveActivity.this);
        waitDialog = new WaitDialog(ReceiveActivity.this);
        waitDialog.setContent("正在连接...");
        transFiles = new ArrayList<FileUpdate>();
        adapter = new FileUpdateAdapter(ReceiveActivity.this,R.layout.file_progress_item,transFiles);
        transList.setAdapter(adapter);
    }

    // 是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }

        return false ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //IP转换
    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
    private void startSendSever(String recevicePath) {
        try {
            this.bingToServerPort(defaultBindPort);
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
            System.out.println("开辟线程数 ： " + Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        } catch (Exception e) {
            try {
                throw new Exception("绑定端口不成功!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        service(recevicePath);
    }

    private void bingToServerPort(int port) throws Exception{
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(port);
            System.out.println("服务启动!");
        } catch (Exception e) {
            this.tryBindTimes = this.tryBindTimes + 1;
            port = port + this.tryBindTimes;
            if(this.tryBindTimes >= 20){
                throw new Exception("您已经尝试很多次了，但是仍无法绑定到指定的端口!请重新选择绑定的默认端口号");
            }
            //递归绑定端口
            this.bingToServerPort(port);
        }
    }

    public void service(String re){
        Socket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
                executorService.execute(new Handler(socket,re));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class Handler implements Runnable{
        private Socket socket;
        private String RECEIVE_FILE_PATH;
        FileUpdate recfile = new FileUpdate();
        public Handler(Socket socket,String re){
            this.socket = socket;
            RECEIVE_FILE_PATH = re;
        }

        public void run() {

            System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

            DataInputStream dis = null;
            DataOutputStream dos = null;

            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];

            try {
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String fileName = dis.readUTF();
                String savePath = RECEIVE_FILE_PATH + fileName;
                long length = dis.readLong();
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));

                recfile.setPath(savePath);
                recfile.setName(fileName);
                recfile.setTotalSize("/" + FileSizeUtil.FormetFileSize(length));
                transFiles.add(recfile);


                int read = 0;
                long passedlen = 0;
                long startTime = System.currentTimeMillis(); // 开始下载时获取开始时间
                while ((read = dis.read(buf)) != -1) {
                    passedlen += read;
                    dos.write(buf, 0, read);
                    recfile.setCurrentSize(FileSizeUtil.FormetFileSize(passedlen));
                    recfile.setCurrentProgress((int) (passedlen * 100L / length));
                    myhandler.sendEmptyMessage(0x126);
                }
                long curTime = System.currentTimeMillis();
                int usedTime = (int) ((curTime-startTime)/1000);
                if(usedTime==0)usedTime = 1;
                double downloadSpeed = (passedlen / usedTime) / 1024/1024; // 下载速度
                recfile.setCurrentSpeed(downloadSpeed + "M/S");
                System.out.println("文件: " + savePath + "接收完成!");
                myhandler.sendEmptyMessage(0x129);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("接收文件失败!");
            }finally{
                try {
                    if(dos != null){
                        dos.close();
                    }
                    if(dis != null){
                        dis.close();
                    }
                    if(socket != null){
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

}
