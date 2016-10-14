package com.example.lameonandroid.activity;

import android.graphics.Bitmap;

import java.io.File;

public class SdFile implements Comparable<SdFile> {
    private String name;
    private String filePath;
    private File file;
    private Bitmap bitmap;
    private boolean isPic;
    private long size;

    public boolean ischecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    private boolean ischecked = false;


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isPic() {
        return isPic;
    }

    public void setPic(boolean isPic) {
        this.isPic = isPic;
    }

    @Override
    public int compareTo(SdFile sdFile) {
        return this.name.length() - sdFile.name.length();
    }
}
