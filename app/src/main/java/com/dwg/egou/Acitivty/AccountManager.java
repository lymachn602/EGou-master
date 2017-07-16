package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dwg.egou.MyApp;
import com.dwg.egou.R;
import com.dwg.egou.entity.User;
import com.dwg.egou.utils.readUtil;

/**
 * Created by Administrator on 2016/5/1.
 */
public class AccountManager extends Activity implements View.OnClickListener {
  private RelativeLayout modifyNicknameBt;
  private RelativeLayout modifyEmailBt;
  private RelativeLayout modifyPasswordBt;
  private TextView usernameText;
  private TextView emailText;
  private TextView registerTime;
  private TextView nicknameText;
  private Button LogOutBt;
  private ImageView managerBackToMyBt;
  private String[] strArr = {"nickname", "email"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.account_manager);
    modifyNicknameBt = (RelativeLayout) findViewById(R.id.modifyNickNameBt);
    modifyEmailBt = (RelativeLayout) findViewById(R.id.modifyEmailBt);
    modifyPasswordBt = (RelativeLayout) findViewById(R.id.modifyPassWordBt);
    LogOutBt = (Button) findViewById(R.id.LogOut);
    usernameText = (TextView) findViewById(R.id.modify_user_info_username);
    nicknameText = (TextView) findViewById(R.id.modify_user_info_nickname);
    emailText = (TextView) findViewById(R.id.modify_user_info_email);
    registerTime = (TextView) findViewById(R.id.modify_user_info_register_time);
    managerBackToMyBt = (ImageView) findViewById(R.id.managerBackToMyBt);
    modifyNicknameBt.setOnClickListener(this);
    modifyEmailBt.setOnClickListener(this);
    modifyPasswordBt.setOnClickListener(this);
    LogOutBt.setOnClickListener(this);
    managerBackToMyBt.setOnClickListener(this);
    User user = MyApp.user;
    usernameText.setText(user.getUsername());
    nicknameText.setText(user.getNickname());
    emailText.setText(user.getEmail());
    registerTime.setText(user.getRegistertime().toString());

    MyApp.FinishListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.modifyNickNameBt: {
        startModifyActivity(strArr[0]);
      }
      break;
      case R.id.modifyEmailBt: {
        startModifyActivity(strArr[1]);
      }
      break;
      case R.id.modifyPassWordBt: {
        Intent i = new Intent(this, ModifyPassword.class);
        startActivity(i);
      }
      break;
      case R.id.LogOut: {//注销登录
        destroyAllInfo();
      }
      break;
      case R.id.managerBackToMyBt:
        finish();
        break;
    }
  }

  private void destroyAllInfo() {
    System.out.println("注销登录");
    //删除所有在sharedPreference中的东西
    readUtil.SET_SESSIONID(AccountManager.this, null);
    MyApp.user = null;
    readUtil.setUsername(AccountManager.this, null);
    readUtil.setPassword(AccountManager.this, null);
    readUtil.setUsernickName(AccountManager.this, null);

    //调回到登录界面
    Toast.makeText(AccountManager.this, "退出登录成功", Toast.LENGTH_SHORT).show();
    Intent i = new Intent();
    setResult(2, i);
    finish();
  }

  private void startModifyActivity(String s) {
    Intent i = new Intent(this, ModifyUserInfo.class);
    i.putExtra("info", s);
    startActivity(i);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    System.out.println("执行了onDestroy函数");
  }
}
