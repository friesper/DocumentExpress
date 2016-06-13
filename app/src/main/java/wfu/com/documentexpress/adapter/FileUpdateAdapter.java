package wfu.com.documentexpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
public class FileUpdateAdapter extends BaseAdapter  {
    private static final int TRANS_COMPLETE = 100;
    private List<FileUpdate> fileList;
    public boolean isShowCheckBox ;
    private int resourceId;
    private Context context;


   public int  childCheckedCount = 0;



    public static class ViewHolder{
        RoundRectImageView fileIcon;
        TextView transFileName;
        TextView currentSize;//需要更新
        TextView totalSize;
        NumberProgressBar currrentProgress; //需要动态更新
        TextView currentSpeed;//需要动态更新
        Button operation;
        LinearLayout showCb;
        public CheckBox cbMultiselect;

    }

    public FileUpdateAdapter(Context context, int ViewResourceId, List<FileUpdate> objects) {
        resourceId = ViewResourceId;
        fileList = objects;
        this.context = context;
        isShowCheckBox =false;
    }

    public FileUpdateAdapter(Context context, int ViewResourceId, List<FileUpdate> objects,SparseBooleanArray stateCheckedMap) {
        resourceId = ViewResourceId;
        fileList = objects;
        this.context = context;
        isShowCheckBox =false;
        for(int i=0;i<fileList.size();i++){
            stateCheckedMap.put(i,false);
        }
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
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
            viewHolder.operation = (Button) view.findViewById(R.id.operation_file);
            viewHolder.cbMultiselect = (CheckBox) view.findViewById(R.id.mut_cb_delete);
            viewHolder.showCb = (LinearLayout) view.findViewById(R.id.show_cb);
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
        viewHolder.operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button= (Button)view;
                button.setTextColor(Color.WHITE);
                openItWithFileType(i);
//                showInfo(i, view);
            }
        });
        //判断是否设置了多选框的选择状态
        if(isShowCheckBox){
            viewHolder.showCb.setVisibility(View.VISIBLE);
        }else{
            viewHolder.showCb.setVisibility(View.GONE);
        }
//多选框选择监听事件
        viewHolder.cbMultiselect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //选择计数
                if (isChecked) {
                    childCheckedCount++;
                } else {
                    childCheckedCount--;
                }
                //根据选择的数量决定底部FrameLayout编辑布局是否需要显示
                switch (childCheckedCount) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                }
            }
        });

        if(currentFile.getCurrentProgress()==TRANS_COMPLETE){
            viewHolder.currrentProgress.setVisibility(View.INVISIBLE);
            viewHolder.currentSpeed.setVisibility(View.VISIBLE);
            viewHolder.totalSize.setText(R.string.trans_complete);
        }
        return view;
    }

    private void openItWithFileType(int position){
        FileUpdate file = fileList.get(position);
        Intent intent = FileOperation.openFile(file.getPath());
        context.startActivity(intent);
    }

//   private void showInfo(int position,View parent){
//
//       if (popupWindow == null) {
//           LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//           popview = layoutInflater.inflate(R.layout.group_list, null);
//
//           lv_group = (ListView) popview.findViewById(R.id.lvGroup);
//           // 加载数据
//           List<String> groups = new ArrayList<String>();
//           groups.add("详情");
//           groups.add("重命名");
//           groups.add("复制");
//           groups.add("删除");
//           GroupAdapter groupAdapter = new GroupAdapter(context, groups);
//           lv_group.setAdapter(groupAdapter);
//           // 创建一个PopuWidow对象
//           popupWindow = new PopupWindow(popview, 300, 350);
//       }
//
//       // 使其聚集
//       popupWindow.setFocusable(true);
//       // 设置允许在外点击消失
//       popupWindow.setOutsideTouchable(true);
//
//       // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
//       popupWindow.setBackgroundDrawable(new BitmapDrawable());
////       WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
////       // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
////       int xPos = windowManager.getDefaultDisplay().getWidth() / 2
////               - popupWindow.getWidth() / 2;
////       Log.i("coder", "xPos:" + xPos);
//
//       popupWindow.showAtLocation((View) parent.getParent().getParent(), Gravity.CENTER,0,0);
//
//       lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//           @Override
//           public void onItemClick(AdapterView<?> adapterView, View view,
//                                   int pos, long id) {
//
//
//
//               if (popupWindow != null) {
//                   popupWindow.dismiss();
//               }
//           }
//       });
//   }


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
