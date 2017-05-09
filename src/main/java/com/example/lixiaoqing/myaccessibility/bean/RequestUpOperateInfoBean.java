package com.example.lixiaoqing.myaccessibility.bean;


public class RequestUpOperateInfoBean {

    private String IMEI;
    private String packageName;
    private String frameID;
    private String nodeNO;


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

    public String getFrameID() {
        return frameID;
    }

    public void setFrameID(String frameID) {
        this.frameID = frameID;
    }

    public String getNodeNO() {
        return nodeNO;
    }

    public void setNodeNO(String nodeNO) {
        this.nodeNO = nodeNO;
    }
}
