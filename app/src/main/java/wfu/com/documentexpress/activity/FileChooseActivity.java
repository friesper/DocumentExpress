package wfu.com.documentexpress.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.adapter.FileListViewAdapter;
import wfu.com.documentexpress.adapter.ImageViewAdapter;
import wfu.com.documentexpress.model.SDFile;
import wfu.com.documentexpress.utils.DateTransformUtil;
import wfu.com.documentexpress.utils.DialogUtil;
import wfu.com.documentexpress.utils.FileOperation;
import wfu.com.documentexpress.view.AnimTabsView;

/**
 * Created by Lenovo on 2016/4/13.
 */
public class FileChooseActivity extends FragmentActivity {
    private ListView fileList;
    private GridView imageList;
    private FileListViewAdapter fileAdapter;
    private ImageViewAdapter imageAdapter;
    private PackageManager pm;
    private Context context;

    private List<SDFile> applist;
    private List<SDFile> musiclist;
    private List<SDFile> file_list;
    private List<String> imagePath;
    private List<Bitmap> image = new ArrayList<Bitmap>();
    private File inSd;  //内部存储
    private File outSd;
    private TextView address;
    private String currentPage = "picture";
    private boolean isChice[];
    private boolean isroot = true;
    private List<String> choosePath;
    private Button send;
    private LinearLayout backLayout;
    //当前父文件夹
    private static File currentParent;
    //当前路径下的所有文件的文件数组
    private static File[] currentFiles;
    private static final String TAG = "FileChooseMainActivity";
    private Handler myhHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    imageAdapter = new ImageViewAdapter(imagePath, context, isChice);
                    imageList.setAdapter(imageAdapter);
                    break;
                case 0x124:
                    try {
                        address.setText("当前路径为：" + currentParent.getCanonicalPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x125:
                    address.setText("");
                    break;

            }

        }
    };

    @Override
    public void onBackPressed() {
        try {
            if (currentPage.equals("allfile")) {
                if (address.getText().equals("")) {
                    showDialog();
                }else{
                    if (currentParent.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getPath()) || currentParent.getCanonicalPath().equals("/storage/extSdCard")) {
                        file_list.clear();
                        initSDFile();
                        fileAdapter.notifyDataSetChanged();myhHandler.sendEmptyMessage(0x125);

                    } else {
                        currentParent = currentParent.getParentFile();
                        currentFiles = currentParent.listFiles();
                        infateListView(currentFiles);
                    }
                }
            } else {
                showDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
//        new AlertDialog.Builder(FileChooseActivity.this).setTitle("注意！")//设置对话框标题
//                .setMessage("您确定要取消本次传输吗？")//设置显示的内容
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//                        finish();
//                    }
//                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {//响应事件
//                dialog.dismiss();
//            }
//        }).show();//在按键响应事件中显示此对话框
        DialogUtil.showDialog(FileChooseActivity.this, "您确定要取消本次传输吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_filechoose);
        context = getApplicationContext();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        loadMessage();
        initView();
        initEvent();

    }

    private void loadMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imagePath = getImageInfo();
//                loadImageBitmap(image, imagePath);
                isChice = new boolean[imagePath.size()];
                for (int i = 0; i < isChice.length; i++) {
                    isChice[i] = false;
                }
                myhHandler.sendEmptyMessage(0x123);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                applist = getAppInfo();
                musiclist = getMusic();
            }
        }).start();
    }

    private void initView() {
        pm = getPackageManager();
        fileList = (ListView) findViewById(R.id.file_list);
        address = (TextView) findViewById(R.id.file_address);
        imageList = (GridView) findViewById(R.id.grid);
        send = (Button) findViewById(R.id.file_send);
        backLayout = (LinearLayout) findViewById(R.id.back_layout);
        choosePath = new ArrayList<String>();
        //将listview隐藏
        setListViewVisiable(fileList, false);
        //将path隐藏
        setPathVisiable(address, false);
        file_list = new ArrayList<>();
        initSDFile();


    }

    private void initSDFile() {
        //内部sd卡路径
        String inPath = Environment.getExternalStorageDirectory().getPath();
        if (inPath != null) {
            inSd = new File(inPath);
        }
        SDFile root1 = FileOperation.fileToSdfile(context, inSd, "root1");
        file_list.add(root1);
        outSd = new File("/storage/extSdCard");
        if (outSd.exists()) {
            SDFile root2 = FileOperation.fileToSdfile(context, outSd, "root2");
            file_list.add(root2);
        }
    }

    private void initEvent() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosePath.size() == 0) {
                    Toast.makeText(context, "未选中任何文件", Toast.LENGTH_SHORT).show();
                } else {
//                    LogUtil.e("1", choosePath.toString());
                    switch (MainActivity.express_mode) {
                        case "WiFi":       Intent intent = new Intent(context, ConnectUserActivity.class);
                            intent.putExtra("path_list", (Serializable) choosePath);
                        startActivity(intent);
                        finish();
                            break;
                        case "NFC":
                            Intent intent1=new Intent(context,NFcExpressActivity.class);
                            intent1.putExtra("path_list", (Serializable) choosePath);
                            startActivity(intent1);
                            finish();
                            default: break;
                    }
                }
            }
        });
        imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                CheckBox cb = (CheckBox) arg1.findViewById(R.id.img_checked);
                if (cb.isChecked()) {
                    cb.setChecked(false);
                } else {
                    cb.setChecked(true);
                }
                imageAdapter.chiceState(position);
                if (choosePath.contains(imagePath.get(position))) {
                    choosePath.remove(imagePath.get(position));
                } else {
                    choosePath.add(imagePath.get(position));
                }

            }
        });
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SDFile chooseFile;
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
                            setCheck(file_list.get(i));
                            addChoosePathToList(file_list.get(i));
                        } else {
                            File[] tem = currentParent.listFiles();
                            if (tem == null || tem.length == 0) {
                                Toast.makeText(FileChooseActivity.this, "当前路径无文件", Toast.LENGTH_SHORT).show();
                            } else {
                                currentFiles = tem;
                                infateListView(currentFiles);
                            }
                        }
                        break;
                    case "allmusic":
                        chooseFile = musiclist.get(i);
                        setCheck(chooseFile);
                        addChoosePathToList(chooseFile);
                        break;
                    case "allapp":
                        chooseFile = applist.get(i);
                        setCheck(chooseFile);
                        addChoosePathToList(chooseFile);
                        break;

                }
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        });
    }

    private void setCheck(SDFile chooseFile) {
        if (chooseFile.isCheck()) {
            chooseFile.setIsCheck(false);
        } else {
            chooseFile.setIsCheck(true);
        }
    }

    private void addChoosePathToList(SDFile file) {
        if (choosePath.contains(file.getFileAbsAddress())) {
            choosePath.remove(file.getFileAbsAddress());
        } else {
            choosePath.add(file.getFileAbsAddress());
        }
    }


    private void setPathVisiable(TextView textView, boolean isVisiable) {
        if (isVisiable) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }

    }

    private void setListViewVisiable(ListView list, boolean isVisiable) {
        if (isVisiable) {
            list.setVisibility(View.VISIBLE);
        } else {
            list.setVisibility(View.GONE);
        }
    }

    private void setGridViewVisiable(GridView grid, boolean isVisiable) {
        if (isVisiable) {
            grid.setVisibility(View.VISIBLE);
        } else {
            grid.setVisibility(View.GONE);
        }
    }

    private void infateListView(File[] currentFiles) {
        file_list.clear();
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory()) {
                file_list.add(FileOperation.fileToSdfile(context, currentFiles[i], "directory"));
            } else {
                file_list.add(FileOperation.fileToSdfile(context, currentFiles[i], "file"));
            }
        }
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, file_list);
        fileList.setAdapter(fileAdapter);
        myhHandler.sendEmptyMessage(0x124);
    }

    private void showAllFileList() {
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, file_list);
        fileList.setAdapter(fileAdapter);
    }

    private void showAppList() {
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, applist);
        fileList.setAdapter(fileAdapter);
    }

    private void showMusicList() {
        fileAdapter = new FileListViewAdapter(context, R.layout.file_list_item, musiclist);
        fileList.setAdapter(fileAdapter);
    }

    private void showPicture() {
        imageAdapter = new ImageViewAdapter(imagePath, context, isChice);
        imageList.setAdapter(imageAdapter);
    }


    //  查找sdcard卡上的所有图片信息
    public List<String> getImageInfo() {
        List<String> imagelist = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));                //文件路径
            imagelist.add(url);
        }
        return imagelist;
    }


    public List<SDFile> getMusic() {
        List<SDFile> allmusic = new ArrayList<SDFile>();
        List<String> musiclist = getMusicInfo();
//        Log.e("1",musiclist.size()+"");
        for (int i = 0; i < musiclist.size(); i++) {
            SDFile music = new SDFile();
            String musicPath = musiclist.get(i);
//            Log.e("1",musicPath);
            File tempMusic = new File(musicPath);
            music.setName(tempMusic.getName());
            music.setFsize(tempMusic.length() + "");
            music.setModificationTime(DateTransformUtil.getModifyTime(tempMusic.lastModified()));
            music.setFileAbsAddress(tempMusic.getAbsolutePath());
            music.setFileType("music");
//            music.setImage(BitmapUtil.comp(BitmapFactory.decodeResource(getResources(), R.drawable.music)));
            allmusic.add(music);
        }
        return allmusic;
    }

    //  查找sdcard卡上的所有歌曲信息
    public List<String> getMusicInfo() {
        List<String> musiclist = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));                //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if (isMusic != 0) {        //只把音乐添加到集合当中
                musiclist.add(url);
            }
        }
        return musiclist;
    }


    public List<SDFile> getAppInfo() {
        List<SDFile> list = new ArrayList<SDFile>();
        List<PackageInfo> apkInfos = pm.getInstalledPackages(0);
        String name = "";
        Drawable icon;
        PackageInfo apk;
        for (int i = 0; i < apkInfos.size(); i++) {
            apk = apkInfos.get(i);
            if ((apk.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                SDFile file = new SDFile();
                name = (String) pm.getApplicationLabel(apk.applicationInfo);
                icon = pm.getApplicationIcon(apk.applicationInfo);
                String path = apk.applicationInfo.sourceDir;
                File apk_file = new File(path);
                file.setName(name);
                file.setModificationTime(DateTransformUtil.getModifyTime(apk_file.lastModified()));
                file.setFsize(apk_file.length() + "");
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
    @SuppressLint("ValidFragment")
    public class PlaceholderFragment extends Fragment {

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
                    switch (type) {
                        case "图片":
                            //将网格布局显示，隐藏listview
                            setGridViewVisiable(imageList, true);
                            setListViewVisiable(fileList, false);
                            setPathVisiable(address, false);
                            currentPage = "picture";
                            showPicture();
                            break;
                        case "音乐":
                            //隐藏网格布局，显示listview
                            setGridViewVisiable(imageList, false);
                            setListViewVisiable(fileList, true);
                            setPathVisiable(address, false);
                            currentPage = "allmusic";
                            showMusicList();
                            break;
                        case "应用":
                            //隐藏网格布局，显示listview
                            setGridViewVisiable(imageList, false);
                            setListViewVisiable(fileList, true);
                            setPathVisiable(address, false);
                            currentPage = "allapp";
                            showAppList();
                            break;
                        case "全部文件":
                            setGridViewVisiable(imageList, false);
                            setListViewVisiable(fileList, true);
                            setPathVisiable(address, true);
                            currentPage = "allfile";
                            showAllFileList();
                            //隐藏网格布局。
                            break;
                    }
                }


            });
        }


    }
}
