package wfu.com.documentexpress.socketoperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lenovo on 2016/5/10.
 */
public class AcceptScanOk {
    private static final String REC_OK = "REC_OK";
    private static final String SCAN_OK = "SCAN_OK";
    private static String Connectresult = null;
    private static String Receiveresult = null;
    public static String recScanOk(){
        ServerSocket ss = null;
        Socket s = null;
        String ip = null;
        try {
            ss = new ServerSocket(Constant.DEFAULT_PORT);
            //接受客户端消息
            s = ss.accept();
            ip = s.getInetAddress().getHostAddress();
            InputStream in = s.getInputStream();
            byte [] buf = new byte[1024];
            int len = in.read(buf);
            Connectresult = new String(buf,0,len);
            //反馈给客户端
            OutputStream out = s.getOutputStream();
            out.write(REC_OK.getBytes());
            s.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Connectresult+"|"+ip;
    }
    public static String sendScanOk(String targetIp)  {
        Socket socket = null;
        try {
            socket = new Socket(targetIp,Constant.DEFAULT_PORT);
            //获取socket输出流
            OutputStream out = socket.getOutputStream();
            out.write(SCAN_OK.getBytes());
            //收到反馈
            InputStream in = socket.getInputStream();
            byte [] buf = new byte[1024];
            int len = in.read(buf);
            Receiveresult = new String(buf,0,len);
            socket.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return Receiveresult;
    }

    public static String recScanOkByUDP(){
        System.out.println("接收端启动....");
        String ip=null;
        try {
            DatagramSocket ds = new DatagramSocket(Constant.DEFAULT_PORT);
            byte [] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf,buf.length);
            ds.receive(dp);
            ip = dp.getAddress().getHostAddress();
            Connectresult = new String(dp.getData(),0,dp.getLength());
            ds.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Connectresult+"|"+ip;
    }
    public static void sendScanOkByUDP(String targetIp)  {
        System.out.println("发送端启动....");
        try {
            DatagramSocket ds = new DatagramSocket();
            String str = SCAN_OK;
            byte [] buf = str.getBytes();
            DatagramPacket dp = new DatagramPacket(buf,buf.length, InetAddress.getByName(targetIp),Constant.DEFAULT_PORT);
            ds.send(dp);
            ds.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
