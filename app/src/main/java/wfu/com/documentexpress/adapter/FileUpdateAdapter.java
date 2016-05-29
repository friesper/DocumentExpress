package wfu.com.documentexpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.model.FileUpdate;
import wfu.com.documentexpress.utils.FileOperation;
import wfu.com.documentexpress.view.RoundRectImageView;

/**
 * Created by Lenovo on 2016/5/25.
 */
public class FileUpdateAdapter extends BaseAdapter {
    private static final int TRANS_COMPLETE = 100;
    private List<FileUpdate> fileList;
    private int resourceId;
    private Context context;
    private static class ViewHolder{
        RoundRectImageView fileIcon;
        TextView transFileName;
        TextView currentSize;//需要更新
        TextView totalSize;
        NumberProgressBar currrentProgress; //需要动态更新
        TextView currentSpeed;//需要动态更新

    }

    public FileUpdateAdapter(Context context, int ViewResourceId, List<FileUpdate> objects) {
        resourceId = ViewResourceId;
        fileList = objects;
        this.context = context;
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
    public FileUpdate getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FileUpdate currentFile = fileList.get(i);
        ViewHolder viewHolder=null;
        if(view==null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.fileIcon = (RoundRectImageView) view.findViewById(R.id.file_icon);
            viewHolder.transFileName  = (TextView) view.findViewById(R.id.trans_file_name);
            viewHolder.currentSize = (TextView) view.findViewById(R.id.current_size);
            viewHolder.totalSize = (TextView) view.findViewById(R.id.total_size);
            viewHolder.currrentProgress = (NumberProgressBar) view.findViewById(R.id.current_progress);
            viewHolder.currentSpeed = (TextView) view.findViewById(R.id.current_speed);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = currentFile.getName();
        viewHolder.transFileName.setText(currentFile.getName());
        viewHolder.fileIcon.setImageResource(getResIdFromName(name));
        viewHolder.currentSize.setText(currentFile.getCurrentSize());
        viewHolder.totalSize.setText(currentFile.getTotalSize());
        viewHolder.currrentProgress.setProgress(currentFile.getCurrentProgress());
        viewHolder.currentSpeed.setText(currentFile.getCurrentSpeed());
        if(currentFile.getCurrentProgress()==TRANS_COMPLETE){
            viewHolder.currrentProgress.setVisibility(View.INVISIBLE);
            viewHolder.totalSize.setText(R.string.trans_complete);
        }
        return view;
    }

    private int getResIdFromName(String name) {
        switch (FileOperation.getSuffix(name)){
            case "mp3":
            case "wma":
            case "wav":
            case "aac":
                return R.drawable.music ;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return R.drawable.image_picture ;
            case "apk":
                return R.drawable.apk_file ;
            case "zip":
                return R.drawable.winrarsz ;
            default:
                return R.drawable.file;
        }
    }
}
