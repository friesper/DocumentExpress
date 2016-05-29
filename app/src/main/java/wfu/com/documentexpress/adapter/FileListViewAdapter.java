package wfu.com.documentexpress.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.model.SDFile;
import wfu.com.documentexpress.utils.FileSizeUtil;
import wfu.com.documentexpress.view.RoundRectImageView;

/**
 * Created by Lenovo on 2016/4/13.
 */
public class FileListViewAdapter extends BaseAdapter {
    private List<SDFile> fileList;
    private int resourceId;
    private Context context;
    private Bitmap musicIcon;

    private static class ViewHolder{
        CheckBox isCheck;
        RoundRectImageView appIcon;
        TextView fileName;
        TextView fileSize;
        TextView fileModifyTime;
    }

    public FileListViewAdapter(Context context, int ViewResourceId, List<SDFile> objects) {
        resourceId = ViewResourceId;
        fileList = objects;
        this.context = context;
        musicIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
    }
    @Override
    public int getCount() {
        if(fileList!=null){
            return fileList.size();
        }else{
            return 0;
        }
    }

    @Override
    public SDFile getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SDFile file = getItem(i);
        ViewHolder viewHolder=null;
        if(view==null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.isCheck = (CheckBox) view.findViewById(R.id.ischeck);
            viewHolder.appIcon = (RoundRectImageView) view.findViewById(R.id.app_icon);
            viewHolder.fileName = (TextView) view.findViewById(R.id.file_name);
            viewHolder.fileSize = (TextView) view.findViewById(R.id.file_size);
            viewHolder.fileModifyTime = (TextView) view.findViewById(R.id.file_modify_time);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        if(file.isCheck()){
            viewHolder.isCheck.setChecked(true);
        }else{
            viewHolder.isCheck.setChecked(false);
        }
        if(file.getFileType().equals("root")||file.getFileType().equals("directory")){
            viewHolder.isCheck.setVisibility(View.GONE);
        }else{
            viewHolder.isCheck.setVisibility(View.VISIBLE);
        }
        viewHolder.appIcon.setImageBitmap(file.getImage());
        viewHolder.fileName.setText(file.getName());
        if(file.getFileType().equals("file")||file.getFileType().equals("")||file.getFileType().equals("music")){
            viewHolder.fileSize.setVisibility(View.VISIBLE);
            viewHolder.fileSize.setText(FileSizeUtil.getAutoFileOrFilesSize(file.getFileAbsAddress()));
        }else{
            viewHolder.fileSize.setVisibility(View.INVISIBLE);
        }
        if(file.getFileType().equals("music")){
            viewHolder.appIcon.setImageBitmap(musicIcon);
        }else{
            viewHolder.appIcon.setImageBitmap(file.getImage());
        }

        viewHolder.fileModifyTime.setText(file.getModificationTime());

        return view;

    }
}
