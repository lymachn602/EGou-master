package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwg.egou.config.CONST;
import com.dwg.egou.MyApp;
import com.dwg.egou.utils.MyUtils;
import com.dwg.egou.utils.NetworkDetector;
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
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;


public class LoginActivity extends Activity implements View.OnClickListener,PlatformActionListener {
//    private SwipeBackLayout mSwipeBackLayout;

    private TextView qqLogin;
    private Button loginButton;
    private EditText userName;
    private EditText userPass;
    private EditText checkCodeText;
    private Button checkCodeButton;
    private ImageView back;
    private User user;
    private String code;
    private TextView register;
    private Button modifyPwdBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);

        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        userName = (EditText) findViewById(R.id.userName);
        userPass = (EditText) findViewById(R.id.userPass);
        checkCodeText = (EditText) findViewById(R.id.check_code_text);
        checkCodeButton = (Button) findViewById(R.id.check_code_button);
        back = (ImageView) findViewById(R.id.back);
        register = (TextView) findViewById(R.id.registerUser);
        modifyPwdBt = (Button) findViewById(R.id.modifyPwdBt);
        register.setOnClickListener(this);
        back.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        checkCodeButton.setOnClickListener(this);
        modifyPwdBt.setOnClickListener(this);
        setCheckCode();
        qqLogin = (TextView) findViewById(R.id.qqLogin);
        ShareSDK.initSDK(this);
        qqLogin.setOnClickListener(this);

        MyApp.FinishListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.qqLogin:
                loginByQQ();
                break;
            case R.id.loginButton:
                if(NetworkDetector.netWorkIsValid(this)){
                    login();
                }else{
                    Toast.makeText(LoginActivity.this, "网络出了问题", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.check_code_button:
                setCheckCode();
                break;
            case R.id.back:
            {
                finish();
            }
            break;
            case R.id.registerUser:{
                Intent i = new Intent(this,RegisterUser.class);
                startActivity(i);
            }
            break;
            case R.id.modifyPwdBt:{
                Intent i =  new Intent(this,ModifyPassword.class);
                startActivity(i);
            }
            break;
        }
    }

    private void login() {

        if(checkCommitInfo()){
            User user = new User();
            user.setUsername(userName.getText().toString());
            user.setPassword(userPass.getText().toString());
            new AsynLogin().execute(user);

        }

    }
    public void defaultLogin(User user){
        new AsynLogin().execute(user);

    }

    private boolean checkCommitInfo() {
        if(userName.getText().toString()==null||userPass.getText().toString()==null){
            Toast.makeText(LoginActivity.this, "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(checkCodeText.getText().toString()==null){
            Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!checkCodeInfo()){
            Toast.makeText(LoginActivity.this, "验证码不正确，重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkCodeInfo() {
        if(code.equals(checkCodeText.getText().toString())){
            return true;
        }
        return false;
    }

    private void setCheckCode() {
        code = MyUtils.getCheckCode(4);
        checkCodeButton.setText(code);
        Log.e("CODE:",code);
    }

    private void loginByQQ() {
        Platform platform = ShareSDK.getPlatform(this, QQ.NAME);
        platform.setPlatformActionListener(this);
        if (platform.isAuthValid()){
            String uname = platform.getDb().getUserName();
            String userIcon = platform.getDb().getUserIcon();
            Log.e("验证通过","yanzhengtongguo");
            Log.e("userName:",uname);
            Log.e("userIcan",userIcon);
            platform.removeAccount();
        }
        else{
            platform.showUser(null);
            Log.e("还没有进行应用授权，接下来会进入授权界面","yanzhengtongguo");
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String uname = platform.getDb().getUserName();
        Toast.makeText(this,"授权成功",Toast.LENGTH_SHORT).show();
        platform.getDb().getUserId();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(this,platform.getName()+"授权失败 请重试",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(this,platform.getName()+"授权取消",Toast.LENGTH_SHORT).show();
    }
    public class AsynLogin extends AsyncTask<User,Void,User>{

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
                    readUtil.SET_SESSIONID(LoginActivity.this,headers.getValue());
                }
                else{
                    Log.e("COOKIE空","COOKIE空");

                }
                HttpEntity entity = httpResponse.getEntity();
                String jsonString  = EntityUtils.toString(entity);
                ResponseObject response = parseUserJson(jsonString);
                if(response.getState()==1){//表示登录成功
                    getUser  = (User) response.getDatas();

                }


            } catch (IOException e) {
                e.printStackTrace();
            }



            return getUser;
        }



        @Override
        protected void onPostExecute(User user) {

            super.onPostExecute(user);
            LoginActivity.this.user = user;

            MyApp.user = user;

            if(user==null){//用户登录失败 可能密码或者是账号错误
                Toast.makeText(LoginActivity.this,"账号或者密码错误",Toast.LENGTH_SHORT).show();
                setCheckCode();
            }
            else{//登录成功进行页面跳转
                loginSuccess();
            }
        }
    }

    private ResponseObject parseUserJson(String jsonString) {
        Gson gson = new Gson();
        ResponseObject responseObject ;
            try {
                responseObject = gson.fromJson(jsonString, new TypeToken<ResponseObject<User>>() {
                }.getType());
            }catch (RuntimeException e){
                responseObject = gson.fromJson(jsonString, new TypeToken<ResponseObject<String>>() {
                }.getType());
            }

        return responseObject;
    }
    private void loginSuccess(){
        //保存当前用户的信息到SharedPreference中
        readUtil.setUsername(this,user.getUsername());
        readUtil.setPassword(this,user.getPassword());
        readUtil.setUsernickName(this,user.getNickname());
        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this,indexActivity.class) ;
        i.putExtra("user",user.getNickname());
        setResult(1,i);
        finish();
    }
}
