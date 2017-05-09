package com.example.lixiaoqing.myaccessibility.bean.strategy;


import java.util.*;

public class StrategyTreeNode {

    private String frameID;

    // 可被点击的列表
    private Map<String, StrategyTreeNode> touchID;

    // 父节点
    private StrategyTreeNode father;
    // 父节点的点击位置
    private String fatherTouchID;
    // 最后操作位置
    private String lastOperateTouchID;
    // 目标操作位
    private String targetTouchID;
    // 节点所在层数
    private int floor;


    public String getFrameID() {
        return frameID;
    }

    public void setFrameID(String frameID) {
        this.frameID = frameID;
    }

    public Map<String, StrategyTreeNode> getTouchID() {
        return touchID;
    }

    public void setTouchID(List<String> touchIDList) {

        Map<String, StrategyTreeNode> map = new LinkedHashMap<String, StrategyTreeNode>();

        if (null != touchIDList && touchIDList.size() != 0){

            for (String touchID : touchIDList) {
                map.put(touchID, null);
            }
        }

        this.touchID = map;
    }

    public StrategyTreeNode getFather() {
        return father;
    }

    public void setFather(StrategyTreeNode father) {
        this.father = father;
    }

    public String getFatherTouchID() {
        return fatherTouchID;
    }

    public void setFatherTouchID(String fatherTouchID) {
        this.fatherTouchID = fatherTouchID;
    }

    public String getLastOperateTouchID() {
        return lastOperateTouchID;
    }

    public void setLastOperateTouchID(String lastOperateTouchID) {
        this.lastOperateTouchID = lastOperateTouchID;
    }

    public String getTargetTouchID() {
        return targetTouchID;
    }

    public void setTargetTouchID(String targetTouchID) {
        this.targetTouchID = targetTouchID;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
