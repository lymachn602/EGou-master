package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.dwg.egou.R;
import com.dwg.egou.utils.readUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/3.
 */
public class GuideActivity extends Activity implements View.OnClickListener {
    private ViewPager viewPager;
    private List<View> list;
    private Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        //初始化pager
        initPager();
        //按钮
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);

    }

    private void initPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        list = new ArrayList<>();
        ImageView img1 = new ImageView(this);
        img1.setImageResource(R.drawable.guide1);
        list.add(img1);


        ImageView img2 = new ImageView(this);
        img2.setImageResource(R.drawable.guide2);
        list.add(img2);


        ImageView img3 = new ImageView(this);
        img3.setImageResource(R.drawable.guide3);
        list.add(img3);

        viewPager.setAdapter(new MyAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2){
                    start.setVisibility(View.VISIBLE);
                }else{
                    start.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                readUtil.WriteWelcome(this);
                Intent i = new Intent(this,indexActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    public class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           // super.destroyItem(container, position, object);
            container.removeView(list.get(position));
        }
    }

}
