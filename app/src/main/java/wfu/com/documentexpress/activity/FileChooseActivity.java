package wfu.com.documentexpress.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileListViewAdapter;
import wfu.com.documentexpress.adapter.ImageViewAdapter;
import wfu.com.documentexpress.model.SDFile;
import wfu.com.documentexpress.utils.DateTransformUtil;
import wfu.com.documentexpress.utils.FileOperation;
import wfu.com.documentexpress.view.AnimTabsView;

/**
 * Created by Lenovo on 2016/4/13.
 */
public class FileChooseActivity extends FragmentActivity {
    private static ListView fileList;
    private static GridView imageList;
    private static FileListViewAdapter fileAdapter;
    private static ImageViewAdapter imageAdapter;
    private static PackageManager pm;
    private static Context context;
    private static List<SDFile> applist;
    private static List<SDFile> musiclist;
    private static List image;
    private static File inSd;  //内部存储
    private static File outSd;
    private static List<SDFile> file_list;
    private static TextView address;
    private static String currentPage="picture";
    //当前父文件夹
    private static File currentParent;
    //当前路径下的所有文件的文件数组
    private static File[] currentFiles;
    private static final String TAG = "FileChooseMainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_filechoose);
        context = getApplicationContext();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                applist = getAppInfo();
                musiclist =getMusic();
            }
        }).start();
        initView();
        initEvent();

    }

    private void initEvent() {
        imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                imageAdapter.chiceState(position);
            }
        });
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.ischeck);
                if (cb.isChecked()) {
                    cb.setChecked(false);
                } else {
                    cb.setChecked(true);
                }
                switch (currentPage) {
                    case "allfile":
                        String path = file_list.get(i).getFileAbsAddress();
                        currentParent = new File(path);
                        if (currentParent.isFile()) {
                            file_list.get(i).setIsCheck(true);
                        } else {
                            file_list.get(i).setIsCheck(false);
                            File[] tem = currentParent.listFiles();
                            if (tem == null || tem.length == 0) {
                                Toast.makeText(FileChooseActivity.this, "当前路径不可用", Toast.LENGTH_SHORT).show();
                            } else {
                                currentFiles = tem;
                                infateListView(currentFiles);

                            }
                        }
                        break;

                }
            }
        });
    }

    private void initView() {
        pm = getPackageManager();
        fileList = (ListView) findViewById(R.id.file_list);
        address = (TextView) findViewById(R.id.file_address);
        imageList = (GridView) findViewById(R.id.grid);
        //将listview隐藏
        setListViewVisiable(fileList, false);
        file_list = new ArrayList<>();
        //内部sd卡路径
        String inPath = Environment.getExternalStorageDirectory().getPath();

        if(inPath!=null) {
            inSd = new File(inPath);
        }
        SDFile root1 = FileOperation.fileToSdfile(context,inSd,"root1");
        file_list.add(root1);
        outSd = new File("/storage/extSdCard");
        if(outSd.exists()){
            SDFile root2 = FileOperation.fileToSdfile(context,outSd,"root2");
            file_list.add(root2);
        }
        //默认加载gridview显示图片
        image = getImageInfo();
        imageAdapter=new ImageViewAdapter(image, this);
        imageList.setAdapter(imageAdapter);


    }
    private static void setPathVisiable(TextView textView,boolean isVisiable){
        if(isVisiable){
            textView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(View.GONE);
        }

    }

    private static void setListViewVisiable(ListView list,boolean isVisiable){
        if(isVisiable){
            list.setVisibility(View.VISIBLE);
        }else{
            list.setVisibility(View.GONE);
        }
    }
    private static void setGridViewVisiable(GridView grid,boolean isVisiable){
        if(isVisiable){
            grid.setVisibility(View.VISIBLE);
        }else{
            grid.setVisibility(View.GONE);
        }
    }

    private static void infateListView(File[] currentFiles) {
        file_list.clear();
        for(int i = 0 ; i <currentFiles.length;i++){
            if(currentFiles[i].isDirectory()){
                file_list.add(FileOperation.fileToSdfile(context, currentFiles[i], "directory"));
            }else{
               file_list.add(FileOperation.fileToSdfile(context, currentFiles[i], "file"));
            }
        }
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, file_list);
        fileList.setAdapter(fileAdapter);
        try {
            address.setText("当前路径为："+currentParent.getCanonicalPath());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void showAllFileList() {
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, file_list);
        fileList.setAdapter(fileAdapter);
    }

    private static void showAppList() {
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, applist);
        fileList.setAdapter(fileAdapter);
    }

    private static void showMusicList() {
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, musiclist);
        fileList.setAdapter(fileAdapter);
    }

    private static void showPicture() {
        imageAdapter=new ImageViewAdapter(image, context);
        imageList.setAdapter(imageAdapter);
    }


    //  查找sdcard卡上的所有图片信息
    public  List<String> getImageInfo() {
        List<String> imagelist = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));				//文件路径
            imagelist.add(url);
        }
        return imagelist;
    }


    public List<SDFile> getMusic(){
        List<SDFile> allmusic = new ArrayList<SDFile>();
        List<String> musiclist = getMusicInfo();
//        Log.e("1",musiclist.size()+"");
        for (int i = 0 ; i <musiclist.size();i++){
            SDFile music = new SDFile();
            String musicPath = musiclist.get(i);
//            Log.e("1",musicPath);
            File tempMusic = new File(musicPath);
            music.setName(tempMusic.getName());
            music.setFsize(tempMusic.length() + "");
            music.setModificationTime(DateTransformUtil.getModifyTime(tempMusic.lastModified()));
            music.setFileAbsAddress(tempMusic.getAbsolutePath());
            music.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.music));
            allmusic.add(music);
        }
        return allmusic;
    }

    //  查找sdcard卡上的所有歌曲信息
    public  List<String> getMusicInfo() {
        List<String> musiclist = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));				//文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if (isMusic != 0) {		//只把音乐添加到集合当中
                musiclist.add(url);
            }
        }
        return musiclist;
    }


    public static List<SDFile> getAppInfo(){
        List<SDFile> list = new ArrayList<SDFile>();
        List<PackageInfo> apkInfos = pm.getInstalledPackages(0);
        String name = "";
        Drawable icon;
        PackageInfo apk;
        for (int i = 0; i < apkInfos.size(); i++ )
        {
            apk = apkInfos.get(i);
            if((apk.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
            {
                SDFile file = new SDFile();
                name = (String) pm.getApplicationLabel(apk.applicationInfo);
                icon = pm.getApplicationIcon(apk.applicationInfo);
                String path = apk.applicationInfo.sourceDir;
                File apk_file = new File(path);
                file.setName(name);
                file.setModificationTime(DateTransformUtil.getModifyTime(apk_file.lastModified()));
                file.setFsize(apk_file.length()+"");
                file.setImage(((BitmapDrawable) icon).getBitmap());
                file.setFileAbsAddress(path);
                list.add(file);//如果非系统应用，则添加至appList
            }
        }

        return list;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private AnimTabsView mTabsView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setupViews(rootView);
            return rootView;
        }

        private void setupViews(View rootView) {
            mTabsView = (AnimTabsView) rootView.findViewById(R.id.publiclisten_tab);
            mTabsView.addItem("图片");
            mTabsView.addItem("音乐");
            mTabsView.addItem("应用");
            mTabsView.addItem("全部文件");

            mTabsView.setOnAnimTabsItemViewChangeListener(new AnimTabsView.IAnimTabsItemViewChangeListener() {
                @Override
                public void onChange(AnimTabsView tabsView, int oldPosition, int currentPosition) {
                    TextView textView = (TextView) tabsView.getItemView(oldPosition).findViewById(R.id.current_type);
                    String type = textView.getText().toString();
                    System.out.println(type);
                    switch (type) {
                        case "图片":
                            //将网格布局显示，隐藏listview
                            setGridViewVisiable(imageList, true);
                            setListViewVisiable(fileList, false);
                            setPathVisiable(address,false);
                            showPicture();
                            break;
                        case "音乐":
                            //隐藏网格布局，显示listview
                            setGridViewVisiable(imageList, false);
                            setListViewVisiable(fileList, true);
                            setPathVisiable(address, false);
                            currentPage="allmusic";
                            showMusicList();
                            break;
                        case "应用":
                            //隐藏网格布局，显示listview
                            setGridViewVisiable(imageList, false);
                            setListViewVisiable(fileList, true);
                            setPathVisiable(address, false);
                            currentPage="allapp";
                            showAppList();
                            break;
                        case "全部文件":
                            setGridViewVisiable(imageList, false);
                            setListViewVisiable(fileList, true);
                            setPathVisiable(address, true);
                            currentPage="allfile";
                            showAllFileList();
                            //隐藏网格布局。
                            break;
                    }
                }


            });
        }


    }
}
