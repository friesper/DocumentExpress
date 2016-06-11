package wfu.com.documentexpress.Server;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import java.util.UUID;

/**
 * 蓝牙工具类
 * @author liujian
 *
 */
public class BluetoothTools {

	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	
	/**
	 * 本程序所使用的UUID
	 */
	//public static final UUID PRIVATE_UUID = UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
	public static final UUID PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	/**
	 * 字符串常量，存放在Intent中的设备对象
	 */
	public static final String DEVICE = "DEVICE";
	
	/**
	 * 字符串常量，服务器所在设备列表中的位置
	 */
	public static final String SERVER_INDEX = "SERVER_INDEX";
	
	/**
	 * 字符串常量，Intent中的数据
	 */
	public static final String DATA = "DATA";
	
	/**
	 * Action类型标识符，Action类型 为读到数据
	 */
	public static final String ACTION_READ_DATA = "ACTION_READ_DATA";
	
	/**
	 * Action类型标识符，Action类型为 搜索结束
	 */
	public static final String ACTION_DISCOVERY_FINISHED = "ACTION_DISCOVERY_FINISHED";
	
	/**
	 * Action类型标识符，Action类型为 开始搜索设备
	 */
	public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";
	
	/**
	 * Action：设备列表
	 */
	public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";
	
	/**
	 * Action：连接中断
	 */
	public static final String ACTION_ACL_DISCONNECTED = "ACTION_ACL_DISCONNECTED";
	
	/**
	 * Action：选择的用于连接的设备
	 */
	public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";
	
	/**
	 * Action：开启服务器
	 */
	public static final String ACTION_START_SERVER = "ACTION_STARRT_SERVER";
	
	/**
	 * Action：关闭后台Service
	 */
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
	
	/**
	 * Action：关闭后台Service1
	 */
	public static final String ACTION_STOP_SERVICE1 = "ACTION_STOP_SERVICE1";
	
	/**
	 * Action：到Service的数据
	 */
	public static final String ACTION_DATA_TO_SERVICE = "ACTION_DATA_TO_SERVICE";
	
	/**
	 * Action：到游戏业务中的数据
	 */
	public static final String ACTION_DATA_TO_GAME = "ACTION_DATA_TO_GAME";
	
	/**
	 * Action：发送文件的百分比
	 */
	public static final String ACTION_FILE_SEND_PERCENT = "ACTION_FILE_SEND_PERCENT";
	
	/**
	 * Action：接收文件的百分比
	 */
	public static final String ACTION_FILE_RECIVE_PERCENT = "ACTION_FILE_RECIVE_PERCENT";
	
	/**
	 * Action：连接成功
	 */
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";
	
	/**
	 * Action：连接错误
	 */
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";
	
	/**
	 * Message类型标识符，连接成功
	 */
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;
	
	/**
	 * Message：连接失败
	 */
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;
	
	/**
	 * Message：通讯失败
	 */
	public static final int MESSAGE_COMMUN_ERROR = 0x00000001;
	
	/**
	 * Message：读取到一个对象
	 */
	public static final int MESSAGE_READ_OBJECT = 0x00000004;
	
	/**
	 * Message：发送文件的百分比
	 */
	public static final int FILE_SEND_PERCENT = 0x00000005;
	
	/**
	 * Message：接收文件的百分比
	 */
	public static final int FILE_RECIVE_PERCENT = 0x00000006;
	
	/**
	 * 打开蓝牙功能
	 */
	public static void openBluetooth() {
		adapter.enable();
	}
	
	/**
	 * 关闭蓝牙功能
	 */
	public static void closeBluetooth() {
		adapter.disable();
	}
	
	/**
	 * 设置蓝牙发现功能
	 * @param duration 设置蓝牙发现功能打开持续秒数（值为0至300之间的整数）
	 */
	public static void openDiscovery(int duration) {
		if (duration <= 0 || duration > 300) {
			duration = 200;
		}
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
	}
	
	/**
	 * 停止蓝牙搜索
	 */
	public static void stopDiscovery() {
		adapter.cancelDiscovery();
	}
	
}
