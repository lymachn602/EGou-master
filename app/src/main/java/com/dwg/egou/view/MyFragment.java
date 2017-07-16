package com.dwg.egou.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dwg.egou.Acitivty.LoginActivity;
import com.dwg.egou.MyApp;
import com.dwg.egou.Acitivty.OrderList;
import com.dwg.egou.R;
import com.dwg.egou.utils.readUtil;

/**
 * Created by Administrator on 2016/4/4.
 */
public class MyFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private RelativeLayout relativeLayout ;
    private int REQUESTCODE = 1;
    private int LOGOUTCODE = 2;
    private TextView usernickName ;
    private TextView myOrderBt;
    public void setNickname(String nickname){
        usernickName.setText(nickname);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("执行了MyFragment的onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my,container,false);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.login);
        usernickName = (TextView) view.findViewById(R.id.replaceUsername);
        myOrderBt = (TextView) view.findViewById(R.id.my_order);
        myOrderBt.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        //查看用户之前是否登录过，如果登录过，直接进行登录
        if(readUtil.getUsername(getActivity())!=null&& MyApp.user!=null){
            usernickName.setText(MyApp.user.getNickname());
        }
        System.out.println("不知道有没有执行MyFragment的onCreateView函数");
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE&&REQUESTCODE==resultCode){
            String userNickName  = data.getStringExtra("user");
            usernickName.setText(userNickName);
            System.out.println("难道没有执行改昵称后对主界面进行更新？？？");
        }else if(requestCode==REQUESTCODE&&LOGOUTCODE==resultCode){
            usernickName.setText("点击登录");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("更改完进入主界面");
        if(MyApp.user!=null)
        usernickName.setText(MyApp.user.getNickname());
    }

    @Override
    public void onClick(View v) {
        Log.e("点击了登录按钮","点击了登录按钮");
        switch (v.getId()){
            case R.id.login:
                if(MyApp.user==null){
                    startLogin();
                }else{
                    startAccountManager();
                }

                break;
            case R.id.my_order:
                startOrderActivity();
        }
    }

    private void startAccountManager() {
        Intent i = new Intent(getActivity(), com.dwg.egou.Acitivty.AccountManager.class);
        startActivityForResult(i,REQUESTCODE);
    }



    private void startOrderActivity() {
        Intent i = new Intent(getActivity(),OrderList.class);
        startActivity(i);
    }

    private void startLogin() {
        Intent i = new Intent(getActivity(),LoginActivity.class);
        startActivityForResult(i,REQUESTCODE);
    }


}
