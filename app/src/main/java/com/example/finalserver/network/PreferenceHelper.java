package com.example.finalserver.network;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {   //쉐어드 관련 메소드를 모아둔 헬퍼 클래스
    //이 클래스는 간단한 데이터를 저장하고 불러올 수 있음. 앱을 꺼도 데이터가 유지되므로
    //서버의 세션 역할을 하는 클래스임
    public SharedPreferences app_prefs;
    public Context context;

    public PreferenceHelper(Context context)   //PreferenceHelper 클래스 생성자
    {
        app_prefs = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
        this.context = context;
    }

    public void putIsLogin(boolean loginOrOut)    //로그인하면 그 로그인 데이터를 내부에 저장하는 메소드(세션 역할)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean("INTRO", loginOrOut);
        edit.apply();
    }

    public void putDeleteExists(String loginOrOut){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("delete", loginOrOut);
        edit.apply();
    }

    public void putEmail(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("EMAIL", loginOrOut);
        edit.apply();
    }

    public void putEmailTwo(String loginOrOut){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("EMAILTWO", loginOrOut);
        edit.apply();
    }

    public void putPassword(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("PASSWORD", loginOrOut);
        edit.apply();
    }

    public void putName(String loginOrOut){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("NAME", loginOrOut);
        edit.apply();
    }

    public void putLoginCheck(String loginOrOut){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("CHECK", loginOrOut);
        edit.apply();
    }

    public void putExistsStatus(String loginOrOut){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("STATUS", loginOrOut);
        edit.apply();
    }

    public void putNfcUID(String loginOrOut){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("NFCUID", loginOrOut);
        edit.apply();
    }
   // public Boolean getIsLogin(){
      //  return app_prefs.getBoolean(INTRO, getIsLogin().booleanValue());
   // }
    public String getEmail(){
        return app_prefs.getString("EMAIL", "");
    }

   public String getName(){
        return app_prefs.getString("NAME", "");
    }

   // public String getPassword(){
   //     return app_prefs.getString("PASSWORD", null);
   // }

    public String getDeleteExists(){
        return app_prefs.getString("delete", "");
    }

    public String getLoginCheck(){
        return app_prefs.getString("CHECK", "");
    }

    public String getExistsStatus(){
        return app_prefs.getString("STATUS","");
    }

    public String getNfcUID(){
        return app_prefs.getString("NFCUID", "");
    }

    public String getEmailTwo(){
        return app_prefs.getString("EMAILTWO", "");
    }

    public void removeNfcUID(){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.remove("NFCUID");
        editor.commit();
    }

    public void removeEmail(){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.remove("EMAIL");
        editor.commit();
    }


    public void removeStatus(){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.remove("STATUS");
        editor.commit();
    }

    public String removeAllPreferences(){   //sharedPreferences에 저장된 모든 데이터를 삭제함(로그아웃)
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.clear();
        editor.commit();

        String returnMessage = "All delete";
        return returnMessage;
    }

}
