package wfu.com.documentexpress.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.widget.Toolbar;
        import android.util.DisplayMetrics;
        import android.view.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

        import java.util.ArrayList;

        import wfu.com.documentexpress.R;
        import wfu.com.documentexpress.adapter.SettingItemAdapter;

public class MainActivity extends BaseActivity {

     private  Button  send_file=null;
      private   Button   recieve_file=null;

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
        ListView   setting_listView=(ListView)findViewById(R.id.setting_list);
        ArrayList<String>  arrayList=new ArrayList<String>();
        arrayList.add("设置");
        SettingItemAdapter  settingItemAdapter  =new SettingItemAdapter(this,R.layout.setting_item,arrayList);
        setting_listView.setAdapter(settingItemAdapter);
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



    }


}
