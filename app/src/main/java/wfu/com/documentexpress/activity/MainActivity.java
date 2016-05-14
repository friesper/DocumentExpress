package wfu.com.documentexpress.activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.utils.SharepreferencesUtilSystemSettings;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private  Button  send_file=null;
    private  Button   recieve_file=null;
    private  SwitchCompat setting_sound=null;
    private  SwitchCompat setting_virbration=null;
    private  TextView  show_dir;
    public static   boolean  Vibration;
    public  static  boolean  Sound;
    public  static String dirPath;
    public static   int  LOOD =1 ;
    public  static    PACKAGE_NAME=
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
        PackageInfo info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
        int currentVersion = info.versionCode;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt(VERSION_KEY, 0);
        if (currentVersion > lastVersion) {
            //如果当前版本大于上次版本，该版本属于第一次启动
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
            prefs.edit().putInt(VERSION_KEY,currentVersion).commit();
        }
        dirPath=SharepreferencesUtilSystemSettings.SETTING;
         File file=new File(dirPath);
        if(!file.isFile()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(LOOD!=1) {
            SharepreferencesUtilSystemSettings.getValue(getApplicationContext(), "Sound", Sound);
            SharepreferencesUtilSystemSettings.getValue(getApplicationContext(), "Vibartion", Vibration);
        }//SharepreferencesUtilSystemSettings  sh=new SharepreferencesUtilSystemSettings(dirPath);
        //Sound=sh.
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
        send_file=(Button)findViewById(R.id.send_file);
        recieve_file=(Button)findViewById(R.id.recieve_file);
        send_file.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {//开始发送文件的Activity
              Intent  intent=new Intent(getApplicationContext(),FileChooseActivity.class);
              startActivity(intent);

          }
        });
    recieve_file.setOnClickListener(new View.OnClickListener() {
         @Override
        public void onClick(View v) {  //开始接收文件的Activity
             Intent  intent=new Intent(getApplicationContext(),FileChooseActivity.class);
             startActivity(intent);

     }
    });
        setting_sound=(SwitchCompat)findViewById(R.id.setting_sound);//设置中的声音开关
        setting_virbration=(SwitchCompat)findViewById(R.id.setting_vibration);//～～～的震动开关
        setting_sound.setOnCheckedChangeListener(this);
        show_dir=(TextView)findViewById(R.id.show_dir);//显示文件默认存储路径的TextView
       // setting_dir=(Button)findViewById(R.id.button_select_file_dir);
        LinearLayout  select_dir=(LinearLayout)findViewById(R.id.select_dir);
        initSwitchButton();
        select_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//选择文件的点击监听事件

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
                dirPath=data.getStringExtra("dir_path");
                show_dir.setText(  data.getStringExtra("dir_path"));
                Log.d("Tag",dirPath);
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
                if (setting_sound.isChecked()) {

                    Log.d("Tag","setting_sound_ischeched");
                    Sound=true;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(),"Sound",true);
                }
                else  {
                    Sound=false;
                    Log.d("Tag","setting_sound_unischeked");
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(),"Sound",false);
                }
                break;

            case R.id.setting_vibration:
                if (setting_virbration.isChecked()) {
                    Vibration=true;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(),"Vibration",true);

                }
                else {
                    Vibration=false;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(),"Vibration",false);
                }
                break;
        }
    }


    private   void initSwitchButton  (){
        if  (Sound==false){
            setting_sound.setChecked(false);
            Log.d("Tag","setting  setting_sound");
        }
        else  setting_sound.setClickable(true);
        if(Vibration==false){
            setting_virbration.setClickable(false);
        }
        else  setting_virbration.setChecked(true);
    }
    @Override
    protected   void onDestroy(){
        LOOD=2;


    }
}
