package com.test.menutest;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.MediaStore;
import android.widget.Toast;

public class MyApplication extends Application {
    private static Context context;

    public void onCreate()
    {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return MyApplication.context;
    }

    public static void deletePhoto(String path)
    {
        if(path.equals("")) return;
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ path });
    }

    public static void showToast(String text)
    {
        Toast.makeText(getAppContext(), text, Toast.LENGTH_SHORT).show();
    }
}
