package wfu.com.documentexpress.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import wfu.com.documentexpress.R;


/**
 * 适配器管理器
 * @author 210001001427
 *
 */
public class AdapterManager {
	private Context mContext;
	private FileListAdapter mFileListAdapter;    //文件列表adapter
	private List<BluetoothDevice> mDeviceList;   //设备集合
	private List<File> mFileList;    //文件集合
	private Handler mainHandler;   //主线程Handler
	
	public AdapterManager(Context context){
		this.mContext = context;
	}
	

	
	/**
	 * 取得文件列表adapter
	 * @return
	 */
	public FileListAdapter getFileListAdapter(){
		System.out.print("11111111111");
		Log.v("调试" , "getFileListAdapter");
		if(null == mFileListAdapter){
			mFileList = new ArrayList<File>();
			mFileListAdapter = new FileListAdapter(mContext, mFileList, R.layout.bluetooth_file_list_item);
		}	
		System.out.print("222222222222222");
		return mFileListAdapter;
	}
	

	/**
	 * 清空设备列表
	 */
	public void clearDevice(){
		if(null != mDeviceList){
			mDeviceList.clear();
		}
	}
	
	/**
	 * 添加设备
	 * @param bluetoothDevice
	 */
	public void addDevice(BluetoothDevice bluetoothDevice){
		mDeviceList.add(bluetoothDevice);
	}
	
	/**
	 * 更新设备信息
	 * @param listId
	 * @param bluetoothDevice
	 */
	public void changeDevice(int listId, BluetoothDevice bluetoothDevice){
		mDeviceList.remove(listId);
		mDeviceList.add(listId, bluetoothDevice);
	}
	
	/**
	 * 更新文件列表
	 * @param path
	 */
	public void updateFileListAdapter(String path){
		mFileList.clear();
		Log.v("打印路径" , "AdapterManager："+path);

		mFileList.addAll(FileUtil.getFileList(path));
		if(null == mainHandler){
			mainHandler = new Handler(mContext.getMainLooper());
		}
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				mFileListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 取得设备列表
	 * @return
	 */
	public List<BluetoothDevice> getDeviceList() {
		return mDeviceList;
	}
}
