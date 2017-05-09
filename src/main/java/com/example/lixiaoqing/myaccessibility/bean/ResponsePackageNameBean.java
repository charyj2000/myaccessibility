package com.example.lixiaoqing.myaccessibility.bean;


public class ResponsePackageNameBean {

    private String packageName;
    private long prepareTime;
    private long onceWaitTime;
    private long needTestTimes;
    private boolean isBreak;
    private int strategyType;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getPrepareTime() {
        return prepareTime;
    }

    public void setPrepareTime(long prepareTime) {
        this.prepareTime = prepareTime;
    }



    public long getOnceWaitTime() {
        return onceWaitTime;
    }

    public void setOnceWaitTime(long onceWaitTime) {
        this.onceWaitTime = onceWaitTime;
    }

    public long getNeedTestTimes() {
        return needTestTimes;
    }

    public void setNeedTestTimes(long needTestTimes) {
        this.needTestTimes = needTestTimes;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    public int getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(int strategyType) {
        this.strategyType = strategyType;
    }
}
