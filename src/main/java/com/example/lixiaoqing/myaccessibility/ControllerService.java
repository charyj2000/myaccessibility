package com.example.lixiaoqing.myaccessibility;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.example.lixiaoqing.myaccessibility.bean.RequestPackageNameBean;
import com.example.lixiaoqing.myaccessibility.bean.ResponsePackageNameBean;
import com.example.lixiaoqing.myaccessibility.tools.DeviceTool;
import com.example.lixiaoqing.myaccessibility.tools.HttpUtils;
import com.example.lixiaoqing.myaccessibility.tools.OpenCloseAPP;

import java.util.concurrent.Callable;

import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.lixiaoqing.myaccessibility.tools.SDCard.cleanJunkFiles;


public class ControllerService extends Service {

    private static Thread thread;
    private static boolean threadRunning = false;
    private static String testPackageName = null;

    private static long needTestTimes, waitTime, prepareTime;
    private static int strategyType;


    public static final int  STATES_GETTING_PACKAGENAME = 1;
    public static final int  STATES_GETTING_PACKAGENAME_END = 2;

    public static final int  STATES_DOWNLOADING = 3;
    public static final int  STATES_DOWNLOADING_END = 4;

    public static final int  STATES_INSTALLING = 5;
    public static final int  STATES_INSTALLING_END = 6;

    public static final int  STATES_OPERATING = 7;
    public static final int  STATES_OPERATING_END = 8;

    public static final int  STATES_UNINSTALLING = 9;
    public static final int  STATES_UNINSTALLING_END = 10;

    public static int  STATES = STATES_UNINSTALLING_END;

    public static int getSTATES() {

        return STATES;

    }

    private static String getStatesName(int state){

        switch (state){

            case STATES_GETTING_PACKAGENAME :

                return "STATES_GETTING_PACKAGENAME";

            case STATES_GETTING_PACKAGENAME_END :
                return "STATES_GETTING_PACKAGENAME_END";

            case STATES_DOWNLOADING :
                return "STATES_DOWNLOADING";

            case STATES_DOWNLOADING_END :
                return "STATES_DOWNLOADING_END";

            case STATES_INSTALLING :
                return "STATES_INSTALLING";

            case STATES_INSTALLING_END :
                return "STATES_INSTALLING_END";

            case STATES_OPERATING :
                return "STATES_OPERATING";

            case STATES_OPERATING_END :
                return "STATES_OPERATING_END";

            case STATES_UNINSTALLING :
                return "STATES_UNINSTALLING";

            case STATES_UNINSTALLING_END :
                return "STATES_UNINSTALLING_END";

        }

        return "";
    }


    public static void setSTATES(int STATES) {

        ControllerService.STATES = STATES;

        MyService.INVOKE_TYPE = MyService.TYPE_NULL;

    }


    public static void setTestPackageName(String packageName){
        testPackageName = packageName;
    }

    public static String ACTION_STOP = "com.lixiaoqing.action.stop";

    public ControllerService() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 通知栏
        startNotification();

        // 启动线程
        startThread();

        MyBroadCaseReceiver myBroadCaseReceiver = new MyBroadCaseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ControllerService.ACTION_STOP);
        registerReceiver(myBroadCaseReceiver, filter);
    }

    @Override
    public void onDestroy() {

        Log.d("myTest", "onDestroy");

        threadRunning = false;
        thread.interrupt();
        thread = null;

        this.stopForeground(true);

        super.onDestroy();
    }

    private void startNotification(){


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.tittle, "my tittle");

        // 按钮点击事件
        Intent broadcastIntent = new Intent(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.close, pendingIntent);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContent(remoteViews)
                .setOngoing(true)//true，设置他为一个正在进行的通知。;
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;

        this.startForeground(1, notification);
    }

    private void startThread(){

        final WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);

        thread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (threadRunning){

                    //  检查wifi是否开启
                    if (!wifiManager.isWifiEnabled()){
                        wifiManager.setWifiEnabled(true);
                    }

                    Log.d("test", " states : " + getStatesName(getSTATES()));

                    if (getSTATES() == STATES_UNINSTALLING_END){

                        getTestPackageName();

                        try {
                            Thread.sleep(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else if(getSTATES() == STATES_GETTING_PACKAGENAME_END){

                        // 下载之前删除sdcard文件

                        SharedPreferences sharedPreferences = ControllerService.this.getSharedPreferences(ControllerService.this.getPackageName(), Context.MODE_APPEND);
                        sharedPreferences.edit().putLong(Properties.START_TIME, System.currentTimeMillis()).apply();
                        long startTime = sharedPreferences.getLong(Properties.START_TIME, System.currentTimeMillis());

                        cleanJunkFiles(ControllerService.this, startTime);

                        // 下载

                        OpenCloseAPP.downloadApp(ControllerService.this, testPackageName);

                    } else if(getSTATES() == STATES_INSTALLING_END) {

                        setSTATES(STATES_OPERATING);

                        MyService myService = MyService.getInstance();

                        if (null != myService)

                            myService.startTouch(ControllerService.this, testPackageName, needTestTimes, waitTime, prepareTime, strategyType);

                    } else if (getSTATES() == STATES_OPERATING_END){

                        setSTATES(STATES_UNINSTALLING);

                        MyService.INVOKE_TYPE = MyService.TYPE_UNINSTALL_APP;

                        // 卸载安装包

                        OpenCloseAPP.uninstallApp(MyService.getInstance(), testPackageName);

                        ControllerService.setTestPackageName(null);

                    }

                    try { Thread.sleep(1000L); } catch (InterruptedException ignored) {}
                }
            }
        });

        threadRunning = true;
        thread.start();
    }

    class MyBroadCaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("myTest", intent.getAction());

            if (ControllerService.ACTION_STOP.equals(intent.getAction())) {

                ControllerService.this.stopSelf();
            }
        }
    }

    // TODO 获取测试pp信息
    private String prepareGetTestPackageName(){

        RequestPackageNameBean requestPackageNameBean = new RequestPackageNameBean();
        requestPackageNameBean.setIMEI(DeviceTool.getIMEI(this));


        String requestStr = JSON.toJSONString(requestPackageNameBean);
        return new HttpUtils().doPost(Properties.GET_PACKAGE_NAME_URL + "?IMEI=" + requestPackageNameBean.getIMEI(), requestStr);
    }

    private void processingGetTestPackageName(String result){

        Log.d("test", "----1");

        ResponsePackageNameBean responsePackageNameBean = JSON.parseObject(result, ResponsePackageNameBean.class);

        if (null != responsePackageNameBean){

            if (!responsePackageNameBean.isBreak()){

                needTestTimes = responsePackageNameBean.getNeedTestTimes();
                strategyType = responsePackageNameBean.getStrategyType();
                waitTime = responsePackageNameBean.getOnceWaitTime();
                prepareTime = responsePackageNameBean.getPrepareTime();
                testPackageName = responsePackageNameBean.getPackageName();

                setSTATES(STATES_GETTING_PACKAGENAME_END);
                return;
            }
        }

        // 条件不满足， 返回上一状态
        setSTATES(STATES_UNINSTALLING_END);

        // isGettingPackage = false;
        // testPackageName = responsePackageNameBean.getPackageName();
    }

    private void getTestPackageName(){

        setSTATES(STATES_GETTING_PACKAGENAME);

        Single<String> single = Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return prepareGetTestPackageName();
            }
        });

        single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {

                    @Override
                    public void onSuccess(String result) {
                        processingGetTestPackageName(result);
                    }

                    @Override
                    public void onError(Throwable error) {

                        // 状态回到卸载结束，重新开始获取包名

                        setSTATES(STATES_UNINSTALLING_END);
                    }
                });

    }


}
