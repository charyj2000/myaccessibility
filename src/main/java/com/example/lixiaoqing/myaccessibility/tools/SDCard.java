package com.example.lixiaoqing.myaccessibility.tools;


import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Runtime.getRuntime;

public class SDCard {

    private static String SDCardPath = null;

    public static String showSDCardPath(){

        String inPath = getInnerSDCardPath();
        Log.d("test", "内置SD卡路径：" + inPath + "\r\n");

        List<String> extPaths = getExtSDCardPath();
        for (String path : extPaths) {
            Log.d("test", "外置SD卡路径：" + path + "\r\n");
        }

        return null;
    }


    /**
     * 获取内置SD卡路径
     * @return s
     */
    public static String getInnerSDCardPath() {

        if (null != SDCardPath){
            return SDCardPath;
        }

        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("test", "sd card MEDIA_MOUNTED");

            SDCardPath = Environment.getExternalStorageDirectory().getPath();
            SDCardPath = checkAndReplaceEmulatedPath(SDCardPath);

            if(new File(SDCardPath).canRead())

                Log.d("test", "找到sdCard 目录");


            else if (new File("/sdcard").canRead())

                SDCardPath = "/sdcard";

            else

                Log.e("test", "没有找到有效的sd卡目录");

            return SDCardPath;

        } else {
            Log.d("test", "sd card " + status);
        }

        return null;
    }

    public static String getDownLoadPath(){

        String downloadFolderPath = Environment.getExternalStoragePublicDirectory("download").getPath();

        File downloadFolder = new File(downloadFolderPath);

        while (!downloadFolder.exists()) {

            if (downloadFolder.mkdir())

                return downloadFolderPath;

            else {

                Log.d("test", "create download folder false");

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {
                }

                Log.d("test", "---88------");
                downloadFolder = new File(downloadFolderPath);

                continue;
            }
        }

        Log.d("test", downloadFolderPath);
        return downloadFolderPath;
    }
    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    private static List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception ignored) {}
        return lResult;
    }






    public static final String CT_S_Sdcard_Sign_Storage_emulated = "storage/emulated/";
    public static final String CT_S_Sdcard_Sign_Storage_sdcard = "storage/sdcard";


    public static String checkAndReplaceEmulatedPath(String strSrc){

        Pattern p = Pattern.compile("/?storage/emulated/\\d{1,2}");
        Matcher m = p.matcher(strSrc);
        if (m.find()){
            strSrc = strSrc.replace(CT_S_Sdcard_Sign_Storage_emulated, CT_S_Sdcard_Sign_Storage_sdcard);
        }

        return strSrc;
    }

    public static void cleanJunkFiles(Context context, long lastOperateTime){


        // 清空下载器内容

        cleanDownloadManager(context);

        // 清空下载APP 记录

//        cleanDownloadAPPRecord();

        //删除sdCard 下的文件；

        String sdcardPath = getInnerSDCardPath();

        File rootFile = new File(sdcardPath);

        if (!rootFile.canRead()){
            return;
        }

        File[] children = rootFile.listFiles();


        List<String> filePaths = new ArrayList<>();

        for (File file : children) {

            if (!file.getPath().contains("/.") || file.lastModified() > lastOperateTime)
                if (!file.getPath().contains("/download"))
                    filePaths.add(file.getPath());
        }

        deleteFile(filePaths);
    }

    private static void deleteFile(List<String> paths){

        try {

            String cmd = "";

            for (String path : paths)

                cmd += "rm -rf " + path + " \n";

            if (cmd.length() > 0)

                getRuntime().exec(cmd);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    // 清空下载器内容
    private static void cleanDownloadManager(Context context){

        DownloadManager downloadManager = (DownloadManager) (context.getSystemService(Context.DOWNLOAD_SERVICE));

        DownloadManager.Query query = new DownloadManager.Query();

        Cursor cursor = downloadManager.query(query);

        long[] ids = new long[cursor.getCount()];
        int i = 0;
        long id ;

        while (cursor.moveToNext()) {

            id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));

            Log.d("test", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)));

            ids[i++] = id;
        }

        if (ids.length>0)

            downloadManager.remove(ids);
    }

    private static void cleanDownloadAPPRecord(){

        String cmd = "rm -rf /data/data/com.android.providers.downloads/databases \n";

        try {

            Process process = Runtime.getRuntime().exec("su");

            OutputStream outputStream =  process.getOutputStream();
            outputStream.write(cmd.getBytes());
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            process = null;

        } catch (Exception ignored){ }
    }

}
