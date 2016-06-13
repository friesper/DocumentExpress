package wfu.com.documentexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import wfu.com.documentexpress.R;

/**
 * Created by Lenovo on 2016/5/31.
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
               // 为了减少代码使用匿名Handler创建一个延时的调用
            public void run() {
                Intent i = new Intent(SplashActivity.this,MainActivity.class);//通过Intent打开最终真正的主界面Main这个Activity
                SplashActivity.this.startActivity(i);    //启动Main界面
                SplashActivity.this.finish();    //关闭自己这个开场屏
            }


        }, 2000);
    }
}
