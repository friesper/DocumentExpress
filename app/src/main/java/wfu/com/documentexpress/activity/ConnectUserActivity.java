package wfu.com.documentexpress.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.libzxing.zxing.encoding.EncodingUtils;
import wfu.com.documentexpress.socketoperation.AcceptScanOk;
import wfu.com.documentexpress.utils.Base64;
import wfu.com.documentexpress.utils.LogUtil;
import wfu.com.documentexpress.wifioperation.WifiApAdmin;

/**
 * Created by Lenovo on 2016/4/26.
 */
public class ConnectUserActivity extends BaseActivity {
    //二维码显示界面，wifiap开启界面
    private static final String special_prefix = "docexp";
    private static final String special_flag = "|";
    private List<String> path_list;
    private String ssid=null;
    private String password=null;
    private ImageView imageView;
    private LinearLayout back;
    private String device_model = Build.MODEL; // 设备型号 。
    private static final String SCAN_OK = "SCAN_OK";
    private String connectedIp=null;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x125:
                    Intent intent = new Intent(ConnectUserActivity.this,SendActivity.class);
                    intent.putExtra("path_list", (Serializable) path_list);
                    intent.putExtra("targetIp", connectedIp);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectuser);
        Intent intent=getIntent();
        path_list = (List<String>)intent.getSerializableExtra("path_list");
        initView();
        initEvent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("1", "123");
                String temp = AcceptScanOk.recScanOkByUDP();
                String s[] = temp.split("[|]");
                LogUtil.e("1",temp);
                connectedIp = s[1];
                LogUtil.e("1",connectedIp);
                if(s[0].equals(SCAN_OK)&&connectedIp!=null){
                    handler.sendEmptyMessage(0x125);
                }
            }
        }).start();
    }
    private void initEvent() {
        closeAp(ConnectUserActivity.this);
        randomInformationGeneration(ssid, password);
        startAp(ConnectUserActivity.this, ssid, password);
        qrCodeGeneration(ssid, password);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.qrcode_img);
        back = (LinearLayout) findViewById(R.id.back_layout_connectuser);
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }
    private void showDialog() {
        new AlertDialog.Builder(ConnectUserActivity.this).setTitle("注意！")//设置对话框标题
                .setMessage("返回后将取消传送，是否确定？")//设置显示的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        closeAp(ConnectUserActivity.this);
                        finish();
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                dialog.dismiss();
            }
        }).show();//在按键响应事件中显示此对话框
    }
    private void qrCodeGeneration(String ssid, String password) {
        if(ssid!=null&&password!=null){
            String qrInfo = ssid+special_flag+password;
//            LogUtil.e("1",qrInfo);
            Bitmap qrCodeBitmap = EncodingUtils.createQRCode(Base64.getBase64(qrInfo),500, 500,
                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
                           );
            imageView.setImageBitmap(qrCodeBitmap);

        }
    }

    private void randomInformationGeneration(String ssid, String password) {
        int Max = 13;
        int Min = 8;
        Random rand = new Random();
        int randNumber = rand.nextInt(Max - Min + 1) + Min;
        this.ssid = special_prefix+device_model;
        this.password = getStringRandom(randNumber);

    }

    private void startAp(Context context, String ssid, String password) {
        if(ssid!=null&&password!=null){
            WifiApAdmin wifiAp = new WifiApAdmin(context);
            wifiAp.startWifiAp(ssid, password);
        }
    }
    private void closeAp(Context context){
        WifiApAdmin.closeWifiAp(context);
    }

    //生成随机数字和字母,
    public String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

}
