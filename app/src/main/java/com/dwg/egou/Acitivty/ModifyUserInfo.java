package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwg.egou.config.CONST;
import com.dwg.egou.MyApp;
import com.dwg.egou.utils.NetworkDetector;
import com.dwg.egou.R;
import com.dwg.egou.entity.User;
import com.dwg.egou.utils.readUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class ModifyUserInfo extends Activity implements OnClickListener{
    private TextView title;
    private EditText content;
    private ImageView config;
    private String info;
    private ImageView modifyBackToMyBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_user_info);
        title = (TextView) findViewById(R.id.modify_user_info_title);
        content = (EditText) findViewById(R.id.modify_user_info_content);
        config = (ImageView) findViewById(R.id.modify_user_info_config);
        modifyBackToMyBt = (ImageView) findViewById(R.id.modifyBackToMyBt);
        modifyBackToMyBt.setOnClickListener(this);
        config.setOnClickListener(this);
        setModifyUserTitle();

        MyApp.FinishListener(this);
    }

    private void setModifyUserTitle() {
        Intent i = getIntent();
        info = i.getStringExtra("info");
        if(info.equals("nickname")){
            title.setText("修改昵称");
            if(MyApp.user!=null){
                content.setText(MyApp.user.getNickname());
            }
        }else if(info.equals("email")){
            title.setText("修改邮箱");
            if(MyApp.user!=null){
                content.setText(MyApp.user.getEmail());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.modifyBackToMyBt:
                finish();
                break;
            case R.id.modify_user_info_config:{
                if(NetworkDetector.netWorkIsValid(this)){
                    User user = new User();

                    if(MyApp.user!=null){
                        if(info.equals("nickname")){
                            user.setNickname(content.getText().toString());
                            user.setEmail(MyApp.user.getEmail());
                            MyApp.user.setNickname(content.getText().toString());
                        }else if(info.equals("email")){
                            user.setEmail(content.getText().toString());
                            user.setNickname(MyApp.user.getNickname());
                        }
                        new ModifyUserAsyn().execute(user);//提交数据

                    }
                }else{
                    Toast.makeText(ModifyUserInfo.this, "网络有问题", Toast.LENGTH_SHORT).show();
                }

            }break;
        }

    }
    public  class ModifyUserAsyn extends AsyncTask<User,Void,User>{

        @Override
        protected User doInBackground(User... params) {
            System.out.println("修改user信息");
            User user = params[0];
            List<NameValuePair> postParams = new ArrayList<>();
            postParams.add(new BasicNameValuePair("nickname",user.getNickname()));
            postParams.add(new BasicNameValuePair("email",user.getEmail()));
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.MODIFYUSER);
            httpPost.setHeader("Cookie", readUtil.GET_SESSIONID(ModifyUserInfo.this));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
                httpClient.execute(httpPost);
                return  user;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user!=null)
            {
                Toast.makeText(ModifyUserInfo.this, "修改成功", Toast.LENGTH_SHORT).show();
                MyApp.user.setNickname(user.getNickname());
                MyApp.user.setEmail(user.getEmail());
                Intent i = new Intent(ModifyUserInfo.this,indexActivity.class);
                startActivity(i);
            }
            else{
                Toast.makeText(ModifyUserInfo.this, "网络有问题", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
