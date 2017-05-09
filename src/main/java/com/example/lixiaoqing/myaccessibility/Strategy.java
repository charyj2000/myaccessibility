package com.example.lixiaoqing.myaccessibility;


import android.util.Log;

import com.example.lixiaoqing.myaccessibility.bean.StrategyBean;
import com.example.lixiaoqing.myaccessibility.bean.strategy.StrategyInputBean;

import java.util.List;

public class Strategy {

    public static int STRATEGY_TYPE_RANDOM = 1;
    public static int STRATEGY_TYPE_BREADTH_FIRST = 2;



    public static StrategyBean getTouchID(String frameID, List<String> list, String[] trapNOArray, int type){

        if (STRATEGY_TYPE_RANDOM == type){

            return StrategyRandom.Random(list, trapNOArray);

        } else if(STRATEGY_TYPE_BREADTH_FIRST == type){

            StrategyInputBean strategyInputBean = new StrategyInputBean();
            strategyInputBean.setFrameID(frameID);
            strategyInputBean.setList(list);
            strategyInputBean.setTrapNOArray(trapNOArray);
            StrategyBean strategyBean = StrategyBreadthFirst.breadthFirst(strategyInputBean);

            if (null == strategyBean){
                Log.d("test", "strategyBean is null ");
            } else {
                Log.d("test", "strategyBean is not null ");
            }


            return strategyBean;

        } else {

            return null;
        }
    }

    public static void clean(){
        StrategyBreadthFirst.clean();
    }




}
