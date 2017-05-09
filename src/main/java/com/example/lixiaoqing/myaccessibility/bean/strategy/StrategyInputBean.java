package com.example.lixiaoqing.myaccessibility.bean.strategy;

import java.util.List;

public class StrategyInputBean {

    private String frameID;
    private List<String> list;
    private String[] trapNOArray;

    public String getFrameID() {
        return frameID;
    }

    public void setFrameID(String frameID) {
        this.frameID = frameID;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String[] getTrapNOArray() {
        return trapNOArray;
    }

    public void setTrapNOArray(String[] trapNOArray) {
        this.trapNOArray = trapNOArray;
    }
}
