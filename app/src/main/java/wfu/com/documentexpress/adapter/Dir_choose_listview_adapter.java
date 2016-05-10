package wfu.com.documentexpress.adapter;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import wfu.com.documentexpress.R;

/**
 * Created by 14298 on 2016/5/8.
 */
public class Dir_choose_listview_adapter extends BaseAdapter {
    private Context context;
    private  int  ViewId;
    private  int  dpi;
    private ArrayList<File> dir_list;
    public Dir_choose_listview_adapter(Context context,int  ViewId,ArrayList<File> arrayList,int  dpi){
        this.context=context;
       this.ViewId=ViewId;
        this.dpi=dpi;
        this.dir_list=arrayList;
    }

    @Override
    public int getCount() {
        if(dir_list!=null){
            return   dir_list.size();
        }
        else
            Log.d("Tag","dir_list  is  null");
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return dir_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder  viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(ViewId, null);
            convertView.setTag(viewHolder);


            viewHolder.file_dir=(TextView)convertView.findViewById(R.id.dir_choose_filename);
            viewHolder.file_dir_icon=(ImageView)convertView.findViewById(R.id.dir_chosse_file_icon);

        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        File  file=(File)getItem(position);
    /*    if(position==0){
            viewHolder.file_dir.setText("返回上一级");
            viewHolder.file_dir_icon.setImageResource(R.drawable.file);


        }
      */ // else{
            if(file.isDirectory()){
                viewHolder.file_dir_icon.setImageResource(R.drawable.folder);
                viewHolder.file_dir.setText(file.getName());
            }
        else  if(file.isFile()) {
                viewHolder.file_dir_icon.setImageResource(R.drawable.file);
                viewHolder.file_dir.setText(file.getName());
               // viewHolder.file_dir.setClickable(false);
                //viewHolder.file_dir_icon.setClickable(false);
                convertView.setClickable(false);
                //}
            }
        return convertView;
    }
    class   ViewHolder{
        private TextView  file_dir;
        private ImageView  file_dir_icon;

    }
  }
