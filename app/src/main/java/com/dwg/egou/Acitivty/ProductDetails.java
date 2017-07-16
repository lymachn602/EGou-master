package com.dwg.egou.Acitivty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwg.egou.MyApp;
import com.dwg.egou.R;
import com.dwg.egou.config.CONST;
import com.dwg.egou.entity.Product;
import com.dwg.egou.utils.readUtil;
import com.squareup.picasso.Picasso;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Administrator on 2016/4/28.
 */
public class ProductDetails extends Activity implements View.OnClickListener{
    private ImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView productDescription;
    private Button addProductCart;
    private Product product;
    private TextView productNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        productImage = (ImageView) findViewById(R.id.productDetailsImage);
        productName = (TextView) findViewById(R.id.productDetailsName);
        productPrice = (TextView) findViewById(R.id.productDetailsPrice);
        productDescription = (TextView) findViewById(R.id.product_description);
        addProductCart = (Button) findViewById(R.id.addProductForCart);
        productNum = (TextView) findViewById(R.id.productDetailsNum);

        addProductCart.setOnClickListener(this);

        Intent i = getIntent();
        product = i.getParcelableExtra("product");
        Picasso.with(this).load(CONST.HOST + product
                .getImgsrc()).placeholder(R.drawable.dot_current).into(productImage);
        productName.setText(product.getName());
        productPrice.setText("￥" + product.getPrice());
        productDescription.setText(product.getDescription());
        productNum.setText("(库存量:" + product.getNumber() + ")");
        MyApp.FinishListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (MyApp.user != null) {
            switch (v.getId()) {
                case R.id.addProductForCart: {
                    new AddProductForCart().execute(product.getId());
                }
                break;
            }
        } else {
            Toast.makeText(ProductDetails.this, "还没有登录", Toast.LENGTH_SHORT).show();
        }

    }

    public class AddProductForCart extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String id = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONST.ADDCART + "?id=" + id);
            if (readUtil.GET_SESSIONID(ProductDetails.this) != null) {
                httpPost.setHeader("Cookie", readUtil.GET_SESSIONID(ProductDetails.this));
            }
            try {
                client.execute(httpPost);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid == true) {
                Toast.makeText(ProductDetails.this, "添加成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductDetails.this, "网络有问题", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
