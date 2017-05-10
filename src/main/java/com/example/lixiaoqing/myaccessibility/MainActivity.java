package com.example.lixiaoqing.myaccessibility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lixiaoqing.myaccessibility.tools.DeviceTool;
import com.example.lixiaoqing.myaccessibility.tools.OpenCloseAPP;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;


public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks{

    protected TextView serverIP;
    protected TextView deviceID;
    private Intent controllerServiceIntent;
    private int REQUEST_READ_EXTERNAL_STORAGE = 0;
    private int REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        requestPermissions();


        serverIP = (TextView)findViewById(R.id.server_ip);
        deviceID = (TextView)findViewById(R.id.deviceID);
        deviceID.setText(null == DeviceTool.getIMEI(this)? "" : DeviceTool.getIMEI(this));

    }

    private void requestPermissions(){

        String[] perms = {READ_PHONE_STATE, READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //...
        } else if (!EasyPermissions.hasPermissions(this, READ_PHONE_STATE)){
            //...
            EasyPermissions.requestPermissions(this, "没有读取手机状态权限", REQUEST_READ_PHONE_STATE, new String[]{READ_PHONE_STATE});

        } else if (!EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)){
            //...
            EasyPermissions.requestPermissions(this, "没有操作SDCard权限", REQUEST_READ_EXTERNAL_STORAGE, new String[]{READ_EXTERNAL_STORAGE});
        }
    }



    @Override
    protected void onResume() {
        super.onResume();


        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_APPEND);
        serverIP.setText(sharedPreferences.getString(Properties.SHAREDPREFERENCES_NAME_SERVER_IP,"117.121.97.28:8091"));
    }

    public void openSet(View view){

        Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(killIntent);

    }

    public void exit(View view){
        this.finish();
        System.exit(0);

    }

    public void save(View view){
        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_APPEND);
        sharedPreferences.edit().putString(Properties.SHAREDPREFERENCES_NAME_SERVER_IP, serverIP.getText().toString()).apply();
        MyService.setServerIP(serverIP.getText().toString());
    }

    public void start(View view){

        if (MyService.getInstance() == null ){
            Toast.makeText(getApplicationContext(), "请先打开辅助功能！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 记录开始时间
        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_APPEND);
        sharedPreferences.edit().putLong(Properties.START_TIME, System.currentTimeMillis()).apply();

        controllerServiceIntent = new Intent(this, ControllerService.class);

        startService(controllerServiceIntent);

    }

    public void stop(View view){

        Intent intent = new Intent();
        intent.setAction(ControllerService.ACTION_STOP);
        sendBroadcast(intent);

        if (null != controllerServiceIntent){

            stopService(controllerServiceIntent);
            controllerServiceIntent = null;
        } else {

            Toast.makeText(getApplicationContext(), "service 已经关闭！", Toast.LENGTH_SHORT).show();
        }
    }

    public void testDownload(View view){
        MyService.INVOKE_TYPE = MyService.TYPE_INSTALL_APP;
        OpenCloseAPP.downloadApp(this, "com.tencent.news");
    }

    public void uninstall(View view){

        CrashReport.testJavaCrash();
        CrashReport.testNativeCrash();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        Log.d("test","onPermissionsGranted");
        requestPermissions();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("test","onPermissionsDenied");
        System.exit(0);
    }
}
