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

public class MainActivityt extends BaseActivity {
    private Button send;
    private Button receive;
    private WifiAdmin wifiAdmin;
    private static final String special_prefix = "docexp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

    }

    @Override
    protected void onResume() {
        closeAp(MainActivityt.this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        closeAp(MainActivityt.this);
        super.onDestroy();
    }

    private void initEvent() {
        closeAp(MainActivityt.this);
        wifiAdmin.openWifi();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityt.this, FileChooseActivity.class));
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCameraIntent = new Intent(MainActivityt.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });
    }

    private void initView() {
        send = (Button) findViewById(R.id.activity_send);
        receive = (Button) findViewById(R.id.activity_receive);
        wifiAdmin = new WifiAdmin(MainActivityt.this);
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
                    wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
//            Log.e("1", ssid + " " + password);
                    wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
                    startActivity(new Intent(MainActivityt.this, ReceiveActivity.class));
                }else{
                    Toast.makeText(MainActivityt.this,"请扫描正确二维码",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MainActivityt.this,"请扫描正确二维码",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void closeAp(Context context){
        WifiApAdmin.closeWifiAp(context);
    }


}
