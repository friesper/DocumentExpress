package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.libzxing.zxing.activity.CaptureActivity;
import wfu.com.documentexpress.utils.Base64;
import wfu.com.documentexpress.wifioperation.WifiAdmin;

/**
 * Created by Lenovo on 2016/5/5.
 */
public class WiFiConnectActivity extends Activity {
    //wifi连接测试界面
    private Button open;
    private Button connect;
    WifiAdmin wifiAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonnect);
        open = (Button) findViewById(R.id.openAp);
        connect = (Button) findViewById(R.id.connectAp);
        wifiAdmin = new WifiAdmin(WiFiConnectActivity.this);
        wifiAdmin.openWifi();
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCameraIntent = new Intent(WiFiConnectActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = Base64.getFromBase64(bundle.getString("result"));
            String s[] = scanResult.split("[|]");
            String ssid = s[0];
            String password = s[1];
            wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
//            Log.e("1", ssid + " " + password);
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
        }
    }
}
