package com.example.lixiaoqing.myaccessibility.bean;

import android.view.accessibility.AccessibilityNodeInfo;



public class OperateAccessibilityNodeInfo {

    String operateType;
    public static String OPERATE_TYPE_CLICK = "OPERATE_TYPE_CLICK";
    public static String OPERATE_TYPE_SCROLLABLE = "OPERATE_TYPE_SCROLLABLE";
    public static String OPERATE_TYPE_EDITABLE = "OPERATE_TYPE_EDITABLE";
    AccessibilityNodeInfo accessibilityNodeInfo;


    public OperateAccessibilityNodeInfo(String operateType, AccessibilityNodeInfo accessibilityNodeInfo){
        this.operateType = operateType;
        this.accessibilityNodeInfo = accessibilityNodeInfo;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return accessibilityNodeInfo;
    }

    public void setAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.accessibilityNodeInfo = accessibilityNodeInfo;
    }
}
