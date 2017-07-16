package com.dwg.egou.config;

/**
 * Created by Administrator on 2016/4/20.
 */
public class CONST {


   public static final String HOST = "http://123.206.56.157:8080/EGou/";
    public static final String GOOD_LIST_URI=HOST + "listProduct_mobile?flag=mobile";
    public static final String LOGIN_URI = HOST + "login?flag=mobile";
    public static final String CART = HOST+"showCartList";
    public static final String ADDCART= HOST + "addCart";
    public static final String REDUCE = HOST + "updateCartServlet";
    public static final String GENERATEORDER = HOST + "generateOrder";
    public static final String ORDERITEMLIST = HOST+"orderItemList";
    public static final String MODIFYUSER = HOST+"modifyUser";

    public static final String CATEGORY = HOST + "listProduct_mobile";
    public static final String SEARCHPRODUCT = HOST + "listProduct_mobile";
    public static final String REGISTER = HOST + "regist";
    public static final String MODIFYPASSWORD = HOST + "changePassword";

}
