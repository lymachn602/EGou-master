// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Product.java

package com.dwg.egou.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable
{

    public Product()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getImgsrc()
    {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc)
    {
        this.imgsrc = imgsrc;
    }

    public String getImg_s()
    {
        return img_s;
    }

    public void setImg_s()
    {
        getSmallImgsrc();
    }

    public int hashCode()
    {
        int prime = 31;
        int result = 1;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Product other = (Product)obj;
        if(id == null)
        {
            if(other.id != null)
                return false;
        } else
        if(!id.equals(other.id))
            return false;
        return true;
    }

    public String getSmallImgsrc()
    {
        int index = imgsrc.lastIndexOf(".");
        String tail = imgsrc.substring(index);
        String prefix = imgsrc.substring(0, index);
        img_s = (new StringBuilder(String.valueOf(prefix))).append("_s").append(tail).toString();
        return (new StringBuilder(String.valueOf(prefix))).append("_s").append(tail).toString();
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getNumber()
    {
        return number;
    }
    //Product����


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(imgsrc);
        dest.writeString(number);
    }
    public static final Parcelable.Creator<Product> CREATOR =  new Parcelable.Creator<Product>(){
        @Override
        public Product createFromParcel(Parcel source) {
            Product product =new Product();
            product.setId(source.readString());
            product.setName(source.readString());
            product.setPrice(source.readDouble());
            product.setCategory(source.readString());
            product.setDescription(source.readString());
            product.setImgsrc(source.readString());
            product.setNumber(source.readString());
            return product;
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    private String id;
    private String name;
    private double price;
    private String category;
    private String description;
    private String imgsrc;
    private String img_s;
    private String number;
}
