package com.example.lixiaoqing.myaccessibility.tools;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.lixiaoqing.myaccessibility.ControllerService;
import com.example.lixiaoqing.myaccessibility.MyService;
import com.example.lixiaoqing.myaccessibility.Properties;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class OpenCloseAPP {

	private static Process process;

	/**
	 * 结束进程,执行操作调用即可
	 */
	public static void close(String packageName) {
		initProcess();
		killProcess(packageName);
		close();
	}

	/**
	 * 初始化进程
	 */
	private static void initProcess() {
		if (process == null)
			try {
				process = Runtime.getRuntime().exec("su");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 结束进程
	 */
	private static void killProcess(String packageName) {
		OutputStream out = process.getOutputStream();
		String cmd = "am force-stop " + packageName + " \n";
		try {
			out.write(cmd.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭输出流
	 */
	private static void close() {
		if (process != null)
			try {
				process.getOutputStream().close();
				process = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void startAPP(Context context, String appPackageName){

		try{
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(intent);
		}catch(Exception e){
//            Toast.makeText(this, "没有安装", Toast.LENGTH_LONG).show();
		}
	}

	// 卸载app
	public static void uninstallApp(Context context, String packageName){
		MyService.INVOKE_TYPE = MyService.TYPE_UNINSTALL_APP;
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);
	}

	// 安装app
	public static boolean installApp(Context context, String packageName){

		try{

			String fileName = SDCard.getDownLoadPath() + File.separator + packageName +".apk";
			Log.d("test", fileName);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
			context.startActivity(intent);

			return true;

		} catch (Exception ignored){
			return false;
		}

	}

	public static void downloadApp(Context context, String packageName){

		String path = SDCard.getDownLoadPath() + File.separator + packageName +".apk";

		File file = new File(path);

		if (file.exists())

			file.delete();


		ControllerService.setSTATES(ControllerService.STATES_DOWNLOADING);

		DownloadManager downloadManager = (DownloadManager) (context.getSystemService(Context.DOWNLOAD_SERVICE));

		Uri uri = Uri.parse(Properties.APP_DOWNLOAD_URL+ packageName);

		DownloadManager.Request request = new DownloadManager.Request(uri);

		// 设置下载路径和文件名

		request.setDestinationInExternalPublicDir("download", packageName +".apk");

		request.setDescription("正在下载apk");

		request.setMimeType("application/vnd.android.package-archive");

		long downloadID = downloadManager.enqueue(request);

		// 把当前下载的ID保存起来

		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);

		sharedPreferences.edit().putLong("downloadID", downloadID).apply();

		sharedPreferences.edit().putString("needInstallPackageName", packageName).commit();

	}

	public static void deleteApp(String packageName){

		Log.d("test", "删除安装包 ： " + packageName);

		String path = SDCard.getDownLoadPath() + File.separator + packageName +".apk";

		path = SDCard.checkAndReplaceEmulatedPath(path);

		File file = new File(path);

		if (file.exists())

			file.delete();
	}
}
