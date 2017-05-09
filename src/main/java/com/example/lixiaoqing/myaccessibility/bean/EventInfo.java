package com.example.lixiaoqing.myaccessibility.bean;


import android.os.Parcelable;

import java.util.List;

@SuppressWarnings("unused")
public class EventInfo {

    private int eventType;
    private long eventTime;
    private String packageName;
    private int movementGranularity;
    private int action;
    private List<String> text;
    private String contentDescription;
    private int itemCount;
    private int currentItemIndex;
    private boolean isEnabled;
    private boolean isPassword;
    private boolean isChecked;
    private boolean isFullScreen;
    private boolean scrollable;
    private String beforeText;
    private int fromIndex;
    private int toIndex ;
    private int scrollX;
    private int scrollY;
    private int maxScrollX;
    private int maxScrollY;
    private int addedCount;
    private int removedCount;
    private Parcelable parcelableData;
    private int recordCount;

    private NoteInfo base;
    private  NoteInfo source;
    private String imei;
    private String ip;
    private String androidID;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getMovementGranularity() {
        return movementGranularity;
    }

    public void setMovementGranularity(int movementGranularity) {
        this.movementGranularity = movementGranularity;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    public void setCurrentItemIndex(int currentItemIndex) {
        this.currentItemIndex = currentItemIndex;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isPassword() {
        return isPassword;
    }

    public void setPassword(boolean password) {
        isPassword = password;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public String getBeforeText() {
        return beforeText;
    }

    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public int getScrollX() {
        return scrollX;
    }

    public void setScrollX(int scrollX) {
        this.scrollX = scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public int getMaxScrollX() {
        return maxScrollX;
    }

    public void setMaxScrollX(int maxScrollX) {
        this.maxScrollX = maxScrollX;
    }

    public int getMaxScrollY() {
        return maxScrollY;
    }

    public void setMaxScrollY(int maxScrollY) {
        this.maxScrollY = maxScrollY;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }

    public int getRemovedCount() {
        return removedCount;
    }

    public void setRemovedCount(int removedCount) {
        this.removedCount = removedCount;
    }

    public Parcelable getParcelableData() {
        return parcelableData;
    }

    public void setParcelableData(Parcelable parcelableData) {
        this.parcelableData = parcelableData;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public NoteInfo getBase() {
        return base;
    }

    public void setBase(NoteInfo base) {
        this.base = base;
    }

    public NoteInfo getSource() {
        return source;
    }

    public void setSource(NoteInfo source) {
        this.source = source;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }
}
