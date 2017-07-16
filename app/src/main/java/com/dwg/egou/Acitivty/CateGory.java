package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dwg.egou.Acitivty.CartGoryList;
import com.dwg.egou.MyApp;
import com.dwg.egou.R;

/**
 * Created by Administrator on 2016/5/1.
 */
public class CateGory extends Activity implements View.OnClickListener{
    private LinearLayout[] linearLayout = new LinearLayout[9];
    private ImageView back;
    private String[] cateGoryList=
            {"家用电器","手机、数码","电脑、办公",
                    "家具、家居、家装、厨具",
                    "个护化妆、清洁用品",
                    "食品、酒类、特产",
                    "图书、音像、电子书",
                    "鞋靴、箱包、钟表、奢侈品",
                    "鞋靴、箱包、钟表、奢侈品"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        back = (ImageView) findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearLayout[0] = (LinearLayout) findViewById(R.id.jiayong);
        linearLayout[1] = (LinearLayout) findViewById(R.id.shouji);
        linearLayout[2] = (LinearLayout) findViewById(R.id.diannao);
        linearLayout[3] = (LinearLayout) findViewById(R.id.jiaju);
        linearLayout[4] = (LinearLayout) findViewById(R.id.gehu);
        linearLayout[5] = (LinearLayout) findViewById(R.id.shipin);
        linearLayout[6] = (LinearLayout) findViewById(R.id.tushu);
        linearLayout[7] = (LinearLayout) findViewById(R.id.xiexue);
        linearLayout[8] = (LinearLayout) findViewById(R.id.qiche);

        for (int i = 0;i<9;i++){
            linearLayout[i].setOnClickListener(this);
        }

        MyApp.FinishListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println("分类中的一个点击了");
        switch (v.getId()){

            case  R.id.jiayong :
                startCartGoryList(cateGoryList[0]);
                break;
            case  R.id.shouji :
                startCartGoryList(cateGoryList[1]);
                break;
            case  R.id.diannao :
                startCartGoryList(cateGoryList[2]);
                break;
            case  R.id.jiaju :
                startCartGoryList(cateGoryList[3]);
                break;
            case  R.id.gehu :
                startCartGoryList(cateGoryList[4]);
                break;
            case  R.id.shipin :
                startCartGoryList(cateGoryList[5]);
                break;
            case  R.id.tushu :
                startCartGoryList(cateGoryList[6]);
                break;
            case  R.id.xiexue :
                startCartGoryList(cateGoryList[7]);
                break;
            case  R.id.qiche :
                startCartGoryList(cateGoryList[8]);
                break;
        }
    }

    private void startCartGoryList(String s) {
        Intent  i = new Intent(this,CartGoryList.class);
        System.out.println(s);
        i.putExtra("category",s);
        startActivity(i);
    }
}
