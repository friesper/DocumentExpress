package wfu.com.documentexpress.Server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

/**
 * 服务器连接线程
 * @author liujian
 *
 */
public class BluetoothServerConnThread extends Thread {
	
	private Handler serviceHandler;		//用于同Service通信的Handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;		//用于通信的Socket
	private BluetoothServerSocket serverSocket;
	private boolean isInterrupted=false;
	//蓝牙通讯线程
	private  BluetoothCommunThreads communThread;	
	
	/**
	 * 构造函数
	 * @param handler
	 */
	public BluetoothServerConnThread(Handler handler) {
		this.serviceHandler = handler;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public void close(){
        isInterrupted = true;
     		if (communThread != null) {
     			communThread.close();
     			communThread = null;
     		}
        if (serverSocket != null) {
			try {
				serverSocket.close();
				Log.v("debug" , "serverSocket已关闭");
			} catch (IOException e) {
				Log.e("debug", "serverSocket关闭 failed", e);
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				Log.v("debug" , "socket已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        super.interrupt();
	}
	
	@Override
	public void run() {		
		try {
			serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			while(!isInterrupted){				
				socket = serverSocket.accept();	
				if (socket != null) {				
					//开启通讯线程
					communThread = new BluetoothCommunThreads(serviceHandler, socket);			
					communThread.start();				
//					//发送连接成功消息，消息的obj字段为连接的socket
//					Message msg = serviceHandler.obtainMessage();
//					msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
//					msg.obj = socket;
//					msg.sendToTarget();
				} else {
					//发送连接失败消息
				//	serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
					break;
				}
			}
		} catch (Exception e) {
			//发送连接失败消息
		//	serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
			return;
		} 
		finally {
			Log.v("debug" , "BluetoothServerConnThread退出");
			try {
				serverSocket.close();
			} catch (Exception e) {
				Log.v("debug" , "serverSocket.close()  failed");
				e.printStackTrace();
			}
		}
	}
	
}
