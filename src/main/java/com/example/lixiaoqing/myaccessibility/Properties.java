package com.example.lixiaoqing.myaccessibility;

public class Properties {

    public static final String NET_ADDRESS_HEAD = "http://";
    public static final String NET_ADDRESS_END = "/PYDataServer/up";

    public static final String SHAREDPREFERENCES_NAME_SERVER_IP = "SERVER_IP";
    public static final String START_TIME = "START_TIME";

    public static final String NOTIFICATION_URL = "http://ap.mixdata.com.cn/notify/action?";
    public static final String NOTIFICATION_URL_START = NOTIFICATION_URL + "flag=begin";
    public static final String NOTIFICATION_URL_END = NOTIFICATION_URL + "flag=end";


    public static final String SERVER_ADDRESS = "http://ap.mixdata.com.cn";

    public static final String GET_PACKAGE_NAME_URL = SERVER_ADDRESS + "/notify/getTestPackageName";
    public static final String GET_TRAP_INFO_URL = SERVER_ADDRESS + "/notify/getTrapInfo";
    public static final String UP_OPERATE_INFO_URL = SERVER_ADDRESS + "/notify/upOpreateInfo";
    public static final String APP_EXITED_URL = SERVER_ADDRESS + "/notify/upTestAPPExited";
    public static final String APP_DOWNLOAD_URL = SERVER_ADDRESS + "/notify/apkDownload?packageName=";
}
