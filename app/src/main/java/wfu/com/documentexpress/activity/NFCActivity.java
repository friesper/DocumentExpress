package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;

/**
 * Created by sion on 2016/5/21.
 */
public class NFCActivity  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NfcManager  nfcManager=(NfcManager)getSystemService(Context.NFC_SERVICE);
        NfcAdapter  nfcAdapter=nfcManager.getDefaultAdapter();



    }
}
