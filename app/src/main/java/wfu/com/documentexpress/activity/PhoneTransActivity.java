package wfu.com.documentexpress.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.libzxing.zxing.activity.CaptureActivity;
import wfu.com.documentexpress.utils.Base64;
import wfu.com.documentexpress.wifioperation.WifiAdmin;
import wfu.com.documentexpress.wifioperation.WifiApAdmin;

/**
 * Created by Lenovo on 2016/6/3.
 */
public class PhoneTransActivity extends BaseActivity {
    private Button send;
    private Button receive;
    private WifiAdmin wifiAdmin;
    private static final String special_prefix = "docexp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonetrans);
        initView();
        initEvent();

    }

    @Override
    protected void onResume() {
        closeAp(PhoneTransActivity.this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        closeAp(PhoneTransActivity.this);
        super.onDestroy();
    }

    private void initEvent() {
        closeAp(PhoneTransActivity.this);
        wifiAdmin.openWifi();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PhoneTransActivity.this, FileChooseActivity.class));
                finish();
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCameraIntent = new Intent(PhoneTransActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });
    }

    private void initView() {
        send = (Button) findViewById(R.id.activity_send);
        receive = (Button) findViewById(R.id.activity_receive);
        wifiAdmin = new WifiAdmin(PhoneTransActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = Base64.getFromBase64(bundle.getString("result"));
            if(scanResult.length()>=special_prefix.length()){
                if(special_prefix.equals(scanResult.substring(0,special_prefix.length()))){
                    String s[] = scanResult.split("[|]");
                    String ssid = s[0];
                    String password = s[1];
//            Log.e("1", ssid + " " + password);
//                    wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
//                    wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
                    Intent intent = new Intent(PhoneTransActivity.this, ReceiveActivity.class);
                    intent.putExtra("ssid",ssid);
                    intent.putExtra("password",password);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(PhoneTransActivity.this, "请扫描正确二维码", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(PhoneTransActivity.this,"请扫描正确二维码",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void closeAp(Context context){
        WifiApAdmin.closeWifiAp(context);
    }
}
