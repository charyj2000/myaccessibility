package com.example.lixiaoqing.myaccessibility.tools;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.alibaba.fastjson.JSON;
import com.example.lixiaoqing.myaccessibility.ControllerService;
import com.example.lixiaoqing.myaccessibility.bean.NoteInfo;
import com.example.lixiaoqing.myaccessibility.bean.OperateAccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;


public class AccessibilityTool {

    public static String getHashCode(AccessibilityNodeInfo accessibilityNodeInfo){

        List<Rect> list = new ArrayList<>();

        nodeToRectList(accessibilityNodeInfo, list);

        StringBuilder stringBuilder = new StringBuilder();

        for (Rect rect: list) {
            stringBuilder.append(rect.top).append(rect.bottom).append(rect.left).append(rect.right);
        }

        return Hash.Encrypt(stringBuilder.toString());
    }

    public static void nodeToRectList(AccessibilityNodeInfo root, List<Rect> list) {

        if (root == null)
            return;

        Rect outBounds = new Rect();
        root.getBoundsInScreen(outBounds);
//        Log.d("test", outBounds.height() + "-" + outBounds.width() + "  :  " + root.getText() + root.getClassName());
        list.add(outBounds);

        for (int j = 0; j < root.getChildCount(); j++) {
            nodeToRectList(root.getChild(j), list);
        }
    }

    public static List<OperateAccessibilityNodeInfo> getTouchAbleList(AccessibilityNodeInfo accessibilityNodeInfo){

        List<OperateAccessibilityNodeInfo> list = new ArrayList<>();

        nodeToList(accessibilityNodeInfo, list);

        return list;
    }

    public static NoteInfo accessibilityNoteInfoToNoteInfo(AccessibilityNodeInfo accessibilityNodeInfo){

        if (null == accessibilityNodeInfo){
            return null;
        }

        NoteInfo noteInfo = new NoteInfo();
        JSON.toJSONString(noteInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            noteInfo.setActions(accessibilityNodeInfo.getActionList());
//            noteInfo.setError(null == accessibilityNodeInfo.getError() ? null : accessibilityNodeInfo.getError().toString());
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

            noteInfo.setChildren(accessibilityNoteInfoToNoteInfo(accessibilityNodeInfo.getChild(i)));
        }

        return noteInfo;
    }

    public static void nodeToList(AccessibilityNodeInfo root, List<OperateAccessibilityNodeInfo> list) {

        if (root == null)
            return;

        if (root.isClickable()){
            list.add(new OperateAccessibilityNodeInfo(OperateAccessibilityNodeInfo.OPERATE_TYPE_CLICK, root));
        }

        if (root.isScrollable()){
            list.add(new OperateAccessibilityNodeInfo(OperateAccessibilityNodeInfo.OPERATE_TYPE_SCROLLABLE, root));
        }

        if (root.isEditable()){
            list.add(new OperateAccessibilityNodeInfo(OperateAccessibilityNodeInfo.OPERATE_TYPE_EDITABLE, root));
        }

//        Rect outBounds = new Rect();
//        root.getBoundsInScreen(outBounds);
//        Log.d("test", outBounds.height() + "-" + outBounds.width() + "  :  " + root.getText() + root.getClassName());

        for (int j = 0; j < root.getChildCount(); j++) {
            nodeToList(root.getChild(j), list);
        }
    }

    public static void logNode(AccessibilityNodeInfo root) {
        if (root == null)
            return;
        Rect outBounds = new Rect();
        root.getBoundsInScreen(outBounds);
        Log.d("test", outBounds.height() + "-" + outBounds.width() + "  :  " + root.getText() + root.getClassName());

        for (int j = 0; j < root.getChildCount(); j++) {
            logNode(root.getChild(j));
        }
    }


    public static void operate(OperateAccessibilityNodeInfo operateAccessibilityNodeInfo){

        Log.d("test", "operate type : " + operateAccessibilityNodeInfo.getOperateType());

        if (operateAccessibilityNodeInfo.getOperateType().equals(OperateAccessibilityNodeInfo.OPERATE_TYPE_CLICK)){

            click(operateAccessibilityNodeInfo.getAccessibilityNodeInfo());
        } else if (operateAccessibilityNodeInfo.getOperateType().equals(OperateAccessibilityNodeInfo.OPERATE_TYPE_EDITABLE)){

            setText(operateAccessibilityNodeInfo.getAccessibilityNodeInfo(),"123456");
        } else if (operateAccessibilityNodeInfo.getOperateType().equals(OperateAccessibilityNodeInfo.OPERATE_TYPE_SCROLLABLE)){

            scroll(operateAccessibilityNodeInfo.getAccessibilityNodeInfo());
        }
    }

    public static boolean setText(AccessibilityNodeInfo node, String text) {

        if (null == node)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Log.d("test", "---setText--");
            try {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                Log.d("test", "node ACTION_SET_TEXT");
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
//        Log.d("test", "---setText1--");

        return false;
    }


    public static boolean click(AccessibilityNodeInfo node) {

        if (null == node)
            return false;

        if (node.isClickable()) {
//            Log.d("test", "---click--");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d("test", "node ACTION_CLICK");
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean scroll(AccessibilityNodeInfo node) {

        if (null == node)
            return false;

        if (node.isScrollable()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    Log.d("test", "node ACTION_SCROLL_FORWARD");
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public static AccessibilityNodeInfo findNode(AccessibilityNodeInfo root, String name) {

        if (null == root)
            return null;


        List<AccessibilityNodeInfo> next_nodes = null;

        try {
            next_nodes = root.findAccessibilityNodeInfosByText(name);
        } catch (Exception ignored) {
        }

        if (next_nodes != null && !next_nodes.isEmpty()) {

            if (next_nodes.size() == 1)
                return next_nodes.get(0);

            AccessibilityNodeInfo node;

            for (int i = 0; i < next_nodes.size(); i++) {
                node = next_nodes.get(i);
                if(null != node.getText() && node.getText().toString().equals(name))
                    return node;
            }
        }

        return null;
    }

    public static boolean isContentsWordsNode(AccessibilityNodeInfo root, String name) {

        if (null == root)
            return false;


        List<AccessibilityNodeInfo> next_nodes = null;

        try {

            next_nodes = root.findAccessibilityNodeInfosByText(name);

        } catch (Exception ignored) {}

        if (next_nodes != null && !next_nodes.isEmpty()) {

            return true;
        }

        return false;
    }

    public static AccessibilityNodeInfo findEditText(AccessibilityNodeInfo root, String name) {

//        AccessibilityNodeInfo root = getRootInActiveWindow();

        if (null == root) {
            return null;
        }

        return findEditTextChild(root, name);
    }

    public static AccessibilityNodeInfo findEditTextChild(AccessibilityNodeInfo root, String name) {

        if (null == root)
            return null;

        if (null != root.getClassName()
                && root.getClassName().toString().equals("android.widget.EditText")
                && null != root.getText()
                && root.getText().toString().contains(name))

            return root;

        for (int j = 0; j < root.getChildCount(); j++) {

            AccessibilityNodeInfo child = findEditText(root.getChild(j), name);

            if (null != child)
                return child;

        }

        return null;
    }


    public static AccessibilityNodeInfo findNearestBottomBrother(AccessibilityNodeInfo node){

        if (null == node)
            return null;

        AccessibilityNodeInfo parent = node.getParent();
        AccessibilityNodeInfo nearestBrother = null;

        Rect outBounds = new Rect();
        node.getBoundsInScreen(outBounds);
        int topDistances = Integer.MAX_VALUE, brotherTop, selfTop = outBounds.top;
//        Log.d("---", selfTop + "selfTop");

        for (int i=0; i<parent.getChildCount(); i++){

            parent.getChild(i).getBoundsInScreen(outBounds);
            brotherTop = outBounds.top;
//            Log.d("---", brotherTop + "brotherTop");

            if (brotherTop <= selfTop)
                continue;

            if ((brotherTop - selfTop) < topDistances){
                nearestBrother = parent.getChild(i);
                topDistances = brotherTop - selfTop;
            }
        }

        return nearestBrother;
    }

    public static AccessibilityNodeInfo findNearestRightBrother(AccessibilityNodeInfo node){

        if (null == node)
            return null;

        AccessibilityNodeInfo parent = node.getParent();
        AccessibilityNodeInfo nearestBrother = null;

        Rect outBounds = new Rect();
        node.getBoundsInScreen(outBounds);
        int leftDistances = Integer.MAX_VALUE, brotherLeft, selfLeft = outBounds.left;
//        Log.d("---", selfTop + "selfTop");

        for (int i=0; i<parent.getChildCount(); i++){

            parent.getChild(i).getBoundsInScreen(outBounds);
            brotherLeft = outBounds.left;
//            Log.d("---", brotherTop + "brotherTop");

            if (brotherLeft <= selfLeft)
                continue;

            if ((brotherLeft - selfLeft) < leftDistances){
                nearestBrother = parent.getChild(i);
                leftDistances = brotherLeft - selfLeft;
            }
        }

        return nearestBrother;
    }

    // 安装app
    public static void processinstallApplication(AccessibilityEvent event, AccessibilityNodeInfo root) {
        if (event == null || event.getPackageName() == null || !matchingApplication(event)) {
            return;
        }


        traverseNode(root);

        if (root != null) {

            findAndPerformAction("安装", root);

            findAndPerformAction("下一步", root);

            findAndPerformAction("确认", root);

            boolean result = findAndPerformAction("完成", root);

            if (result){

                // 安装完成，同步状态

                ControllerService.setSTATES(ControllerService.STATES_INSTALLING_END);

            }
        }

        Log.d("test", "--------------------------------------------------------------------------------");
    }

    // 关闭app
    public static void processKillApplication(AccessibilityEvent event, AccessibilityNodeInfo root) {

        if (event.getPackageName().equals("com.android.settings")) {

            findAndPerformAction("强行停止", root);

            findAndPerformAction("确定", root);
        }
    }

    // 卸载app
    public static void processUninstallApplication(AccessibilityEvent event, AccessibilityNodeInfo root) {

        if (matchingApplication(event)) {

            boolean result = findAndPerformAction("确定", root);

            if (result){

                // 卸载完成

                ControllerService.setSTATES(ControllerService.STATES_UNINSTALLING_END);
            }
        }
    }

    private static boolean matchingApplication(AccessibilityEvent event) {
        if (event == null) {
            return false;
        }

        Log.d("test", " package name is : " + event.getPackageName());
        Log.d("test", " package name is : " + event.getPackageName());
        traverseNode(event.getSource());

        if ("com.android.packageinstaller".equals(event.getPackageName())
                ||"com.google.android.packageinstaller".equals(event.getPackageName())
                || "com.miui.packageinstaller".equals(event.getPackageName())
                || "com.lenovo.safecenter".equals(event.getPackageName())
                || "com.sec.android.app.capabilitymanager".equals(event.getPackageName())) {

            return true;
        }

        return false;
    }

    private static void traverseNode(AccessibilityNodeInfo node) {
        if (null == node)
            return;

        final int count = node.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                traverseNode(childNode);
            }
        } else {
            CharSequence text = node.getText();
            Log.d("test", "Node text = " + text);
        }

    }

    private static boolean findAndPerformAction(String text, AccessibilityNodeInfo source) {

        if (source == null)

            return false;

        List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText(text);

        if (nodes != null && !nodes.isEmpty()) {

            AccessibilityNodeInfo node;

            for (int i = 0; i < nodes.size(); i++) {

                node = nodes.get(i);

                performActionClick(node);
            }

            return true;
        }

        return false;
    }

    private static void performActionClick(AccessibilityNodeInfo node) {
        if (node == null) {
            return;
        }

        if (isButton(node) || isTextView(node) || isView(node)) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private static boolean isButton(AccessibilityNodeInfo node) {
        return node.getClassName().equals("android.widget.Button") && node.isEnabled();
    }

    private static boolean isTextView(AccessibilityNodeInfo node) {
        return node.getClassName().equals("android.widget.TextView") && node.isEnabled();
    }

    private static boolean isView(AccessibilityNodeInfo node) {
        return node.getClassName().equals("android.widget.View") && node.isEnabled();
    }



}
