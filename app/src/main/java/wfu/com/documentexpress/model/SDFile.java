package wfu.com.documentexpress.model;

import android.graphics.Bitmap;

/**
 * Created by Lenovo on 2016/4/14.
 */
public class SDFile {
    private String name="";
    private String fsize="";
    private String modificationTime="";
    private Bitmap image=null;
    private boolean isCheck = false;
    private boolean isFile = false;
    private String fileType = "";

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    private String fileAbsAddress;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getFileAbsAddress() {
        return fileAbsAddress;
    }

    public void setFileAbsAddress(String fileAbsAddress) {
        this.fileAbsAddress = fileAbsAddress;
    }

    public String getFsize() {
        return fsize;
    }

    public void setFsize(String fsize) {
        this.fsize = fsize;
    }


    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
