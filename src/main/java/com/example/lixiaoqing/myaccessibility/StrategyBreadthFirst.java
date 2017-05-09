package com.example.lixiaoqing.myaccessibility;


import android.util.Log;

import com.example.lixiaoqing.myaccessibility.bean.StrategyBean;
import com.example.lixiaoqing.myaccessibility.bean.strategy.StrategyInputBean;
import com.example.lixiaoqing.myaccessibility.bean.strategy.StrategyTreeNode;

import java.util.ArrayList;
import java.util.List;

public class StrategyBreadthFirst {


    private static StrategyTreeNode root;
    private static List<List<StrategyTreeNode>> hierarchyList = new ArrayList<List<StrategyTreeNode>>() ;
    private static StrategyTreeNode lastNode;
    private static boolean lastReturnRestart = true;

    public static void clean(){

        root = null;
        hierarchyList = new ArrayList<List<StrategyTreeNode>>() ;
        lastNode = null ;
        lastReturnRestart = true;
    }

    public static void setRestart(boolean isRestart){
        lastReturnRestart = isRestart;
    }

    // 广度优先
    public static StrategyBean breadthFirst(StrategyInputBean strategyInputBean){



        // 输入不合法验证
        if (null == strategyInputBean || null == strategyInputBean.getFrameID() || null == strategyInputBean.getList())

        return null;

        // 剔除陷阱
        if (null != strategyInputBean.getTrapNOArray()){

            for (String touchID : strategyInputBean.getTrapNOArray()) {

                int index = strategyInputBean.getList().indexOf(touchID);

                if (-1 != index)
                    strategyInputBean.getList().set(index, null);
            }
        }

        // input 信息放入bean
        StrategyTreeNode inputNode = new StrategyTreeNode();
        inputNode.setFrameID(strategyInputBean.getFrameID());
        inputNode.setTouchID(strategyInputBean.getList());

        // 构建 返回bean
        StrategyBean strategyBean = new StrategyBean();

        // 如果上次没有重启 且 最后操作节点下面没有挂载新的节点. 则把当前节点挂载在上个节点下面.返回重启
        if(!lastReturnRestart && null == lastNode.getTouchID().get(lastNode.getLastOperateTouchID())){

            inputNode.setFloor(lastNode.getFloor()+1);
            inputNode.setFather(lastNode);
            inputNode.setFatherTouchID(lastNode.getLastOperateTouchID());

            addToHierarchyList(inputNode);

            lastNode.getTouchID().put(lastNode.getLastOperateTouchID(), inputNode);

            strategyBean.setNeedRestart(true);
            strategyBean.setTouchID(null);
            lastReturnRestart = strategyBean.isNeedRestart();

            Log.d("test", "------------1");
            return strategyBean;
        }


        // 找到树上需要被操作的目标节点
        StrategyTreeNode targetNode = findTargetNode(); // 排查最后操作节点
        String bottomLeftTouchID = hierarchyList.get(hierarchyList.size()-1).get(0).getFrameID();


        if (null == targetNode){ // 如果没找到目标节点 , 挂载根节点 或者 上次点击节点

            Log.d("test", "------------2");

            if (null == lastNode){ // 如果没有上次点击位置 挂载根节点 重启

                Log.d("test", "------------3");

                inputNode.setFloor(root.getFloor()+1);
                inputNode.setFather(root);
                inputNode.setFatherTouchID("child");
                Log.d("test", "------------3。1");

                root.getTouchID().put("child",inputNode);
                Log.d("test", "------------3。2");
                addToHierarchyList(inputNode);
                Log.d("test", "------------3。3");

                root.setTargetTouchID("child");
                Log.d("test", "------------3。4");
                root.setLastOperateTouchID("child");
                Log.d("test", "------------3。5");
                lastNode = root;
                Log.d("test", "------------3。6");

                strategyBean.setTouchID(null);
                Log.d("test", "------------3。7");
                strategyBean.setNeedRestart(true);
            } else if (lastNode.getTargetTouchID().equals(bottomLeftTouchID)){ // 如果上次点击的位置与最后一层的第一个位置相同,挂载这个节点.


                Log.d("test", "------------4");
                inputNode.setFloor(lastNode.getFloor()+1);
                inputNode.setFather(lastNode);
                inputNode.setFatherTouchID(lastNode.getLastOperateTouchID());

                addToHierarchyList(inputNode);

                lastNode.getTouchID().put(lastNode.getLastOperateTouchID(), inputNode);

                strategyBean.setTouchID(null);
                strategyBean.setNeedRestart(true);

            } else if(lastNode.getTargetTouchID().equals(bottomLeftTouchID)){ // 如果上次点击的位置不是最后一层的第一个位置,则点击第一个位置

                Log.d("test", "------------5");
            }

            lastReturnRestart = strategyBean.isNeedRestart();


            return strategyBean;

        } else { // 如果找到了 目标节点


            Log.d("test", "------------6");

            StrategyTreeNode operateNode = isPathNode(targetNode, inputNode);
            if (null != operateNode){ // 如果新节点在节点链上

                Log.d("test", "------------7");
                // 如果是目标节点,点击,不重启
                if (targetNode.getFrameID().equals(inputNode.getFrameID())){

                    Log.d("test", "------------8");

                    strategyBean.setTouchID(targetNode.getTargetTouchID());
                    strategyBean.setNeedRestart(false);

                    lastNode = targetNode;
                    lastNode.setLastOperateTouchID(strategyBean.getTouchID());
                    lastNode.setTargetTouchID(strategyBean.getTouchID());


                } else { // 如果,不是目标节点,点击到达目标节点的位置,不重启

                    Log.d("test", "------------9");

                    String touchID = getPathTouchID(targetNode, inputNode);
                    strategyBean.setTouchID(touchID);
                    strategyBean.setNeedRestart(false);
                }

            } else { // 如果新节点不在节点链上,说明点击相同的位置产生了不同的结果,挂载上次点击的位置上,重启

                Log.d("test", "------------10");
                StrategyTreeNode oldChild;
                // 如果上次重启了,替换根节点
                if (lastReturnRestart){

                    Log.d("test", "------------11");

                    oldChild = root.getTouchID().get("child");
                    lastNode = root;

                } else { // 上次没重启, 替换上次操作的节点.

                    Log.d("test", "------------12");

                    oldChild = lastNode.getTouchID().get(lastNode.getLastOperateTouchID());

                }

                if (null != oldChild){

                    Log.d("test", "------------13");
                    oldChild.setFather(null);
                    // 清除层状存储结构的无根节点
                    cleanHierarchyList();
                    // 清除上次点击位置下面挂载的节点.
                    lastNode.getTouchID().put(lastNode.getLastOperateTouchID(), null);

                }

                // 设置新节点属性
                inputNode.setFloor(lastNode.getFloor()+1);
                inputNode.setFatherTouchID(lastNode.getLastOperateTouchID());
                inputNode.setFather(lastNode);

                // 将新节点挂载在树上
                addToHierarchyList(inputNode);
                lastNode.getTouchID().put(lastNode.getLastOperateTouchID(), inputNode);

                lastNode.getTouchID().put(lastNode.getLastOperateTouchID(), inputNode);

                strategyBean.setTouchID(null);
                strategyBean.setNeedRestart(true);

                Log.d("test", "------------14");

            }

        }

        lastReturnRestart = strategyBean.isNeedRestart();
        return strategyBean;
    }

    // 把节点添加到层状存储结构上
    private static void addToHierarchyList(StrategyTreeNode inputNode){

        if (inputNode.getFloor() + 1 > hierarchyList.size())

            hierarchyList.add(new ArrayList<StrategyTreeNode>());

        hierarchyList.get(inputNode.getFloor()).add(inputNode);
    }

    // 是否是操作链上的节点
    private static StrategyTreeNode isPathNode(StrategyTreeNode targetNode, StrategyTreeNode inputNode){

        if (null == inputNode || inputNode.getFrameID() == null || inputNode.getFrameID().length() == 0){
            return null;
        }

        while( targetNode != null ){

            if (inputNode.getFrameID() .equals(targetNode.getFrameID()))
                return targetNode;

            else
                targetNode = targetNode.getFather();
        }

        return null;
    }

    // 寻找目标操作位
    private static StrategyTreeNode findTargetNode(){

        if (null == root){
            root = new StrategyTreeNode();
            root.setFrameID("root");
            root.setFather(null);
            root.setFloor(0);

            List<String> touchIDList = new ArrayList<String>();
            touchIDList.add("child");
            root.setTouchID(touchIDList);

            List<StrategyTreeNode> list = new ArrayList<StrategyTreeNode>();
            list.add(root);
            hierarchyList.add(list);

            return null;
        }

        List<StrategyTreeNode> innerList;

        for (int j=0; j< hierarchyList.size(); j++ ) {

            innerList = hierarchyList.get(j);

            StrategyTreeNode child;

            //  循环同级别所有节点
            for (int i=0; i< innerList.size(); i++) {

                child = innerList.get(i);

                String touchID = findLastUsefulTouchID(child);

                if (null != touchID){ // 找到可点击位置节点.返回这个节点,和这个节点应该点击的位置

                    child.setTargetTouchID(touchID);
                    return child;

                }

                // 如果没在这个节点找到可点击位置,继续向后寻找.
            }
        }

        // 如果找到头,则返回最后一行的第一个节点,或者返回null,等待分配节点.挂载.
        return null;
    }


    // 节点链上的节点,需要点击的位置
    private static String getPathTouchID(StrategyTreeNode targetNode, StrategyTreeNode inputNode){

        while (null != targetNode.getFather()){
            if (targetNode.getFather().getFrameID().equals(inputNode.getFrameID())){
                return targetNode.getFatherTouchID();
            }

            targetNode = targetNode.getFather();
        }
        return null ;
    }

    // 清除层状列表的无根节点
    private static void cleanHierarchyList(){

        List<StrategyTreeNode> list ;

        for (int i = hierarchyList.size()-1; i>=0; i--) {

            list = hierarchyList.get(i);

            if (null != list)

                for (int j = list.size()-1; j >= 0; j--) {
                    if (!isOnTheTree(list.get(j))){

                        list.get(j).setFather(null);
                        list.remove(j);
                    }
                }
        }

        for (int i = hierarchyList.size()-1; i>=0; i--) {
            if(hierarchyList.get(i).isEmpty()){
                hierarchyList.remove(i);
            }
        }
    }

    // 这个节点是否还在树上
    private static boolean isOnTheTree(StrategyTreeNode node){

        while (null != node){

            if (node.getFrameID().equals("root"))

                return true;
            else

                node = node.getFather();
        }

        return false;
    }

    // 找到最后可用的点击位置
    private static String findLastUsefulTouchID(StrategyTreeNode strategyTreeNode){

        if (null == strategyTreeNode.getTouchID() || strategyTreeNode.getTouchID().isEmpty())

            return null;

        if (null == strategyTreeNode.getLastOperateTouchID())

            return strategyTreeNode.getTouchID().keySet().iterator().next();
//            for (String key : strategyTreeNode.getTouchID().keySet()) {
//                return key;
//            }

        boolean isStart = false;

        for (String key : strategyTreeNode.getTouchID().keySet()) {

            if (!isStart ){

                if (key.equals(strategyTreeNode.getLastOperateTouchID()))

                    isStart = true;

                continue;
            }

            return key;
        }

        return null;
    }







    private static StrategyInputBean createNode(String name, int childrenNumber){

        List firstList = new ArrayList<String>(childrenNumber);
        for (int i = 0; i < childrenNumber; i++) {

            firstList.add(i + "");
        }

        StrategyInputBean strategyInputBean = new StrategyInputBean();
        strategyInputBean.setFrameID(name);
        strategyInputBean.setList(firstList);

        return strategyInputBean;

    }

    private static void test(StrategyInputBean strategyInputBean){

        StrategyBean strategyBean = breadthFirst(strategyInputBean);
        printStrategyBean(strategyInputBean, strategyBean);
    }

    public static void main(String[] a){

        System.out.println("start");


        StrategyInputBean nodeA = createNode("A", 2);
        StrategyInputBean nodeB = createNode("B", 2);
        StrategyInputBean nodeC = createNode("C", 2);
        StrategyInputBean nodeD = createNode("D", 2);

        test(nodeA);

        test(nodeA);
        test(nodeB);
        test(nodeA);
        test(nodeB);

        test(nodeA);
        test(nodeB);
        test(nodeC);
        test(nodeD);
        test(nodeD);
        test(nodeA);




    }

    private static void printStrategyBean(StrategyInputBean strategyInputBean, StrategyBean strategyBean){


        System.out.print("input  ");

        if (null == strategyInputBean)
            System.out.println("null");
        else
            System.out.println(strategyInputBean.getFrameID() + " : " + strategyInputBean.getList().toString());



        System.out.print("return  ");

        if (null == strategyBean)
            System.out.println("null");
        else
            System.out.println(strategyBean.getTouchID() + " : " + strategyBean.isNeedRestart());


    }

}
