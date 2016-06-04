package wfu.com.documentexpress.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wfu.com.documentexpress.R;

/**
 * Created by yinxucun on 16-6-4.
 */
public class BlueTooth_list_adapter extends BaseAdapter {
    private Context context;
    private  int  ViewId;
    private  int  dpi;
    private ArrayList<BluetoothDevice> list;
    public BlueTooth_list_adapter(Context context, int  ViewId, ArrayList<BluetoothDevice> list){
        this.context=context;
        this.ViewId=ViewId;
        this.list=list;
    }

    @Override
    public int getCount() {
        if(list!=null){
            return  list.size();
        }
        else{
            Log.d("Tag","dir_list  is  null");
        return 0;
    }
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder  viewHolder;
        if (view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(ViewId, null);
            view.setTag(viewHolder);
            viewHolder.Bluetooth_info= (TextView) view.findViewById(R.id.bluetoot_devices_info);
            viewHolder.Bluetooth_name=(TextView)view.findViewById(R.id.bluetoot_devices_name);
        }
        else{
            viewHolder=(ViewHolder)view.getTag();
        }
        BluetoothDevice  mdevices=(BluetoothDevice)getItem(i);
        if(mdevices!=null){
            viewHolder.Bluetooth_name.setText(mdevices.getName());
            viewHolder.Bluetooth_info.setText(mdevices.getAddress());
        }
        return view;
    }



    private class   ViewHolder{
        private TextView Bluetooth_name;
        private TextView Bluetooth_info;

    }
}
