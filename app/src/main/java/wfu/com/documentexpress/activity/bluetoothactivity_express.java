package wfu.com.documentexpress.activity;

import java.util.Date;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.Server.BluetoothTools;
import wfu.com.documentexpress.Server.SelectFileActivity;
import wfu.com.documentexpress.Server.TransmitBean;


public class bluetoothactivity_express extends BaseActivity {

	public static final int RESULT_CODE = 1000;    //选择文件   请求码
	public static final String SEND_FILE_NAME = "sendFileName";
	private  TextView serversText;
	private  EditText chatEditText;
	private EditText sendEditText;
	private  Button sendBtn;
	private Button filesendBtn;
	Button mSelectFileBtn;
	TextView mSendFileNameTV;
	private ProgressDialog spDialog; 
	private ProgressDialog rpDialog;
	
	
	@Override
	protected void onStart() {
		
		//开启后台service  因为之前关闭后台service，此处开启只是调用后台service的onStart方法，可以去掉这里的startService
//		Intent startService = new Intent(ClientActivity2.this, BluetoothClientService.class);
//		startService(startService);
			
		//注册BoradcasrReceiver
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);
		intentFilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
		intentFilter.addAction(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
		registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client2);		
		spDialog=new ProgressDialog(bluetoothactivity_express.this);
		rpDialog=new ProgressDialog(bluetoothactivity_express.this);
	//	serversText = (TextView)findViewById(R.id.clientServersText);
		chatEditText = (EditText)findViewById(R.id.clientChatEditText);
		sendEditText = (EditText)findViewById(R.id.clientSendEditText);	
		mSendFileNameTV = (TextView) findViewById(R.id.sendFileTV);
		mSelectFileBtn = (Button) findViewById(R.id.cancelSearchBtn);
		mSelectFileBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(bluetoothactivity_express.this, SelectFileActivity.class);
				startActivityForResult(intent, bluetoothactivity_express.RESULT_CODE);
			}
		});
		


		
		filesendBtn = (Button)findViewById(R.id.fileSendBtn);
		filesendBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				/*发送文件  由于Intent无法传递很多数据，所以先将文件路径广播给BluetoothClientService，
				 * *由该Service读取文件后通过对象流发送给远程蓝牙设备
				 */
				if ("".equals(mSendFileNameTV.getText().toString().trim())) {
					Toast.makeText(bluetoothactivity_express.this, "未选择文件", Toast.LENGTH_SHORT).show();
				} else {
					TransmitBean transmit = new TransmitBean();
					String path=mSendFileNameTV.getText().toString();
					String filename=path.substring(path.lastIndexOf("/")+1,path.length());
					transmit.setFilename(filename);
					transmit.setFilepath(path);
					Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
					sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
					sendBroadcast(sendDataIntent);
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_CODE){
			//请求为 "选择文件"
			try {
				//取得选择的文件名
				String sendFileName = data.getStringExtra(SEND_FILE_NAME);
				mSendFileNameTV.setText(sendFileName);
			} catch (Exception e) {				
			}
		}	
	}
	
	//广播接收器
	public  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {	
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();		
//			if (BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)) {//连接成功
//				serversText.setText("连接成功");
//				sendBtn.setEnabled(true);				
//			}
			if (BluetoothTools.ACTION_CONNECT_ERROR.equals(action)) {//连接失败
				spDialog.dismiss();
				rpDialog.dismiss();
				Toast.makeText(bluetoothactivity_express.this, "通讯失败", Toast.LENGTH_LONG).show();
			//	serversText.setText("通讯失败");
			//	sendBtn.setEnabled(true);				
			}
			if (BluetoothTools.ACTION_DATA_TO_GAME.equals(action)) {//接收数据
				TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				String msg = "";					
				if(transmit.getFilename()!=null&&!"".equals(transmit.getFilename())){
					msg = "receive file from remote " + new Date().toLocaleString() + " :\r\n" + transmit.getFilename() + "\r\n";
				}else{
					msg = "receive message from remote " + new Date().toLocaleString() + " :\r\n" + transmit.getMsg() + "\r\n";
				}
				chatEditText.append(msg);
			}
			if (BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)) {//发送文件百分比
				TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);			  
				spDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
				spDialog.setTitle("提示");  
				spDialog.setIcon(R.drawable.icon);  
				if(!"0".equals(data.getTspeed())){
					spDialog.setMessage("文件发送速度:"+data.getTspeed()+"k/s");  
				}
				spDialog.setMax(100);  
				spDialog.setProgress(Integer.valueOf(data.getUppercent())); 
				spDialog.setIndeterminate(false);  
				spDialog.setCancelable(true);  
//				spDialog.setButton("取消", new DialogInterface.OnClickListener(){  
//				    @Override  
//				    public void onClick(DialogInterface dialog, int which) {  
//				        dialog.cancel();  			          
//				    }      
//				});				
				spDialog.show();
				if(Integer.valueOf(data.getUppercent())==100){				
					spDialog.dismiss();
					spDialog.setProgress(0);  
				}			
			}
			if (BluetoothTools.ACTION_FILE_RECIVE_PERCENT.equals(action)) {//接收文件百分比
				TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);				  
				rpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
				rpDialog.setTitle("提示");  
				rpDialog.setIcon(R.drawable.icon);   
				if(!"0".equals(data.getTspeed())){
					rpDialog.setMessage("文件接收速度:"+data.getTspeed()+"k/s");  
				}
				rpDialog.setMax(100);  
				rpDialog.setProgress(Integer.valueOf(data.getUppercent()));   
				rpDialog.setIndeterminate(false);  
				rpDialog.setCancelable(true);  
			
				rpDialog.show();
				if(Integer.valueOf(data.getUppercent())==100){				
					rpDialog.dismiss();
					rpDialog.setProgress(0);  
				}			
			}
		}
	};

//	@Override
//	public void onBackPressed() {
//	// 这里处理逻辑代码，注意：该方法仅适用于2.0或更新版的sdk
//		//关闭后台Service 
//		Intent stopService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
//		sendBroadcast(stopService);
//		unregisterReceiver(broadcastReceiver);	 
//		super.onBackPressed();
//	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);		
		super.onDestroy();
	}

	@Override
	protected void onStop() {		
		super.onStop();
	}
}
