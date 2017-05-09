package com.example.lixiaoqing.myaccessibility.bean;


public class RequestAPPExitedBean {

    private String IMEI;
    private String packageName;
    private boolean isSafetyExit;


    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSafetyExit() {
        return isSafetyExit;
    }

    public void setSafetyExit(boolean safetyExit) {
        isSafetyExit = safetyExit;
    }
}
