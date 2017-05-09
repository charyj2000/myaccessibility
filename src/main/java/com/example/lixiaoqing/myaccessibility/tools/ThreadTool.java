package com.example.lixiaoqing.myaccessibility.tools;

public class ThreadTool {

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
