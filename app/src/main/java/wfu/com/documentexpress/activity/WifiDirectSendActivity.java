package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileUpdateAdapter;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.utils.WifiP2pTransterClient;

/**
 * Created by yinxucun on 16-6-2.
 */
public class WifiDirectSendActivity extends Activity {

    private LinearLayout cancleTrans;
    private ListView transList;
    private Button interrupt_trans;
    private TextView title;
    private FileUpdateAdapter adapter;

    private  List<String>  file_list;
    private  List<FileUpdate>  files_list;

    WifiP2pTransterClient wifiP2pTransterClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendfile);
        initEvent();
        wifiP2pTransterClient.service();

    }
   private  void initEvent(){
       Intent  intent=getIntent();
       files_list = new ArrayList<FileUpdate>();
       file_list=(List<String>)intent.getSerializableExtra("path_list");
       for(int i = 0 ; i < files_list.size() ;i++){
           files_list.add(files_list.get(i));
       }

       wifiP2pTransterClient=new WifiP2pTransterClient(file_list);
   }
}
