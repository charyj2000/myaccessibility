package com.example.lixiaoqing.myaccessibility;

import com.example.lixiaoqing.myaccessibility.bean.StrategyBean;

import java.util.List;
import java.util.Random;


public class StrategyRandom {

    private static Random random = new Random();

    public static StrategyBean Random(List<String> list, String[] trapNOArray){


        StrategyBean strategyBean = new StrategyBean();

        if (null == list || 0 == list.size()){
            strategyBean.setNeedRestart(true);
            strategyBean.setTouchID(null);
            return strategyBean;
        }

        // 剔除陷阱位置
        for (String trapNO_id: trapNOArray) {
            list.remove(trapNO_id);
        }

        if (null == list || 0 == list.size()){
            strategyBean.setNeedRestart(true);
            strategyBean.setTouchID(null);
            return strategyBean;
        }

        int randomNO = random.nextInt(list.size());

        String touchID = list.get(randomNO);

        strategyBean.setNeedRestart(false);
        strategyBean.setTouchID(touchID);
        return strategyBean;
    }


}
