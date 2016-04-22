package wfu.com.documentexpress.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sion on 2016/4/22.
 */
public class SettingItemAdapter extends BaseAdapter {
    private  int  number;
    private  int  ViewId;
    private   Context  context;
    private   ArrayList<String>   arrayList;
    private static class ViewHolder{
        CheckBox isCheck;
        wfu.com.documentexpress.view.RoundRectImageView appIcon;
        TextView fileName;
        TextView fileSize;
        TextView fileModifyTime;
    }
    public  SettingItemAdapter(Context context,int   ViewId,  ArrayList<String>  arrayList   ){
        this.ViewId=ViewId;
        this.context=context;
    }
    @Override
    public int getCount() {
        if(arrayList!=null)
        return arrayList.size();
        else   return  0;
    }

    @Override
    public Object getItem(int position) {
        return  arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }
}
