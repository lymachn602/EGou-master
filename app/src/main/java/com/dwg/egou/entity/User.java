// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   User.java

package com.dwg.egou.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class User implements Parcelable
{

    public User()
    {
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public void setRegistertime(Timestamp registertime)
    {
        this.registertime = registertime;
    }

    public Timestamp getRegistertime()
    {
        return registertime;
    }
    //user����
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String role;
    private Timestamp registertime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(password);
        dest.writeString(nickname);
        dest.writeString(email);
        dest.writeString(role);
        dest.writeValue(registertime.getTime());
    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
    {
        public User createFromParcel(Parcel in)
        {
            User user =new User();
            user.setPassword(in.readString());
            user.setNickname(in.readString());
            user.setEmail(in.readString());
            user.setRole(in.readString());
            user.setRegistertime(new Timestamp(in.readLong()));
            return user;
        }

        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
}
