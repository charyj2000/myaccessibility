package com.example.lixiaoqing.myaccessibility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    protected TextView serverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverIP = (TextView)findViewById(R.id.server_ip);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_APPEND);
        serverIP.setText(sharedPreferences.getString(Properties.SHAREDPREFERENCES_NAME_SERVER_IP,""));
    }

    public void openSet(View view){

        Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(killIntent);
    }

    public void exit(View view){
        this.finish();
        System.exit(0);

    }

    public void save(View view){
        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getPackageName(), Context.MODE_APPEND);
        sharedPreferences.edit().putString(Properties.SHAREDPREFERENCES_NAME_SERVER_IP, serverIP.getText().toString()).apply();
        MyService.setServerIP(serverIP.getText().toString());
    }

    public void test(View view){

    }



}
