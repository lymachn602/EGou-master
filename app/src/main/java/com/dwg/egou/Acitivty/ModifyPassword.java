package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dwg.egou.config.CONST;
import com.dwg.egou.MyApp;
import com.dwg.egou.utils.NetworkDetector;
import com.dwg.egou.R;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.config.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 */
public class ModifyPassword extends Activity implements View.OnClickListener{
    private Button confirm;
    private Button back;
    private EditText username;
    private EditText email;
    private EditText newPwd;
    private EditText newPwdConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);
        confirm = (Button) findViewById(R.id.confirmModifyPwdBt);
        back = (Button) findViewById(R.id.back);
        username = (EditText) findViewById(R.id.modifyUserNameEdit);
        email = (EditText) findViewById(R.id.modifyEmail);
        newPwd = (EditText) findViewById(R.id.newPassword);
        newPwdConfig = (EditText) findViewById(R.id.newPasswordConfig);

        confirm.setOnClickListener(this);
        back.setOnClickListener(this);

        MyApp.FinishListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.confirmModifyPwdBt:
                if(NetworkDetector.netWorkIsValid(this)){
                    mofidyPwd();
                }else{
                    Toast.makeText(ModifyPassword.this, "网络有问题", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void mofidyPwd() {
        String usernameS = username.getText().toString();
        String emailS = email.getText().toString();
        String newPwdS =newPwd.getText().toString();
        String newPwdConfirmS = newPwdConfig.getText().toString();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username",usernameS));
        params.add(new BasicNameValuePair("email",emailS));
        params.add(new BasicNameValuePair("password",newPwdS));
        if(newPwdS.equals(newPwdConfirmS)){
            if(Validator.isUsername(usernameS)){
                if(Validator.isEmail(emailS)){
                    if(newPwdS.equals(newPwdConfirmS)){
                        if(Validator.isPassword(newPwdS)){
                            new modifyPwdAsyn().execute(params);
                        }else{
                            Toast.makeText(ModifyPassword.this, "密码格式不正确", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ModifyPassword.this, "两次输入的密码不符", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ModifyPassword.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(ModifyPassword.this, "用户名格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(ModifyPassword.this, "两次输入的密码不符", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    public class modifyPwdAsyn extends AsyncTask<List<NameValuePair>,Void,ResponseObject> {
        @Override
        protected ResponseObject doInBackground(List<NameValuePair>... params) {
            List<NameValuePair> param = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(CONST.MODIFYPASSWORD);
            try {
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response =  client.execute(post);
                String json = EntityUtils.toString(response.getEntity());
                return parseCartJson(json);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ResponseObject responseObject) {
            super.onPostExecute(responseObject);
            if(responseObject.getState()==1){
                Toast.makeText(ModifyPassword.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ModifyPassword.this,indexActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }else if(responseObject.getState()==2){
                Toast.makeText(ModifyPassword.this, "用户不存在", Toast.LENGTH_SHORT).show();
            }else if(responseObject.getState()==3){
                Toast.makeText(ModifyPassword.this, "邮箱不正确", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject responseObject;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }
        return  responseObject;
    }
}
