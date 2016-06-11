package wfu.com.documentexpress.Server;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.io.Serializable;

/**
 * 蓝牙模块服务器端主控制Service
 * @author liujian
 *
 */
public class BluetoothServerService extends Service {

	//蓝牙适配器
	private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	
	//服务端连接线程
	private  BluetoothServerConnThread connThread;
	
	//蓝牙通讯
	private BluetoothCommunSocket communSocket;
	
	public BluetoothSocket socket;		//通信Socket
	//控制信息广播接收器
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		private BluetoothDevice device;
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();		
			if (BluetoothTools.ACTION_STOP_SERVICE.equals(action)) {				
				//关闭连接线程
				if (connThread != null) {
					connThread.close();
					connThread=null;
				}
				//关闭
				if (communSocket != null) {
					communSocket.close();
				}
				//停止后台服务
				stopSelf();				
			}else if (BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)) {
				//选择了连接的服务器设备
				device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
		
				//开启客户端连接线程
			//	new BluetoothClientConnThread(serviceHandler, device).start();			
			} else if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
				
				try {
					socket = device.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					socket.connect();
				} catch (IOException e) {
					e.printStackTrace();
				}			
				communSocket = new BluetoothCommunSocket(serviceHandler, socket);

				final TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				if (communSocket != null) {
				class MyRunnable implements Runnable{
					public void run(){
						communSocket.write(transmit);
					}
					}
				Thread t=new Thread(new MyRunnable());
				t.start();
				}
			}
		}
	};
	
	//接收其他线程消息的Handler
	private Handler serviceHandler = new Handler() {
		@Override
		public void handleMessage( Message msg) {			
			switch (msg.what) {
			case BluetoothTools.MESSAGE_CONNECT_SUCCESS:		
//				//发送连接成功消息
//				Intent connSuccIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
//				sendBroadcast(connSuccIntent);
				break;	
			case BluetoothTools.MESSAGE_CONNECT_ERROR:
				//发送连接错误广播
				Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);			
				break;			
			case BluetoothTools.MESSAGE_READ_OBJECT:
				//读取到数据
				//发送数据广播（包含数据对象）
				TransmitBean transmit = (TransmitBean)msg.obj;	
				Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
				dataIntent.putExtra(BluetoothTools.DATA, transmit);
				sendBroadcast(dataIntent);
				break;
			case BluetoothTools.FILE_RECIVE_PERCENT://文件接收百分比	
				//接收文件传输百分比广播，实现进度条用
				Intent flieIntent = new Intent(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
				flieIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
				sendBroadcast(flieIntent);			
				break;
			case BluetoothTools.FILE_SEND_PERCENT://文件发送百分比	
				//发送文件传输百分比广播，实现进度条用
				Intent flieIntent1 = new Intent(BluetoothTools.ACTION_FILE_SEND_PERCENT);
				flieIntent1.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
				sendBroadcast(flieIntent1);			
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onCreate() {
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);	
		controlFilter.addAction(BluetoothTools.ACTION_SELECTED_DEVICE);
		registerReceiver(controlReceiver, controlFilter);	
		
		bluetoothAdapter.enable();	//打开蓝牙
		//开启蓝牙发现功能（300秒）
		Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		discoveryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//提示信息
		startActivity(discoveryIntent);
		//开启连接线程
		connThread=new BluetoothServerConnThread(serviceHandler);	
		connThread.start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {		
		//关闭连接线程
		if (connThread != null) {
			connThread.close();
		}
		//关闭
		if (communSocket != null) {
			communSocket.close();
		}
		unregisterReceiver(controlReceiver);
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
