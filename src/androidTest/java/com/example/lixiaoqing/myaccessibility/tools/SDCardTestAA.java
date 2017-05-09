package com.example.lixiaoqing.myaccessibility.tools;

import junit.framework.TestCase;

import java.io.File;

import static com.example.lixiaoqing.myaccessibility.tools.SDCard.getDownLoadPath;


public class SDCardTestAA extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testGetDownLoadPath() throws Exception {

        File file = new File(getDownLoadPath());

        assert(file.canRead());
//        assertEquals("111", getDownLoadPath());
    }

}