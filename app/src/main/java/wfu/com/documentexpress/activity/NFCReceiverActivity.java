package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;

/**
 * Created by sion on 2016/5/25.
 */
public class NFCReceiverActivity extends Activity implements NfcAdapter.OnNdefPushCompleteCallback,NfcAdapter.CreateNdefMessageCallback {
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return null;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

    }
}
