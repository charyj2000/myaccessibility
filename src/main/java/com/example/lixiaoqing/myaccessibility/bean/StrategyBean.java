package com.example.lixiaoqing.myaccessibility.bean;


public class StrategyBean {

    private boolean isNeedRestart;
    private String touchID;

    public boolean isNeedRestart() {
        return isNeedRestart;
    }

    public void setNeedRestart(boolean needRestart) {
        isNeedRestart = needRestart;
    }

    public String getTouchID() {
        return touchID;
    }

    public void setTouchID(String touchID) {
        this.touchID = touchID;
    }
}
