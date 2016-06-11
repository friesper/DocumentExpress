package wfu.com.documentexpress.Server;

import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * 蓝牙通讯线程
 * @author liujian
 *
 */
public class BluetoothCommunSocket {
	private Handler serviceHandler;		//与Service通信的Handler
	public BluetoothSocket socket;
	public DataInputStream inStream;		//对象输入流
	public DataOutputStream outStream;	//对象输出流
	public volatile boolean isRun = true;	//运行标志位
	private long downbl;

	/**
	 * 构造函数
	 * @param handler 用于接收消息
	 * @param socket
	 */
	public BluetoothCommunSocket(Handler handler, BluetoothSocket socket) {
		this.serviceHandler = handler;
		this.socket = socket;
		try {
			this.inStream= new DataInputStream(this.socket.getInputStream());
			this.outStream= new DataOutputStream(this.socket.getOutputStream());
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
		}
	}
	public void close(){
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				Log.v("调试" , "clientsocket已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入流
	 * @param obj
	 */
	public void write(Object obj) {
		try {
			TransmitBean transmit_s = (TransmitBean) obj;
			if(transmit_s.getFilename()!=null&&!"".equals(transmit_s.getFilename())){
				Log.v("调试" , "type:"+2);
				String filename=transmit_s.getFilename();
				byte type = 2; //类型为2，即传文件 
				//读取文件长度
				FileInputStream fins=new FileInputStream(transmit_s.getFilepath());
				long fileDataLen = fins.available(); //文件的总长度			
				int f_len=filename.getBytes("GBK").length; //文件名长度

				byte[] data=new byte[f_len];
				data=filename.getBytes("GBK");
				long totalLen = 4+1+1+f_len+fileDataLen;//数据的总长度
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入文件名的长度
				outStream.write(data);    //4.写入文件名的数据
				outStream.flush();								
				//读取文件并发送
				try { 	
					byte[] buffer=new byte[1024*64];
					downbl=0;
					int size=0;
					long sendlen=0;
					float tspeed=0;
					int i=0;
					long time1=Calendar.getInstance().getTimeInMillis();
					while((size=fins.read(buffer, 0, 1024*64))!=-1)
					{  						
						outStream.write(buffer, 0, size);
						outStream.flush();
						sendlen+=size;
						Log.v("调试" , "fileDataLen:"+fileDataLen);
						i++;
						if(i%5==0){
							long time2=Calendar.getInstance().getTimeInMillis();
							tspeed=sendlen/(time2-time1)*1000/1024;	
						}
					//	Log.v("调试" ,"tspeed："+tspeed);
						downbl = ((sendlen * 100) / fileDataLen);
						TransmitBean up = new TransmitBean();
						up.setUppercent(String.valueOf(downbl));	
						up.setTspeed(String.valueOf(tspeed));
						if(i==1){
							up.setShowflag(true);
						}else{
							up.setShowflag(false);
						}
						Message msg = serviceHandler.obtainMessage();											
						msg.what = BluetoothTools.FILE_SEND_PERCENT;					
						msg.obj = up;
						msg.sendToTarget();		
					}    
					fins.close();    	
					Log.v("调试" , "文件发送完成");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}else{
				Log.v("调试" , "type:"+1);
				byte type = 1; //类型为1，即传文本消息
				String msg=transmit_s.getMsg();
				int f_len=msg.getBytes("GBK").length; //消息长度
				long totalLen = 4+1+1+f_len;//数据的总长度
				byte[] data=new byte[f_len];
				data=msg.getBytes("GBK");
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入消息的长度
				outStream.write(data);    //4.写入消息数据
				outStream.flush();
			}
			
			this.read();
			
			byte[] ef = new byte[3];
			inStream.read(ef);//读取消息
			String eof = new String(ef);
			if("EOF".equals(eof)){
				Log.v("调试" ,"接收EOF");
			}
		}catch (Exception ex) {
			Log.v("调试" , "通讯中断Exception:");
			//发送通讯失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			ex.printStackTrace();
		}
		finally {
			close();
		}
	}
	
	
	public void read() throws IOException{			
		TransmitBean transmit_r = new TransmitBean();
		long totalLen = inStream.readLong();//总长度
		if(totalLen>0){
		byte type = inStream.readByte();//类型
		if(type==1){//文本类型
			try {
				byte len = inStream.readByte();//消息长度
				byte[] ml = new byte[len];
				int size=0;
				int receivelen=0;
				while (receivelen <len){
					size=inStream.read(ml,0,ml.length);
					receivelen+=size;
				}
				String msg = new String(ml);
				Log.v("调试" , "msg:"+msg);
				transmit_r.setMsg(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(type==2){//文件类型
			try {
				byte len = inStream.readByte();//文件名长度
				byte[] fn = new byte[len];
				inStream.read(fn);//读取文件名
				String filename = new String(fn,"GBK");
				Log.v("调试" , "filename:"+filename);
				transmit_r.setFilename(filename);						
				long datalength = totalLen-1-4-1-fn.length;//文件数据		
				String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + transmit_r.getFilename();
				transmit_r.setFilepath(savePath);
				FileOutputStream file=new FileOutputStream(savePath, false);
				byte[] buffer = new byte[1024*1024];
				int size = -1;
				long receivelen=0;
				int i=0;
				float tspeed=0;
				long time1=Calendar.getInstance().getTimeInMillis();
				while (receivelen <datalength){						
					size=inStream.read(buffer);
					file.write(buffer, 0 ,size);
					receivelen+=size;
					i++;
					if(i%10==0){
						long time2=Calendar.getInstance().getTimeInMillis();
						tspeed=receivelen/(time2-time1)*1000/1024;	
					}													
					downbl = (receivelen * 100) / datalength;						
					TransmitBean up = new TransmitBean();
					up.setUppercent(String.valueOf(downbl));
					up.setTspeed(String.valueOf(tspeed));
					if(i==1){
						up.setShowflag(true);
					}else{
						up.setShowflag(false);
					}
					Message msg = serviceHandler.obtainMessage();
					msg.what = BluetoothTools.FILE_RECIVE_PERCENT;
					msg.obj = up;
					msg.sendToTarget();
				}
				Log.v("调试" , "接收完成,receivelen:"+receivelen);
				file.flush();
				file.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//发送成功读取到对象的消息，消息的obj参数为读取到的对象
		Message msg = serviceHandler.obtainMessage();
		msg.what = BluetoothTools.MESSAGE_READ_OBJECT;	
		msg.obj = transmit_r;
		msg.sendToTarget();
		}
	}

}
