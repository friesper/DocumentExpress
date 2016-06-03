package wfu.com.documentexpress.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
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

/**
 * Created by yinxucun on 16-6-2.
 */
public class TransterClient {
    private static ArrayList<String> fileList = new ArrayList<String>();
    private List<FileUpdate> transFiles;
    private   boolean  transFile_flag=false;
    private   Handler  handler;

    /**
     * 带参数的构造器，用户设定需要传送文件的文件夹
     */
    public TransterClient(List<String>   alist, List<FileUpdate> transFiles,Handler handler){

            for(String file_name:alist){
        getFilePath(file_name);
            }
        this.transFiles=transFiles;
        this.handler=handler;
    }

    /**
     * 不带参数的构造器。使用默认的传送文件的文件夹
     */

    public boolean service(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        Vector<Integer> vector = getRandom(fileList.size());
        for(Integer integer : vector){
            String filePath = fileList.get(integer.intValue());
            executorService.execute(sendFile(filePath));
        }
        if(!transFile_flag){
            return   false;
        }
        else {
            return true;
        }
    }


    private void getFilePath(String dirPath){

                fileList.add(dirPath);


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

    private  Runnable sendFile(final String filePath){
        return new Runnable(){

            private Socket socket = null;
            private String ip ="192.168.49.1";
            private int port = Constant.DEFAULT_BIND_PORT;

            public void run() {
                System.out.println("开始发送文件:" + filePath);
                File file = new File(filePath);
                FileUpdate transfile = new FileUpdate();
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
                            Log.d("debug","已经完成文件 [" + file.getName() + "]百分比: " + passedlen * 100L/ length + "%");
                            dos.write(buf, 0, read);
                            transfile.setCurrentSize(FileSizeUtil.FormetFileSize(passedlen));
                            transfile.setCurrentProgress((int) (passedlen * 100L / length));
                            handler.sendEmptyMessage(0x13);
                        }
                        long curTime = System.currentTimeMillis();
                        int usedTime = (int) ((curTime-startTime)/1000);
                        if(usedTime==0)usedTime = 1;
                        double downloadSpeed = (passedlen / usedTime) / 1024/1024; // 下载速度
                        transfile.setCurrentSpeed(downloadSpeed+"M/S");
                        dos.flush();
                        fis.close();
                        dos.close();
                        socket.close();
                        transFile_flag=true;
                        handler.sendEmptyMessage(0x14);
                        Log.d("debug","文件 " + filePath + "传输完成!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private boolean createConnection() {
                try {
                    socket = new Socket(ip, port);
                    Log.d("debug","连接服务器成功！");
                    return true;
                } catch (Exception e) {
                    Log.d("debug","连接服务器失败！");
                    return false;
                }
            }

        };
    }


}
