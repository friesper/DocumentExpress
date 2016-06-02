package wfu.com.documentexpress.utils;

import android.util.Log;

import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import wfu.com.documentexpress.socketoperation.Constant;

/**
 * Created by yinxucun on 16-6-2.
 */
public class wifiDirSocket {

    private  Socket  socket;
    private OutputStream stream;


    private File file;

    private   List<String>   aList;
    public  wifiDirSocket(List<String>  file_list){
        aList=file_list;
        initSocket();

    }
    protected void  initSocket()  {
        try{
        socket=new Socket("192.168.49.1", Constant.DEFAULT_BIND_PORT);
    }
        catch (Exception e){
            Log.d("debug","socket  create  failed");
        }
        try {
             stream = socket.getOutputStream();
        }
        catch (Exception e2){
            Log.d("debug","stream  is    Exception");
        }

    }
    protected   void   OpenFile(){
          for(String   name:aList){
              file=new File(name);
              if(file.isFile()){


              }
          }
          }
    }

