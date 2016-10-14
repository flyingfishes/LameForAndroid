package com.example.lameonandroid.function;

import android.os.Environment;

/**
 * Author:pdm on 2016/8/22 10:42
 * Email:aiyh0202@163.com
 */
public class Constants {
    public static final String ROOT = "storage/emulated/0";
    public static final String DEFULTROOT = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    public static final String INSIDEROOT = Environment.getDataDirectory().getAbsolutePath().toString();
}
