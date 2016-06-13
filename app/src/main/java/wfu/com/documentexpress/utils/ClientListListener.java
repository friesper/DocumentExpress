package wfu.com.documentexpress.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.Server.BluetoothTools;

/**
 * Created by yinxucun on 16-6-11.
 */
public class ClientListListener {
    public BluetoothAdapter bluetooth;
    public ArrayList<BluetoothDevice> unbondDevices; // 用于存放未配对蓝牙设备
    public ArrayList<BluetoothDevice> bondDevices;  // 用于存放已配对蓝牙设备
    public ListView unbondDevicesListView ;
    public ListView bondDevicesListView;
    public Activity ClientActivity;

    public ClientListListener(Activity ClientActivity,BluetoothAdapter bluetooth,ArrayList<BluetoothDevice> unbondDevices,
                              ArrayList<BluetoothDevice> bondDevices,ListView unbondDevicesListView,ListView bondDevicesListView){
        this.ClientActivity = ClientActivity;
        this.bluetooth = bluetooth;
        this.unbondDevices = unbondDevices;
        this.bondDevices = bondDevices;
        this.unbondDevicesListView = unbondDevicesListView;
        this.bondDevicesListView = bondDevicesListView;
    }
    public void addUnbondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = unbondDevices.size();
        System.out.println("未绑定设备数量：" + count);
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", this.unbondDevices.get(i).getName());
            data.add(map);// 把item项的数据加到data中
        }
        String[] from = { "deviceName" };
        int[] to = { R.id.device_name };
        SimpleAdapter simpleAdapter = new SimpleAdapter(ClientActivity, data,R.layout.unbonddevice_item, from, to);
        // 把适配器装载到listView中
        this.unbondDevicesListView.setAdapter(simpleAdapter);
        // 为每个item绑定监听，用于设备间的配对
        this.unbondDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                try {
                    bluetooth.cancelDiscovery();
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(unbondDevices.get(arg2));
                    // 将绑定好的设备添加的已绑定list集合
                    bondDevices.add(unbondDevices.get(arg2));
                    // 将绑定好的设备从未绑定list集合中移除
                    unbondDevices.remove(arg2);
                    addBondDevicesToListView();
                    addUnbondDevicesToListView();
                } catch (Exception e) {
                    Toast.makeText(ClientActivity, "配对失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 添加已绑定蓝牙设备到ListView
     */
    public void addBondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = bondDevices.size();

        if(count==0){//刚打开ACTIVITY 尚未开始搜索
            //获得已配对的远程蓝牙设备的集合
            Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
            if(devices.size()>0){
                for(Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext();){
                    BluetoothDevice device = (BluetoothDevice)it.next();
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("deviceName", device.getName()+ "|" +device.getAddress());
                    bondDevices.add(device);
                    data.add(map);// 把item项的数据加到data中
                }
            }
        }else{
            for (int i = 0; i < count; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("deviceName", this.bondDevices.get(i).getName()+ "|" +this.bondDevices.get(i).getAddress());
                data.add(map);// 把item项的数据加到data中
            }
        }
        String[] from = { "deviceName" };
        int[] to = { R.id.device_name };
        SimpleAdapter simpleAdapter = new SimpleAdapter(ClientActivity, data, R.layout.bonddevice_item, from, to);
        // 把适配器装载到listView中
        this.bondDevicesListView.setAdapter(simpleAdapter);
        this.bondDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                BluetoothDevice device = bondDevices.get(arg2);
                Intent intent = new Intent();

                Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
                selectDeviceIntent.putExtra(BluetoothTools.DEVICE, device);
                ClientActivity.sendBroadcast(selectDeviceIntent);
                intent.setClassName(ClientActivity,"wfu.com.documentexpress.activity.bluetoothactivity_express");
                intent.putExtra("deviceAddress", device.getAddress());
                ClientActivity.startActivity(intent);
                ClientActivity.finish();
            }
        });
        this.bondDevicesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                try {
                    Method createBondMethod = BluetoothDevice.class.getMethod("removeBond");
                    createBondMethod.invoke(bondDevices.get(arg2));
                } catch (Exception e) {
                    Toast.makeText(ClientActivity, "取消配对失败！", Toast.LENGTH_SHORT).show();
                }
                if(bondDevices.size()>0){//如果在没有搜索前直接取消已有配对,此时bondDevices为空则报错
                    // 将绑定好的设备添加的已绑定list集合
                    unbondDevices.add(bondDevices.get(arg2));
                    // 将绑定好的设备从未绑定list集合中移除
                    bondDevices.remove(arg2);
                }
                addBondDevicesToListView();
                addUnbondDevicesToListView();
                return true;
            }
        });
    }
}
