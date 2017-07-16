package com.dwg.egou.Acitivty;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.dwg.egou.R;
import com.dwg.egou.utils.readUtil;

public class MainActivity extends FragmentActivity{
    private Handler mHander;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //实现三秒跳转
        mHander = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (readUtil.getWelcomeBoolean(getApplicationContext())) {
                    Log.e("isfirst",""+readUtil.getWelcomeBoolean(getApplicationContext()));
                    Intent i = new Intent(MainActivity.this, GuideActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(MainActivity.this, indexActivity.class);
                    startActivity(i);
                }
                finish();
            }
        };
        mHander.sendEmptyMessageDelayed(1,3000);

        //



    }
}
