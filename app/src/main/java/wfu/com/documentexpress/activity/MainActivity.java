package wfu.com.documentexpress.activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import wfu.com.documentexpress.R;
import wfu.com.documentexpress.utils.SharepreferencesUtilSystemSettings;
import wfu.com.documentexpress.utils.WIFIDirectBroadCastReceiver;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private  Button  send_file=null;
    private  Button   recieve_file=null;
    private  SwitchCompat setting_sound=null;
    private  SwitchCompat setting_vibration=null;
    private  TextView  show_dir;
    private  Button   exit=null;
    public static   boolean  Vibration;
    public  static  boolean  Sound;
    public  static String dirPath;
    public static  String  express_mode;
    public  boolean   NFC_ENABLE;
    private Spinner  spinner=null;
    public NfcManager manager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * 通过获取不同的分辨率适配不同的布局文件
        * */
        initView();
        send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//开始发送文件的Activity
                Intent  intent=new Intent(getApplicationContext(),BluetoothSendActivity.class);
                startActivity(intent);

            }
        });
        recieve_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //开始接收文件的Activity
                Intent  intent=new Intent(getApplicationContext(),BluetoothReceiverActivity.class);
                startActivity(intent);

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
            case R.id.setting_sound: {
                Log.d("debug", "setting_sound");
                if (setting_sound.isChecked()) {
                    Sound = true;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(), "Sound", true);
                    Log.d("debug", "setting_sound"+Sound);
                } else {
                    Sound = false;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(), "Sound", false);
                    Log.d("debug", "setting_sound"+Sound);
                }
                break;
            }
            case R.id.setting_vibration: {
                Log.d("debug", "setting_sound");
                if (setting_vibration.isChecked()) {
                    Vibration = true;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(), "Vibration", true);
                    Log.d("debug", "setting_sound"+Vibration);
                } else {
                    Vibration = false;
                    SharepreferencesUtilSystemSettings.putValue(getApplicationContext(), "Vibration", false);
                    Log.d("debug", "setting_sound"+Vibration);
                }
            }
            break;
            default:break;
        }
    }

void  initView(){/*
            初始化界面，设置属性，文件路径
                */
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
    dirPath=SharepreferencesUtilSystemSettings.SETTING;

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

    setting_sound=(SwitchCompat)findViewById(R.id.setting_sound);//设置中的声音开关
    setting_vibration=(SwitchCompat)findViewById(R.id.setting_vibration);//～～～的震动开关
    setting_sound.setOnCheckedChangeListener(this);
    setting_vibration.setOnCheckedChangeListener(this);
    LinearLayout  select_dir=(LinearLayout)findViewById(R.id.select_dir);
    exit=(Button)findViewById(R.id.button_exit);
    show_dir=(TextView)findViewById(R.id.show_dir);//显示文件默认存储路径的TextView
    spinner=(Spinner)findViewById(R.id.select_mode);
    SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
    boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    if (isFirstRun)
    {
        Log.d("debug", "第一次运行");
        editor.putBoolean("isFirstRun", false);
        editor.commit();
        SharepreferencesUtilSystemSettings.putValue(this,"Sound",true);
        SharepreferencesUtilSystemSettings.putValue(this,"vibration",true);
    } else
    {
        Log.d("debug", "不是第一次运行");
        Sound=SharepreferencesUtilSystemSettings.getValue(getApplicationContext(), "Sound", true);
        Vibration=SharepreferencesUtilSystemSettings.getValue(getApplicationContext(), "Vibration", true);
        express_mode=SharepreferencesUtilSystemSettings.getValue(getApplicationContext(),"express_mode","");
        Log.d("debug",express_mode);
        if(Sound==true){
            setting_sound.setChecked(true);
        }else  setting_sound.setChecked(false);
        if(Vibration==true)
            setting_vibration.setChecked(true);
        else setting_vibration.setChecked(false);
        Log.d("debug", "setting_sound"+"...");
        Log.d("debug", "setting_sound"+"0000");
    }
    // 建立数据源
    String[] mItems = getResources().getStringArray(R.array.languages);
// 建立Adapter并且绑定数据源
    ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//绑定 Adapter到控件
    spinner .setAdapter(adapter);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String [] languagess=getResources().getStringArray(R.array.languages);
            express_mode=languagess[i];
            Log.d("debug",express_mode);
            SharepreferencesUtilSystemSettings.putValue(getApplicationContext(),"express_mode",express_mode);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });
    manager=(NfcManager)getSystemService(Context.NFC_SERVICE);
    NfcAdapter  nfcAdapter=manager.getDefaultAdapter();
    if(adapter==null){
        NFC_ENABLE=false;
        Toast.makeText(this,"NFC功能不存在",Toast.LENGTH_LONG);
    }
    else if(!adapter.isEnabled(0)){
        NFC_ENABLE=false;{
            Toast.makeText(this,"NFC功能未开启",Toast.LENGTH_LONG);
        }

    }
    else  NFC_ENABLE=true;
    exit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.exit(0);
            Log.d("debug","exit");
        }
    });

    // setting_dir=(Button)findViewById(R.id.button_select_file_dir);

    // initSwitchButton();


    select_dir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {//选择文件的点击监听事件

            Intent intent = new Intent(getApplicationContext(),DirChosseActivity.class);

            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

            startActivityForResult(intent, 1);

        }
    });
}

}