package wfu.com.documentexpress.utils;

import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.socketoperation.Constant;

/**
 * Created by yinxucun on 16-6-2.
 */
public class TransterServer {


    private int defaultBindPort = Constant.DEFAULT_BIND_PORT;    //默认监听端口号为10000
    private int tryBindTimes = 0;           //初始的绑定端口的次数设定为0

    private ServerSocket serverSocket;      //服务套接字等待对方的连接和文件发送
    private   String  savePath;
    private ExecutorService executorService;    //线程池
    private final int POOL_SIZE = 4;            //单个CPU的线程池大小


    private List<FileUpdate> transFiles;
    private ListView transList;
    private android.os.Handler   handler;
    /**
     * 不带参数的构造器，选用默认的端口号
     * @throws Exception
     */
    public TransterServer() throws Exception{
        try {
            this.bingToServerPort(defaultBindPort);
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
            Log.d("debug","开辟线程数 ： " + Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        } catch (Exception e) {
            throw new Exception("绑定端口不成功!");
        }
    }

    /**
     * 带参数的构造器，选用用户指定的端口号
     * @param port
     * @throws Exception
     */
    public TransterServer(int port, String reciverPath, List<FileUpdate> transFiles, android.os.Handler handler) throws Exception{
        try {
            this.bingToServerPort(port);
            this.savePath=reciverPath;
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        } catch (Exception e) {
            throw new Exception("绑定端口不成功!");
        }
        this.transFiles=transFiles;
        this.handler=handler;
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

    public void service(){
        Socket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
                executorService.execute(new Handler(socket));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class Handler implements Runnable{
        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }
        FileUpdate recfile = new FileUpdate();

        public void run() {

            Log.d("debug","New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

            DataInputStream dis = null;
            DataOutputStream dos = null;

            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];

            try {
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String filename = savePath + dis.readUTF();
                long length = dis.readLong();
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
                recfile.setPath(filename);
                recfile.setName(filename);
                recfile.setTotalSize("/" + FileSizeUtil.FormetFileSize(length));
                transFiles.add(recfile);
                handler.sendEmptyMessage(0x126);
                int read = 0;
                long passedlen = 0;
                long startTime = System.currentTimeMillis(); // 开始下载时获取开始时间
                while ((read = dis.read(buf)) != -1) {

                    passedlen += read;
                    dos.write(buf, 0, read);
                    Log.d("debug","文件[" + savePath + "]已经接收: " + passedlen * 100L/ length + "%");
                    recfile.setCurrentSize(FileSizeUtil.FormetFileSize(passedlen));
                    recfile.setCurrentProgress((int) (passedlen * 100L / length));
                    handler.sendEmptyMessage(0x126);

                }
                long curTime = System.currentTimeMillis();
                int usedTime = (int) ((curTime-startTime)/1000);
                if(usedTime==0)usedTime = 1;
                double downloadSpeed = (passedlen / usedTime) / 1024/1024; // 下载速度
                recfile.setCurrentSpeed(downloadSpeed + "M/S");
                Log.d("debug","文件: " + savePath + "接收完成!");
                handler.sendEmptyMessage(0x129);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("debug","接收文件失败!");
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
}
