package wfu.com.documentexpress.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import wfu.com.documentexpress.R;

/**
 * Created by sion on 2016/4/22.、】
 *
 * 设置列表的ListView  适配器
 */
public class SettingItemAdapter extends BaseAdapter {


    private LayoutInflater mInflater;
    private  int  ViewId;
    private   Context  context;
    private   ArrayList<String>   arrayList;
    public static class ViewHolder{
        public  TextView  title;
        public    SwitchCompat  switchCompat;
    }
    public  SettingItemAdapter(Context context,int   ViewId,  ArrayList<String>  arrayList   ){
        this.ViewId=ViewId;
        this.context=context;
        this.arrayList=arrayList;

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
        ViewHolder  viewHolder;
        if(convertView==null) {
            convertView = mInflater.from(context).inflate(R.layout.setting_item, null);
          viewHolder=new ViewHolder();
            if(arrayList.get(position).equals(""))
            viewHolder.title=(TextView)convertView.findViewById(R.id.setting_item_text);
            viewHolder.switchCompat=(SwitchCompat)convertView.findViewById(R.id.setting_item_button);
            viewHolder.title.setText(arrayList.get(position));
            convertView.setTag(viewHolder);
        }
        else{
              viewHolder=(ViewHolder)convertView.getTag();
            viewHolder.title.setText(arrayList.get(position));


        }
        return convertView;
    }

}
