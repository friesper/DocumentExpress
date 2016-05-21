package wfu.com.documentexpress.activity;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.Dir_choose_listview_adapter;

/**
 * Created by 14298 on 2016/5/8.
 */
public class DirChosseActivity   extends   BaseActivity {
    ListView   dir_list;
    ArrayList<File>  files;
    File  foder= Environment.getExternalStorageDirectory();
    File   foder_now;
    Dir_choose_listview_adapter  dir_choose_listview_adapter;

    private Button   dir_sure;
    public int densityDpi;// 屏幕密度，单位为dpi
    String dirPath ;


    @Override
   protected   void  onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dir_chosse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.app_name);
        initView();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        densityDpi = dm.densityDpi;


        dir_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     foder_now=(File)dir_choose_listview_adapter.getItem(position);
                if (foder_now.isDirectory()) {
                    foder=(File)dir_choose_listview_adapter.getItem(position);
                    initData(foder_now);

                    Log.d("Tag","foder is  Dir");

                } else {

                }
            }
        });

        dir_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dirPath =foder.getPath();
               // Bundle   bundle=new Bundle();
                //bundle.putString("dir_path",dirPath);
                Intent  intent =new Intent();
                intent.putExtra("dir_path",dirPath);

                setResult(1,intent);

                finish();
            }
        });
    }

    private void initData(File folder) {
        ArrayList<File> files = new ArrayList<File>();
        File[] filterFiles = folder.listFiles();
        if (null != filterFiles && filterFiles.length > 0) {
            files.clear();
            for (File file : filterFiles) {
                files.add(file);
            }
        }
        dir_choose_listview_adapter =new Dir_choose_listview_adapter(getApplicationContext(),R.layout.chosse_dir_adapter_item,files,densityDpi);
        dir_list.setAdapter(dir_choose_listview_adapter);

    } private void  initView(){
          dir_sure=(Button)findViewById(R.id.chosse_dir_sure);
          files  =new ArrayList<File>();
        dir_list=(ListView)findViewById(R.id.setting_dir_chosse);
         initData(foder);

    }


    @Override
    public void onBackPressed() {
        if(foder.getParentFile()!=null){
            foder=foder.getParentFile();
            initData(foder);
        } else
            finish();


    }

}
