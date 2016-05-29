package wfu.com.documentexpress.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.io.File;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.model.SDFile;

/**
 * Created by Lenovo on 2016/4/14.
 */
public class FileOperation {
    public static String getSuffix(String fileName){
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot >-1) && (dot < (fileName.length() - 1))) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }


    public static SDFile fileToSdfile(Context context,File file,String fileType){
        SDFile sdFile = new SDFile();
        if(fileType.equals("root1")){
            sdFile.setName("SD卡");
            sdFile.setFileType("root");
            sdFile.setFileAbsAddress(file.getAbsolutePath());
            sdFile.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.folder));
        }
        if(fileType.equals("root2")){
            sdFile.setName("扩展内存卡");
            sdFile.setFileType("root");
            sdFile.setFileAbsAddress(file.getAbsolutePath());
            sdFile.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.folder));
        }
        switch (fileType) {
            case "directory":
                sdFile.setName(file.getName());
                sdFile.setFileType("directory");
                sdFile.setIsFile(false);
                sdFile.setFileAbsAddress(file.getAbsolutePath());
                sdFile.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.folder));
                break;
            case "file":
                sdFile.setName(file.getName());
                sdFile.setIsFile(true);
                sdFile.setFsize(file.length() + "");
                sdFile.setFileType("file");
                sdFile.setFileAbsAddress(file.getAbsolutePath());
                sdFile.setModificationTime(DateTransformUtil.getModifyTime(file.lastModified()));
                sdFile.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.file));
                break;
        }
        return sdFile;
    }


}
