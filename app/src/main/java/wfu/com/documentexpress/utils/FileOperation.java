package wfu.com.documentexpress.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.model.SDFile;



/**
 * Created by Lenovo on 2016/4/14.
 */
public class FileOperation {

    public final static int DEFAULT_FILE_OPERATE_MODE = 0;

    public final static int IGNORE_NOT_RECREATE_MODE = 1;

    public final static int IGNORE_AND_RECREATE_MODE = 2;

    public final static int NOT_IGNORE_RECREATE_MODE = 3;

    private final static boolean DEFAULT_IGNORE_STYLE = false;

    private final static boolean DEFAULT_AUTO_CREATE_DIRECTORY = true;


// ============== delete file and dirctory==================
    /**
     * delete a file.
     *
     * @param file
     */
    public static void deleteFile(File file)
    {

        if (file == null || TextUtils.isEmpty(file.getAbsolutePath())) {
            return;
        }
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    /**
     * @param filePath
     */
    public static void deleteFile(String filePath)
    {

        if (!TextUtils.isEmpty(filePath)) {
            deleteFile(new File(filePath));
        }
    }

    /**
     * delete folder.
     *
     * @param folder
     */
    public static void deleteFolder(File folder)
    {

        if (folder == null || TextUtils.isEmpty(folder.getAbsolutePath())) {
            return;
        }
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if(files!=null)
                {
                    for (File file : files) {
                        deleteFolder(file);
                    }
                }
            }
            else {
                deleteFile(folder);
            }
        }
    }



// ===========copy single file.=================
/**
 * To copy single file.
 *
 * @param src
 * @param dst
 * @return
 * @throws IOException
 */
    public static boolean copyFile(File src, File dst) throws IOException
    {

        if ((!src.exists()) || src.isDirectory() || dst.isDirectory()) {
            return false;
        }
        if (!dst.exists()) {
            dst.createNewFile();
            return false;
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        inputStream = new FileInputStream(src);
        outputStream = new FileOutputStream(dst);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = inputStream.read(buf)) != -1) {
            outputStream.write(buf, 0, readLen);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
        return true;
    }

    /**
     * @param src
     * @param dst
     * @return
     * @throws IOException
     */
    public static boolean copyFile(String src, String dst) throws IOException
    {

        return copyFile(new File(src), new File(dst));
    }

// ===========copy folder to storage=================
    /**
     * To copy the folder.
     * @param srcDir
     * @param destDir
     * @param auto
     * @return
     * @throws IOException
     */
    public static boolean copyFolder(File srcDir, File destDir, boolean auto)
            throws IOException
    {

        if ((!srcDir.exists())) {
            return false;
        }
        if (srcDir.isFile() || destDir.isFile())
            return false;
        if (!destDir.exists()) {
            if (auto) {
                destDir.mkdirs();
            }
            else {
                return false;
            }
        }
        File[] srcFiles = srcDir.listFiles();
        int len = srcFiles.length;
        for (int i = 0; i < len; i++) {
            if (srcFiles[i].isFile()) {
                File destFile = new File(destDir.getPath() + "//"
                        + srcFiles[i].getName());
                copyFile(srcFiles[i], destFile);
            }
            else if (srcFiles[i].isDirectory()) {
                File theDestDir = new File(destDir.getPath() + "//"
                        + srcFiles[i].getName());
                copyFolder(srcFiles[i], theDestDir,auto);
            }
        }
        return true;
    }

    /**
     * @param srcDir
     * @param desDir
     * @param auto
     * @return
     * @throws IOException
     */
    public static boolean copyFolder(String srcDir, String desDir, boolean auto)
            throws IOException
    {

        return copyFolder(new File(srcDir), new File(desDir), auto);
    }

    /**
     * @param srcDir
     * @param desDir
     * @return
     * @throws IOException
     */
    public static boolean copyFolder(File srcDir, File desDir)
            throws IOException
    {

        return copyFolder(srcDir, desDir, DEFAULT_AUTO_CREATE_DIRECTORY);
    }

    /**
     * @param srcDir
     * @param desDir
     * @return
     * @throws IOException
     */
    public static boolean copyFolder(String srcDir, String desDir)
            throws IOException
    {

        return copyFolder(srcDir, desDir, DEFAULT_AUTO_CREATE_DIRECTORY);
    }

// ===========move single file to storage=================
    /**
     * To move the single file.
     * @param src
     * @param dst
     * @return
     */
    public static boolean moveFile(File src, File dst)
    {
        boolean isCopy = false;
        try {
            isCopy = copyFile(src, dst);
        }
        catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(!isCopy)
        {
            return false;
        }
        deleteFile(src);
        return true;
    }

    /**
     * @param src
     * @param dst
     * @return
     */
    public static boolean moveFile(String src, String dst)
    {

        return moveFile(new File(src), new File(dst));
    }
// ===========move folder to storage=================
    /**
     * To move the folder.
     * @param srcDir
     * @param destDir
     * @param auto
     * @return
     */
    public static boolean moveFolder(File srcDir,File destDir,boolean auto)
    {
        if (!srcDir.isDirectory() || !destDir.isDirectory()) {
            return false;
        }
        if(!srcDir.exists())
        {
            return false;
        }
        if(!destDir.exists())
        {
            if(auto)
            {
                destDir.mkdirs();
            }
            else{
                return false;
            }
        }
        File[] srcDirFiles = srcDir.listFiles();
        int len = srcDirFiles.length;
        if(len<=0)
        {
            srcDir.delete();
        }
        for (int i = 0; i < len; i++) {
            if (srcDirFiles[i].isFile()) {
                File oneDestFile = new File(destDir.getPath() + "//"
                        + srcDirFiles[i].getName());
                moveFile(srcDirFiles[i], oneDestFile);
            } else if (srcDirFiles[i].isDirectory()) {
                File oneDestFile = new File(destDir.getPath() + "//"
                        + srcDirFiles[i].getName());
                moveFolder(srcDirFiles[i], oneDestFile,auto);
                deleteFolder(srcDirFiles[i]);
            }
        }
        return true;
    }
    /**
     * @param src
     * @param dst
     * @param auto
     * @return
     */
    public static boolean moveFolder(String src,String dst,boolean auto)
    {
        return moveFolder(new File(src), new File(dst));
    }
    /**
     * @param src
     * @param dst
     * @return
     */
    public static boolean moveFolder(File src,File dst)
    {
        return moveFolder(src, dst, DEFAULT_AUTO_CREATE_DIRECTORY);
    }
    /**
     * @param src
     * @param dst
     * @return
     */
    public static boolean moveFolder(String src,String dst)
    {
        return moveFolder(new File(src), new File(dst), DEFAULT_AUTO_CREATE_DIRECTORY);
    }

    public static String getSuffix(String fileName){
        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
        return prefix;
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




    public static Intent openFile(String filePath){

        File file = new File(filePath);
        if(!file.exists()) return null;
        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            return getAudioFileIntent(filePath);
        }else if(end.equals("3gp")||end.equals("mp4")){
            return getAudioFileIntent(filePath);
        }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            return getImageFileIntent(filePath);
        }else if(end.equals("apk")){
            return getApkFileIntent(filePath);
        }else if(end.equals("ppt")){
            return getPptFileIntent(filePath);
        }else if(end.equals("xls")){
            return getExcelFileIntent(filePath);
        }else if(end.equals("doc")){
            return getWordFileIntent(filePath);
        }else if(end.equals("pdf")){
            return getPdfFileIntent(filePath);
        }else if(end.equals("chm")){
            return getChmFileIntent(filePath);
        }else if(end.equals("txt")){
            return getTextFileIntent(filePath,false);
        }else{
            return getAllIntent(filePath);
        }
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent( String param ) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri,"*/*");
        return intent;
    }
    //Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent( String param ) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent( String param ) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent( String param ){

        Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent( String param ) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent( String param, boolean paramBoolean){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean){
            Uri uri1 = Uri.parse(param );
            intent.setDataAndType(uri1, "text/plain");
        }else{
            Uri uri2 = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }
    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}
