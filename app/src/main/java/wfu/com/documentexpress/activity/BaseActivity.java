package wfu.com.documentexpress.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import wfu.com.documentexpress.utils.ActivityCollector;

/**
 * Created by Lenovo on 2016/4/13.
 */
public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    //public abstract void  onCheckedChangeListener(CompoundButton buttonView, boolean isChecked);
}
