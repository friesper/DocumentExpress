package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;

/**
 * Created by yinxucun on 16-5-17.
 */
public class NetWorkExpress extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            NfcAdapter nfcAdapter=NfcAdapter.getDefaultAdapter(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
