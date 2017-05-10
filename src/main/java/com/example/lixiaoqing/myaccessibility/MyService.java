//  添加策略
package com.example.lixiaoqing.myaccessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.alibaba.fastjson.JSON;
import com.example.lixiaoqing.myaccessibility.bean.EventInfo;
import com.example.lixiaoqing.myaccessibility.bean.OperateAccessibilityNodeInfo;
import com.example.lixiaoqing.myaccessibility.bean.RequestAPPExitedBean;
import com.example.lixiaoqing.myaccessibility.bean.RequestTrapInfoBean;
import com.example.lixiaoqing.myaccessibility.bean.RequestUpOperateInfoBean;
import com.example.lixiaoqing.myaccessibility.bean.ResponseAPPExitedBean;
import com.example.lixiaoqing.myaccessibility.bean.ResponseTrapInfoBean;
import com.example.lixiaoqing.myaccessibility.bean.ResponseUpOperateInfoBean;
import com.example.lixiaoqing.myaccessibility.bean.StrategyBean;
import com.example.lixiaoqing.myaccessibility.tools.AccessibilityTool;
import com.example.lixiaoqing.myaccessibility.tools.DeviceTool;
import com.example.lixiaoqing.myaccessibility.tools.HttpUtils;
import com.example.lixiaoqing.myaccessibility.tools.OpenCloseAPP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.lixiaoqing.myaccessibility.tools.AccessibilityTool.click;
import static com.example.lixiaoqing.myaccessibility.tools.AccessibilityTool.findNode;
import static com.example.lixiaoqing.myaccessibility.tools.AccessibilityTool.getHashCode;
import static com.example.lixiaoqing.myaccessibility.tools.AccessibilityTool.getTouchAbleList;
import static com.example.lixiaoqing.myaccessibility.tools.AccessibilityTool.isContentsWordsNode;
import static com.example.lixiaoqing.myaccessibility.tools.DeviceTool.getAndroidID;
import static com.example.lixiaoqing.myaccessibility.tools.DeviceTool.getAndroidVersion;
import static com.example.lixiaoqing.myaccessibility.tools.DeviceTool.getIMEI;
import static com.example.lixiaoqing.myaccessibility.tools.DeviceTool.getLocalIpAddress;
import static com.example.lixiaoqing.myaccessibility.tools.DeviceTool.getModel;
import static com.example.lixiaoqing.myaccessibility.tools.ThreadTool.sleep;


public class MyService extends AccessibilityService {


    private static String TAG ="lxq_test";
    private static String serverAddress;


    public static final int TYPE_NULL = 0;
    public static final int TYPE_KILL_APP = 1;
    public static final int TYPE_INSTALL_APP = 2;
    public static final int TYPE_UNINSTALL_APP = 3;
    public static final int TYPE_OPERATE_APP = 4;

    public static int INVOKE_TYPE = TYPE_NULL;


    private static String targetPackage = "";
    private Context context;

    private static long testTimes = 0L;
    private static long needTestTimes = 60;

    private static long waitTime = 3 * 1000L;
    private static long prepareTime = 10 * 1000L;
    private static int strategyType = 0;

    private static Thread operateThread = null;


    private static AccessibilityEvent rootEvent;
    private static String nowPackageName=null;
    private static AccessibilityNodeInfo rootNode;
    private static String hashCode;
    private static boolean isOperating = false;

    private static List<OperateAccessibilityNodeInfo> operateAbleList;

    private static MyService myService = null;

    @Nullable
    public static MyService getInstance(){

        return myService;
    }

    synchronized public boolean startTouch(Context context_, String packageName_, long needTestTimes_, long waitTime_, long prepareTime_, int strategyType_){

        if (null != operateThread){
            Log.d("myTest", " startTouch running");
            return false;
        }

        // 删除安装包
        OpenCloseAPP.deleteApp(packageName_);

        // 初始化线程
        operateThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Thread.sleep(prepareTime);

                    while (testTimes++ < needTestTimes){

                        Thread.sleep(waitTime);

                        while(isOperating){
                            Thread.sleep(200L);
                        }

                        operate();
                    }

                } catch (Exception ignored){}
            }
        });

        ControllerService.setSTATES(ControllerService.STATES_OPERATING);
        // 切换辅助功能处理条件
        INVOKE_TYPE = TYPE_OPERATE_APP;

        context = context_;
        targetPackage = packageName_;
        needTestTimes = needTestTimes_;
        waitTime = waitTime_;
        prepareTime = prepareTime_;
        strategyType = strategyType_;

        Log.d("myTest", " startTouch 1" + targetPackage + "    " +  needTestTimes + "     " + waitTime + "    " + prepareTime);

        // 启动目标APP
        OpenCloseAPP.startAPP(context_, packageName_);

        // 启动操作线程

        testTimes = 0;

        // 点击策略清空
        Strategy.clean();

        operateThread.start();

        return true;
    }

    public static void setServerIP(String insServerAddress){

        serverAddress = insServerAddress;
    }

    @Override
    protected void onServiceConnected() {
        myService = this;

        MyBroadCaseReceiver myBroadCaseReceiver = new MyBroadCaseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ControllerService.ACTION_STOP);
        registerReceiver(myBroadCaseReceiver, filter);

        super.onServiceConnected();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        myService = null;
        return super.onUnbind(intent);
    }

    @Override
    protected boolean onGesture(int gestureId) {
        return super.onGesture(gestureId);
    }

    @Override
    public List<AccessibilityWindowInfo> getWindows() {
        return super.getWindows();
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        return super.getRootInActiveWindow();
    }

    @Override
    public AccessibilityNodeInfo findFocus(int focus) {
        return super.findFocus(focus);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        return super.getSystemService(name);
    }

    //接收按键事件
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return true;
    }

//    服务中断，如授权关闭或者将服务杀死
    @Override
    public void onInterrupt() {
        Log.d(TAG, "server be killed");
    }

//   接收事件,如触发了通知栏变化、界面变化等
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        // 跳过小米安全中心

        if(null != event && null != event.getPackageName() && "com.miui.securitycenter".equals(event.getPackageName().toString())){

            AccessibilityNodeInfo accessibilityNodeInfo = findNode(getRootInActiveWindow(),"允许");
            click(accessibilityNodeInfo);

            return;
        }

        // 跳过app意外停止
        if(null != event && null != event.getPackageName() && "com.android.packageinstaller".equals(event.getPackageName().toString())){

            if(null != findNode(getRootInActiveWindow(),"解析程序包时出现问题")){

                AccessibilityNodeInfo accessibilityNodeInfo = findNode(getRootInActiveWindow(),"确定");
                click(accessibilityNodeInfo);
                // 这时状态正在安装进行中，需要跳转到重新下载
                ControllerService.setSTATES(ControllerService.STATES_UNINSTALLING_END);
                return;
            }
        }

        // 跳过app意外停止
        if(null != event && null != event.getPackageName() && "com.google.android.packageinstaller".equals(event.getPackageName().toString())){

            if(null != findNode(getRootInActiveWindow(),"解析软件包时出现问题")){

                AccessibilityNodeInfo accessibilityNodeInfo = findNode(getRootInActiveWindow(),"确定");
                click(accessibilityNodeInfo);
                // 这时状态正在安装进行中，需要跳转到重新下载
                ControllerService.setSTATES(ControllerService.STATES_UNINSTALLING_END);
                return;
            }
        }


        // 程序无响应，关闭。
        if(null != event && null != event.getPackageName()){

            if(null != findNode(getRootInActiveWindow(),"无响应。要将其关闭吗？")){

                AccessibilityNodeInfo accessibilityNodeInfo = findNode(getRootInActiveWindow(),"确定");
                click(accessibilityNodeInfo);
                return;
            }
        }

        // 跳过已经停止运行
        if(null != event && null != event.getPackageName() && "android".equals(event.getPackageName().toString())){

            if(true == isContentsWordsNode(getRootInActiveWindow(),"已停止运行。")){

                AccessibilityNodeInfo accessibilityNodeInfo = findNode(getRootInActiveWindow(),"确定");
                click(accessibilityNodeInfo);
                return;
            }
        }




        switch (INVOKE_TYPE) {
            case TYPE_KILL_APP:
                AccessibilityTool.processKillApplication(event,  getRootInActiveWindow());
                break;
            case TYPE_INSTALL_APP:
                AccessibilityTool.processinstallApplication(event, getRootInActiveWindow());
                break;
            case TYPE_UNINSTALL_APP:
                AccessibilityTool.processUninstallApplication(event, getRootInActiveWindow());
                break;
            case TYPE_OPERATE_APP:
                rootEvent = event;
                nowPackageName = null == rootEvent.getPackageName() ? "" : rootEvent.getPackageName().toString();
                rootNode = this.getRootInActiveWindow();
                LogNote(event);
            default:
                break;
        }


    }

    private void operate(){

        Log.d("myTest", " operate");
        if (testTimes >= needTestTimes){

            doAppExited(targetPackage, true);
            OpenCloseAPP.close(targetPackage);

            targetPackage = "";
            operateThread = null;

            Log.d("myTest", " operate    end");
            isOperating = false;
            ControllerService.setSTATES(ControllerService.STATES_OPERATING_END);
            return ;
        }

        isOperating = true;

        if (null == rootEvent){
            Log.d("myTest", " null == rootEvent ");
            isOperating = false;
            return ;
        }

        // 已经跳出了app
        if (!nowPackageName.equals(targetPackage)) {

            Log.d("myTest", " 已经跳出了app, 重启app ");
            doAppExited(targetPackage, false);
            OpenCloseAPP.startAPP(context, targetPackage);
            try {
                Thread.sleep(prepareTime);
            } catch (InterruptedException ignored) {}

            isOperating = false;

        } else { // 如果条件满足进行满足

            operateAbleList = getTouchAbleList(rootNode);
            hashCode = getHashCode(rootNode);

            Log.d("myTest", " operateAbleList  : " + operateAbleList.size());
            Log.d("myTest", " hashCode : " + hashCode);

            if (operateAbleList.size() != 0)
                getTrapInfo(targetPackage, hashCode);
            else
                isOperating = false;

        }
    }

    private void LogNote(AccessibilityEvent event){

        if (null == event){
            Log.d(TAG, "event is null !");
            return;
        }

        if(null == event.getSource()){
            return;
        }

        EventInfo eventInfo = new EventInfo();
        eventInfo.setIp(getLocalIpAddress(this));
        eventInfo.setImei(getIMEI(this));
        eventInfo.setAndroidID(getAndroidID(this));
        if (Build.VERSION.SDK_INT>=16){
            eventInfo.setMovementGranularity(event.getMovementGranularity());
            eventInfo.setAction(event.getAction());
        }
        eventInfo.setAddedCount(event.getAddedCount());
        eventInfo.setBeforeText(null == event.getBeforeText() ? null : event.getBeforeText().toString());
        eventInfo.setChecked(event.isChecked());
        eventInfo.setContentDescription(null == event.getContentDescription() ? null : event.getContentDescription().toString());
        eventInfo.setCurrentItemIndex(event.getCurrentItemIndex());
        eventInfo.setEnabled(event.isEnabled());
        eventInfo.setEventTime(System.currentTimeMillis());
        eventInfo.setEventType(event.getEventType());
        eventInfo.setFromIndex(event.getFromIndex());
        eventInfo.setFullScreen(event.isFullScreen());
        eventInfo.setItemCount(event.getItemCount());
        if (Build.VERSION.SDK_INT>=15){
            eventInfo.setMaxScrollX(event.getMaxScrollX());
            eventInfo.setMaxScrollY(event.getMaxScrollY());
        }

        eventInfo.setPackageName(null == event.getPackageName() ? null : event.getPackageName().toString());

        eventInfo.setParcelableData(event.getParcelableData());
        eventInfo.setPassword(event.isPassword());
        eventInfo.setRecordCount(event.getRecordCount());
        eventInfo.setRemovedCount(event.getRemovedCount());
        eventInfo.setScrollable(event.isScrollable());
        eventInfo.setScrollX(event.getScrollX());
        eventInfo.setScrollY(event.getScrollY());

        List<String> eventText = null;
        if (null != event.getText() && !event.getText().isEmpty()){
            eventText = new ArrayList<>(event.getText().size());
            for (int i=0; i<event.getText().size(); i ++){
                if (null != event.getText().get(i)){
                    eventText.add(event.getText().get(i).toString());
                }
            }
        }

        eventInfo.setText(eventText);
        eventInfo.setToIndex(event.getToIndex());

        eventInfo.setBase(AccessibilityTool.accessibilityNoteInfoToNoteInfo(this.getRootInActiveWindow()));
        eventInfo.setSource(AccessibilityTool.accessibilityNoteInfoToNoteInfo(event.getSource()));

        String json = JSON.toJSONString(eventInfo);
        Log.d(TAG, json);

        String params = "&ap=" + (null == event.getPackageName() ? "" : event.getPackageName().toString());
        params += "&time=" + System.currentTimeMillis() ;
        params += "&ip=" + getLocalIpAddress(this);
        params += "&dm=" + getModel();
        params += "&os=" + getAndroidVersion();

        new HttpUtils().httpGet(Properties.NOTIFICATION_URL_START+params);

        String record = eventInfo.getImei() + "\t" + eventInfo.getPackageName() + "\t" + eventInfo.getEventTime() + "\t" + eventInfo.getEventType() + "\t" + json;
        new HttpUtils().httpPost(Properties.NET_ADDRESS_HEAD+getServerIP()+Properties.NET_ADDRESS_END+"?id=" + eventInfo.getImei(), record);

        // new HttpUtils().httpGet(Properties.NOTIFICATION_URL_END+params);
    }

    private String getServerIP(){

        if (null == serverAddress){
            SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_APPEND);
            serverAddress = sharedPreferences.getString(Properties.SHAREDPREFERENCES_NAME_SERVER_IP,"");
        }

        return serverAddress;
    }

    // TODO 请求当前帧的坑位置
    private String prepareGetTrapInfo(String packageName, String frameID){

        RequestTrapInfoBean requestTrapInfoBean = new RequestTrapInfoBean();
        requestTrapInfoBean.setIMEI(DeviceTool.getIMEI(MyService.this));
        requestTrapInfoBean.setPackageName(packageName);
        requestTrapInfoBean.setFrameID(frameID);

        String requestStr = JSON.toJSONString(requestTrapInfoBean);
        return new HttpUtils().doPost(Properties.GET_TRAP_INFO_URL + "?IMEI="+ DeviceTool.getIMEI(MyService.this) +"&packageName="+packageName+"&frameID="+frameID, requestStr);
    }

    private void processingGetTrapInfo(String result){

        Log.d("myTest", " processingGetTrapInfo :" + result);
        boolean isInTrap = false;
        String[] trapNOArray = new String[0];

        if (null != result){

            ResponseTrapInfoBean responseTrapInfoBean = JSON.parseObject(result, ResponseTrapInfoBean.class);

            if (null != responseTrapInfoBean){

                trapNOArray = responseTrapInfoBean.getTrapNO();

                isInTrap = responseTrapInfoBean.isInTrap();
            }
        }

        Log.d("test", "isInTrap : " + isInTrap);
        if (isInTrap){
            // 重启，重启后结束本次点击，进入新的点击。
            reStart();
            isOperating = false;
            return;
        }

        // 可点击位置放入list
        List<String> list = new ArrayList<>();
        for (int i = 0; i < operateAbleList.size(); i++) {
            list.add(i+"");
        }

        // 需要返回是否重启的状态

        StrategyBean strategyBean = Strategy.getTouchID(hashCode, list, trapNOArray, strategyType);

        if(null != strategyBean ){

            if (null != strategyBean.getTouchID()){

                String touchID = strategyBean.getTouchID();

                // 发送点击请求
                upOperateInfo(nowPackageName, hashCode, touchID);

                // 点击
                AccessibilityTool.operate(operateAbleList.get(Integer.parseInt(touchID)));

            } else {
                Log.d("test", "touchID is == strategyBean");
            }

            // 如果策略要求app在执行操作后重启，则。。
            if(strategyBean.isNeedRestart()){
                // 点击后延时1秒给app加载资源，请求网络的时间。
                sleep(1000L);
                reStart();
            }

        } else {

        }

        isOperating = false;
    }

    // 重启
    private void reStart(){

        OpenCloseAPP.startAPP(context, targetPackage);

        sleep(prepareTime);
    }

    private void getTrapInfo(final String packageName, final String frameID){

        Single<String> single = Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return prepareGetTrapInfo(packageName, frameID);
            }
        });

        single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {

                    @Override
                    public void onSuccess(String result) {
                        processingGetTrapInfo(result);
                    }

                    @Override
                    public void onError(Throwable error) {
                        processingGetTrapInfo(null);
                    }
                });
    }

    // TODO 上传点击位置
    private String prepareUpOperateInfo(String packageName, String frameID, String nodeNO){

        RequestUpOperateInfoBean requestUpOperateInfoBean = new RequestUpOperateInfoBean();
        requestUpOperateInfoBean.setIMEI(DeviceTool.getIMEI(this));
        requestUpOperateInfoBean.setPackageName(packageName);
        requestUpOperateInfoBean.setFrameID(frameID);
        requestUpOperateInfoBean.setNodeNO(nodeNO);

        String requestStr = JSON.toJSONString(requestUpOperateInfoBean);
        return new HttpUtils().doPost(Properties.UP_OPERATE_INFO_URL + "?IMEI="+DeviceTool.getIMEI(this)+"&packageName="+packageName+"&frameID="+frameID+"&nodeNO="+nodeNO, requestStr);
    }

    private void processingUpOperateInfo(String result){

        if(null != result){

            ResponseUpOperateInfoBean responseUpOperateInfoBean = JSON.parseObject(result, ResponseUpOperateInfoBean.class);
            Log.d("myTest", "上传将要点击的位置： " + responseUpOperateInfoBean.getState());
        }
    }

    private void upOperateInfo(final String packageName, final String frameID, final String nodeNO){


        Single<String> tvShowSingle = Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return prepareUpOperateInfo(packageName, frameID, nodeNO);
            }
        });

        tvShowSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {

                    @Override
                    public void onSuccess(String result) {
                        processingUpOperateInfo(result);
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }

    // TODO 上传app退出
    private String prepareAppExited(String packageName, boolean isSafetyExit){

        RequestAPPExitedBean requestAPPExitedBean = new RequestAPPExitedBean();
        requestAPPExitedBean.setIMEI(DeviceTool.getIMEI(this));
        requestAPPExitedBean.setPackageName(packageName);
        requestAPPExitedBean.setSafetyExit(isSafetyExit);

        String requestStr = JSON.toJSONString(requestAPPExitedBean);
        return new HttpUtils().doPost(Properties.APP_EXITED_URL + "?IMEI="+DeviceTool.getIMEI(this)+"&packageName="+ targetPackage + "&isSafetyExit=" + isSafetyExit, requestStr);
    }

    private void processingAppExited(String result){

        if (null == result)

            return;

        ResponseAPPExitedBean responseAPPExitedBean = JSON.parseObject(result, ResponseAPPExitedBean.class);

        if (null != responseAPPExitedBean && null != responseAPPExitedBean.getState())

            Log.d("myTest", "app退出通知请求" + responseAPPExitedBean.getState());
    }

    private void doAppExited(final String packageName, final boolean isSafetyExit){


        Single<String> single = Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return prepareAppExited(packageName, isSafetyExit);
            }
        });

        single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {

                    @Override
                    public void onSuccess(String result) {
                        processingAppExited(result);
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }

    class MyBroadCaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("myTest", intent.getAction());

            if (ControllerService.ACTION_STOP.equals(intent.getAction())) {

                needTestTimes = 0;
            }
        }
    }

}
