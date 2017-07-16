package com.dwg.egou.Acitivty;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dwg.egou.config.CONST;
import com.dwg.egou.view.CartFragment;
import com.dwg.egou.widget.Indicator;
import com.dwg.egou.view.MainFragment;
import com.dwg.egou.MyApp;
import com.dwg.egou.view.MyFragment;
import com.dwg.egou.R;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.entity.User;
import com.dwg.egou.utils.readUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class indexActivity extends FragmentActivity implements View.OnClickListener{
    private ViewPager myPager;
    private List<Fragment> lists;
    private RadioButton bt[] = new RadioButton[3];
    private RadioGroup btGroup;
    private float offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private ImageView cursor;
    private int screenW;
    private Indicator mIndicator;
    private User user;
    private myFragmentAdapter fragmentAdapter;
    private List<String> listTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("难道调用了indexAcitivity的onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mIndicator = (Indicator) findViewById(R.id.indicator);
        //验证用户是否已经登录过
        defaultLogin();
        initLists();
        initPager();
        initButton();
        myPager.setOnPageChangeListener(new MyPagerChange());

    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");


    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }

    private void defaultLogin() {
        if(readUtil.getUsername(this)!=null){//不为空说明登录过
            int  flag = 0;
            user = new User();
            user.setUsername(readUtil.getUsername(this));
            user.setPassword(readUtil.getPassword(this));
            user.setNickname(readUtil.getUsernickname(this));
            System.out.println(user.getNickname()+"哈哈 成功得到了");
            new AsynLogin().execute(user);
        }
        System.out.println("这个方法运行了？");
    }

    private void initButton() {
        bt[0] = (RadioButton) findViewById(R.id.mainBt);
        bt[1] = (RadioButton) findViewById(R.id.cartBt);
        bt[2] = (RadioButton) findViewById(R.id.my);

        Drawable drawableWeiHui = getResources().getDrawable(R.drawable.main_select);
        drawableWeiHui.setBounds(0, 0, 69, 69);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        bt[0].setCompoundDrawables(null, drawableWeiHui, null, null);//只放上面

        Drawable drawableAdd = getResources().getDrawable(R.drawable.cart_press);
        drawableAdd.setBounds(0, 0, 69, 69);
        bt[1].setCompoundDrawables(null, drawableAdd, null, null);

        Drawable drawableRight = getResources().getDrawable(R.drawable.my_press);
        drawableRight.setBounds(0, 0, 69, 69);
        bt[2].setCompoundDrawables(null, drawableRight, null, null);

        btGroup = (RadioGroup) findViewById(R.id.group);
        bt[0].setChecked(true);
        btGroup.setOnCheckedChangeListener(new GroupCheckListener());
    }

    private void initLists() {
        lists = new ArrayList<>();
        MyFragment myFragment = new MyFragment();
        MainFragment mainFragment = new MainFragment();
        CartFragment cartFragment = new CartFragment();
        lists.add(mainFragment);
        lists.add(cartFragment);
        lists.add(myFragment);

    }

    private void initPager() {
        myPager = (ViewPager) findViewById(R.id.mypager);
        myPager.setOffscreenPageLimit(2);//设置当前的myPager可以缓存的页面数量是2
        fragmentAdapter = new myFragmentAdapter(getSupportFragmentManager(),lists);
        myPager.setAdapter(fragmentAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    private class myFragmentAdapter extends FragmentPagerAdapter{
        List<Fragment> lists;
        public myFragmentAdapter(FragmentManager fm,List<Fragment> lists) {
            super(fm);
            this.lists = lists;
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public int getCount() {
            return lists.size();
        }

    }

    private class GroupCheckListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
                case R.id.mainBt:
                    myPager.setCurrentItem(0,true);
                    break;
                case R.id.cartBt:
                    myPager.setCurrentItem(1,true);
                    break;
                case R.id.my:
                    myPager.setCurrentItem(2,true);
                    break;
            }

        }
    }
    private class MyPagerChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mIndicator.scoll(position,positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0;i<3;i++){
                bt[i].setChecked(false);
            }
            bt[position].setChecked(true);
            if(position==1){//中间cart选中
                CartFragment  cartFragment = (CartFragment) lists.get(position);
                System.out.println("中间的被选中");
                cartFragment.upDateSet();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public class AsynLogin extends AsyncTask<User,Void,User> {

        @Override
        protected User doInBackground(User... params) {
            User user  = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.LOGIN_URI+"&username="+user.getUsername()+"&password="+user.getPassword());
            Log.e("user:",user.getUsername()+user.getPassword());
            User getUser = null;
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);

                Header headers = httpResponse.getFirstHeader("Set-Cookie");
                if (headers!=null)
                {
                    Log.e("非空","非空");
                    readUtil.SET_SESSIONID(indexActivity.this,headers.getValue());
                }
                else{
                    Log.e("COOKIE空","COOKIE空");
                }
                HttpEntity entity = httpResponse.getEntity();
                String jsonString  = EntityUtils.toString(entity);
                ResponseObject response = parseUserJson(jsonString);
                if(response.getState()==1){//表示登录成功
                    getUser  = (User) response.getDatas();
                    MyApp.user = getUser;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return getUser;
        }


    }
    private ResponseObject parseUserJson(String jsonString) {
        Gson gson = new Gson();
        ResponseObject responseObject = null;
        try {
            responseObject = gson.fromJson(jsonString, new TypeToken<ResponseObject<User>>() {
            }.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(jsonString, new TypeToken<ResponseObject<String>>() {
            }.getType());
        }

        return responseObject;
    }

}
