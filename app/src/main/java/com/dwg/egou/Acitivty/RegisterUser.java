package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dwg.egou.MyApp;
import com.dwg.egou.R;
import com.dwg.egou.config.Validator;
import com.dwg.egou.config.CONST;
import com.dwg.egou.entity.ResponseObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/3.
 */
public class RegisterUser extends Activity implements View.OnClickListener{
    private EditText username ;
    private EditText emial;
    private EditText password;
    private EditText passwordConfirm;
    private Button signUpBt;
    private ImageView back;
    private EditText nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
        username = (EditText) findViewById(R.id.registerName);
        emial = (EditText) findViewById(R.id.registerEmail);
        password = (EditText) findViewById(R.id.registerPassword);
        passwordConfirm = (EditText) findViewById(R.id.registerPasswordConfirm);
        signUpBt = (Button) findViewById(R.id.registerBt);
        back = (ImageView) findViewById(R.id.backToLogin);
        nickName = (EditText) findViewById(R.id.registerNickname);
        signUpBt.setOnClickListener(this);
        back.setOnClickListener(this);

        MyApp.FinishListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backToLogin:{
                finish();
            }break;
            case R.id.registerBt:{
                register();
            }break;
        }
    }

    private void register() {
        if(!Validator.isUsername(username.getText().toString())){
            Toast.makeText(RegisterUser.this, "用户名格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Validator.isEmail(emial.getText().toString())){
            Toast.makeText(RegisterUser.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.getText().toString().equals(passwordConfirm.getText().toString())){
            Toast.makeText(RegisterUser.this, "2次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Validator.isPassword(passwordConfirm.getText().toString())){
            Toast.makeText(RegisterUser.this, "密码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }


        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username",username.getText().toString()));
        params.add(new BasicNameValuePair("email",emial.getText().toString()));
        params.add(new BasicNameValuePair("nickname",nickName.getText().toString()));
        params.add(new BasicNameValuePair("password",password.getText().toString()));
        new registerAnsy().execute(params);
    }
    private class registerAnsy extends AsyncTask<List<NameValuePair>, Void, ResponseObject> {
        @Override
        protected ResponseObject doInBackground(List<NameValuePair>... params) {
            List<NameValuePair> param = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.REGISTER);
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response =  client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                return parseCartJson(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseObject responseObject) {
            super.onPostExecute(responseObject);
            if(responseObject.getState()==1){
                Toast.makeText(RegisterUser.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();
            }else if(responseObject.getState() == 0){
                Toast.makeText(RegisterUser.this, "注册失败！用户名已经存在", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject<String>  responseObject;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }

        return  responseObject;
    }
}
