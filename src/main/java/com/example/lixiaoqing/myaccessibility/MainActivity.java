package com.example.lixiaoqing.myaccessibility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSet(View view){

        Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(killIntent);
    }

    public void exit(View view){
        this.finish();
        System.exit(0);

    }

    public void test(View view){

    }



}
