package wfu.com.documentexpress.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Locale;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.utils.TextRecord;

/**
 * Created by sion on 2016/5/21.
 */
public class NFcExpressActivity  extends Activity implements NfcAdapter.CreateNdefMessageCallback,NfcAdapter.OnNdefPushCompleteCallback {
    private    NfcAdapter  nfcAdapter;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcsend_activity);
        NfcManager  nfcManager=(NfcManager)getSystemService(Context.NFC_SERVICE);
          nfcAdapter=nfcManager.getDefaultAdapter();
        if(nfcAdapter==null){

        }else{

            pendingIntent=PendingIntent.getActivities(this,0, new Intent[]{new Intent(this, getClass())},0);
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String    WIFI_MAC=getMacFromWifi(getApplicationContext());
        if(WIFI_MAC!=null){
            NdefMessage  ndefMessage=new NdefMessage(new NdefRecord[]{createTextRecord(WIFI_MAC)});
            return  ndefMessage;
        } else
        return  null;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Log.d("message", "complete");
        Intent intent=getIntent();
        intent.setClass(getApplicationContext(),WiFiDirectReceiverActivity.class);

    }



    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter!=null){
            nfcAdapter.enableForegroundDispatch(this,pendingIntent,null,null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter!=null){
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private static String getMacFromWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String mResult = wifiInfo.getMacAddress();
        Log.i("debug","Mac address(wifi): "+mResult);
        return mResult;
    }
    @Override
     public void onNewIntent(Intent intent){
        String  macDress= processIntent(intent);
        Intent  intent1=new Intent(getApplicationContext(),WifiDriectExpressActivity.class);
        intent1.putExtra("MacDress",macDress);
        startActivity(intent1);
        onDestroy();
     }

    public NdefRecord createTextRecord(String text) {
                 byte[] langBytes = Locale.CHINA.getLanguage().getBytes(
                Charset.forName("US-ASCII"));
                 Charset utfEncoding = Charset.forName("UTF-8");
                 byte[] textBytes = text.getBytes(utfEncoding);
                 int utfBit = 0;
                 char status = (char) (utfBit + langBytes.length);
                 byte[] data = new byte[1 + langBytes.length + textBytes.length];
                 data[0] = (byte) status;
                 System.arraycopy(langBytes, 0, data, 1, langBytes.length);
                 System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
                         textBytes.length);
                 NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                         NdefRecord.RTD_TEXT, new byte[0], data);
                 return record;
            }
    String processIntent(Intent intent) {
            Parcelable[] rawMsgs = intent
                        .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                 NdefMessage msg = (NdefMessage) rawMsgs[0];
                 String WIFI_MacDress = TextRecord.parse(msg.getRecords()[0]).getText();
        return WIFI_MacDress;
             }
}
