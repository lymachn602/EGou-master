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
import com.dwg.egou.R;
import com.dwg.egou.entity.ResponseObject;
import com.dwg.egou.utils.readUtil;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateOrder extends Activity implements View.OnClickListener{
    private EditText name;
    private EditText telphone;
    private EditText address;
    private Button back;
    private Button confirmation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address);
        name = (EditText) findViewById(R.id.name);
        telphone = (EditText) findViewById(R.id.telphone);
        address = (EditText) findViewById(R.id.address);
        back = (Button) findViewById(R.id.backToCartBt);
        confirmation = (Button) findViewById(R.id.confirmation);
        back.setOnClickListener(this);
        confirmation.setOnClickListener(this);
        MyApp.FinishListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backToCartBt:{
                finish();
            }break;
            case R.id.confirmation:{
                List<NameValuePair> params= new ArrayList<>();
                params.add(new BasicNameValuePair("username",name.getText().toString()));
                params.add(new BasicNameValuePair("telephone",telphone.getText().toString()));
                params.add(new BasicNameValuePair("address",address.getText().toString()));
                new generateOrderAnsy().execute(params);

            }break;
        }
    }

    private class generateOrderAnsy extends AsyncTask<List<NameValuePair>, Void, ResponseObject> {
        @Override
        protected ResponseObject<String> doInBackground(List<NameValuePair>... params) {
            List<NameValuePair> param = params[0];
            ResponseObject<String> responseObject=null;
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.GENERATEORDER);

            httpPost.setHeader("Cookie", readUtil.GET_SESSIONID(GenerateOrder.this));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(param,HTTP.UTF_8));
                HttpResponse response = client.execute(httpPost);
                responseObject = parseCartJson(EntityUtils.toString(response.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseObject;
        }

        @Override
        protected void onPostExecute(ResponseObject responseObject) {
            super.onPostExecute(responseObject);
            Intent i = new Intent();


            if(responseObject.getState()==1){
                Toast.makeText(GenerateOrder.this, "生成订单成功", Toast.LENGTH_SHORT).show();
                i.putExtra("state",1);
            }else{
                Toast.makeText(GenerateOrder.this,(String)responseObject.getDatas(), Toast.LENGTH_SHORT).show();
                i.putExtra("state",0);
            }
            setResult(1,i);
            finish();
        }
    }
    private ResponseObject parseCartJson(String json) {
        Gson gson = new Gson();
        System.out.println("json>>>>.."+json);
        ResponseObject<String>  responseObject=null;
        try{
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }catch (RuntimeException e){
            responseObject = gson.fromJson(json,new TypeToken<ResponseObject<String>>(){}.getType());
        }

        return  responseObject;

    }
}
