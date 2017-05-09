package com.example.lixiaoqing.myaccessibility;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lixiaoqing.myaccessibility.tools.OpenCloseAPP;


public class DownLoadBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {



        long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);

        long refernece = sharedPreferences.getLong("downloadID", 0);

        String needInstallPackageName = sharedPreferences.getString("needInstallPackageName", "");

        if (refernece == myDwonloadID && !"".equals(needInstallPackageName)) {

            ControllerService.setSTATES(ControllerService.STATES_DOWNLOADING_END);

            ControllerService.setSTATES(ControllerService.STATES_INSTALLING);

            MyService.INVOKE_TYPE = MyService.TYPE_INSTALL_APP;

            String serviceString = Context.DOWNLOAD_SERVICE;

            DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);

            boolean installResult = OpenCloseAPP.installApp(context, needInstallPackageName);

            if (installResult){

                Log.d("test", "正常安装");
            } else {

                Log.d("test", "正常异常");

                ControllerService.setSTATES(ControllerService.STATES_UNINSTALLING_END);
            }
        }
    }
}
