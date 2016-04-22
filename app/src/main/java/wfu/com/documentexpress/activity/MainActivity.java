package wfu.com.documentexpress.activity;
        import android.os.Bundle;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.widget.Toolbar;
        import android.util.DisplayMetrics;
        import android.view.View;

import android.os.Bundle;

        import wfu.com.documentexpress.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

}
