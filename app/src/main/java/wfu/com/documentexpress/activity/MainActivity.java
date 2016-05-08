package wfu.com.documentexpress.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
        import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.Dir_choose_listview_adapter;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

     private  Button  send_file=null;
      private   Button   recieve_file=null;
        private   SwitchCompat setting_sound=null;
    private SwitchCompat setting_virbration=null;
    private Spinner  express_mod=null;
    private  Button setting_dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        /*
        * 通过获取不同的分辨率适配不同的布局文件
        * */

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay();
        float density = metrics.density;
        if(density>1.5){
            setContentView(R.layout.activity_main_normal);
        }
        else  if(density <= 0.8) {
            setContentView(R.layout.activity_main_large);

        }
        else  setContentView(R.layout.activity_main_largest);
        /*
        * 绘制Materl  Design  的TOOLBar
        * */
        Toolbar  toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_view_headline_black_18dp);
        DrawerLayout mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle mActionBarDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,0, 0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            //hello,word
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
      //  ListView   setting_listView=(ListView)findViewById(R.id.setting_list);
        ArrayList<String>  arrayList=new ArrayList<String>();
        arrayList.add("设置");
       // SettingItemAdapter  settingItemAdapter  =new SettingItemAdapter(this,R.layout.setting_item,arrayList);
       // setting_listView.setAdapter(settingItemAdapter);
        send_file=(Button)findViewById(R.id.send_file);
        recieve_file=(Button)findViewById(R.id.recieve_file);
        send_file.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent  intent=new Intent(getApplicationContext(),FileChooseActivity.class);
              startActivity(intent);

          }
        });
    recieve_file.setOnClickListener(new View.OnClickListener() {
         @Override
        public void onClick(View v) {
             Intent  intent=new Intent(getApplicationContext(),FileChooseActivity.class);
             startActivity(intent);

     }
    });
        setting_sound=(SwitchCompat)findViewById(R.id.setting_sound);
        setting_virbration=(SwitchCompat)findViewById(R.id.setting_vibration);
        setting_sound.setOnCheckedChangeListener(this);
          setting_dir=(Button)findViewById(R.id.button_select_file_dir);
        setting_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),DirChosseActivity.class);

                intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

                startActivityForResult(intent, 1);

            }
        });


    }
    @Override
    protected void onActivityResult(int  requestCode,int  resultCode,Intent data) {
        switch (requestCode) {
            case 1:
          setting_dir.setText(  data.getStringExtra("dir_path"));
                Log.d("Tag",data.getStringExtra("dir_path"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_sound:
                Log.d("Tag","setting_sound");
                if (setting_sound.getCompoundPaddingRight()==0) {

                    Log.d("Tag","setting_sound_left");
                }
                else if(setting_sound.getCompoundPaddingLeft()==0) { Log.d("Tag","setting_sound_right");}
                break;

            case R.id.setting_vibration:
                if (setting_sound.getCompoundPaddingLeft() == 1) {

                }
                break;
        }
    }

}
