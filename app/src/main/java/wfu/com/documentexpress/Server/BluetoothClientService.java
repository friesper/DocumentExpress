package wfu.com.documentexpress.Server;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import android.util.Log;

/**
 * 蓝牙模块客户端主控制Service
 * @author liujian
 *
 */
public class BluetoothClientService extends Service {
	
	//搜索到的远程设备集合
	private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
	//蓝牙适配器
	private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	//蓝牙通讯
	private BluetoothCommunSocket communSocket;
	
	public BluetoothSocket socket;		//通信Socket
	
	//服务端连接线程
	private  BluetoothServerConnThread connThread;
	//控制信息广播的接收器
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		private BluetoothDevice device;
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();		
			if (BluetoothTools.ACTION_START_DISCOVERY.equals(action)) {
				//开始搜索
				discoveredDevices.clear();	//清空存放设备的集合
				bluetoothAdapter.enable();	//打开蓝牙
				bluetoothAdapter.startDiscovery();	//开始搜索		
			} else if (BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)) {
				//选择了连接的服务器设备
				device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);				
			} else if (BluetoothTools.ACTION_STOP_SERVICE.equals(action)) {
				//关闭连接线程
				if (connThread != null) {
					connThread.close();
				}
				//关闭
				if (communSocket != null) {
					communSocket.close();
				}
				//停止后台服务
				stopSelf();					
			} else if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
				//与服务端建立连接
				try {
					socket = device.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
			//		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					socket.connect();
				} catch (IOException e) {
					Log.v("调试", "连接服务端失败");
					e.printStackTrace();
				}			
				communSocket = new BluetoothCommunSocket(handler, socket);				


				final TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				class MyRunnable implements Runnable{
					public void run(){


							communSocket.write(transmit);

					}
					}
				Thread t=new Thread(new MyRunnable());
				t.start();

			}
		}
	};
	
	/*
	//蓝牙搜索广播的接收器
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//获取广播的Action
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				System.out.print("//开始搜索");
				//开始搜索
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				System.out.print("//发现远程蓝牙设备");
				//发现远程蓝牙设备
				//获取设备
				BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(bluetoothDevice);
				//发送发现设备广播
				Intent deviceListIntent = new Intent(BluetoothTools.ACTION_FOUND_DEVICE);
				deviceListIntent.putExtra(BluetoothTools.DEVICE, bluetoothDevice);
				sendBroadcast(deviceListIntent);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				//搜索结束
				System.out.print("//搜索结束");
				Intent foundIntent = new Intent(BluetoothTools.ACTION_DISCOVERY_FINISHED);
				sendBroadcast(foundIntent);
			}
		}
	};
	*/

	//接收其他线程消息的Handler
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//处理消息
			switch (msg.what) {
			case BluetoothTools.MESSAGE_CONNECT_ERROR://连接错误
				//发送连接错误广播
				Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);
				break;
			case BluetoothTools.MESSAGE_CONNECT_SUCCESS://连接成功			
				//开启通讯线程
			//	communSocket = new BluetoothCommunSocket(handler, (BluetoothSocket)msg.obj);
			//	communSocket.start();				
				//发送连接成功广播
//				Intent succIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
//				sendBroadcast(succIntent);
				break;
			case BluetoothTools.MESSAGE_READ_OBJECT://读取到对象
				//发送数据广播（包含数据对象）
				Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
				dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
				sendBroadcast(dataIntent);
				break;
			case BluetoothTools.FILE_SEND_PERCENT://文件发送百分比
				//发送文件传输百分比广播，实现进度条用
				Intent flieIntent = new Intent(BluetoothTools.ACTION_FILE_SEND_PERCENT);
				flieIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
				sendBroadcast(flieIntent);
				break;
			case BluetoothTools.FILE_RECIVE_PERCENT://文件接收百分比	
				//接收文件传输百分比广播，实现进度条用
				Intent flieIntent1 = new Intent(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
				flieIntent1.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
				sendBroadcast(flieIntent1);			
				break;
			}
			super.handleMessage(msg);
		}
	};
	

	@Override
	public void onStart(Intent intent, int startId) {		
		super.onStart(intent, startId);
	}	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/**
	 * Service创建时的回调函数
	 */
	@Override
	public void onCreate() {
		//discoveryReceiver的IntentFilter
		IntentFilter discoveryFilter = new IntentFilter();
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);	
		//controlReceiver的IntentFilter
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(BluetoothTools.ACTION_START_DISCOVERY);
		controlFilter.addAction(BluetoothTools.ACTION_SELECTED_DEVICE);
		controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);	
		//注册BroadcastReceiver
	//	registerReceiver(discoveryReceiver, discoveryFilter);
		registerReceiver(controlReceiver, controlFilter);
		//开启连接线程
		connThread=new BluetoothServerConnThread(handler);	
		connThread.start();
		super.onCreate();
	}	

	
	/**
	 * Service销毁时的回调函数
	 */
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
		//解除绑定
	//	unregisterReceiver(discoveryReceiver);
		unregisterReceiver(controlReceiver);
		super.onDestroy();
	}
}
