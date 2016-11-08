package com.example.lixiaoqing.myaccessibility.bean;


import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class NoteInfo {

    private Rect rectInScreen;
    private String packageName;
    private String className;
    private String text;
    private String error;
    private int maxTextLength;
    private String contentDescription;
    private String viewIdResName;
    private boolean checkable;
    private boolean checked;
    private boolean focusable;
    private boolean focused;
    private boolean selected;
    private boolean clickable;
    private boolean longClickable;
    private boolean enabled;
    private boolean password;
    private boolean scrollable;
    private List<AccessibilityNodeInfo.AccessibilityAction> actions;
    private List<NoteInfo> children;

    public Rect getRectInScreen() {
        return rectInScreen;
    }

    public void setRectInScreen(Rect rectInScreen) {
        this.rectInScreen = rectInScreen;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getViewIdResName() {
        return viewIdResName;
    }

    public void setViewIdResName(String viewIdResName) {
        this.viewIdResName = viewIdResName;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isLongClickable() {
        return longClickable;
    }

    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public List<AccessibilityNodeInfo.AccessibilityAction> getActions() {
        return actions;
    }

    public void setActions(List<AccessibilityNodeInfo.AccessibilityAction> actions) {
        this.actions = actions;
    }

    public List<NoteInfo> getChildren() {
        return children;
    }

    public void setChildren(NoteInfo child) {

//        this.children = children;

        if (null != child){

            if (null == this.children){
                this.children = new ArrayList<>();
            }

            children.add(child);
        }
    }
}
