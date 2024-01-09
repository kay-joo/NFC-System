package com.example.finalserver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalserver.network.PreferenceHelper;
import com.example.finalserver.network.RetrofitClient;
import com.example.finalserver.network.ServiceLoginApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomDialog extends AppCompatActivity  {    //커스텀 다이얼로그 클래스(팝업창 메시지 만드는 클래스) 손대지 마세요
    private Context context;
    private ServiceLoginApi deleteApi;
    Dialog dig;
    private PreferenceHelper preferenceHelper;
    private int num, nums;
    //private Button inputPwd;

    public CustomDialog(Context context){
        this.context = context;
        preferenceHelper = new PreferenceHelper(this.context);
    }

    public void callFunction(){


        dig = new Dialog(this.context);
        dig.setContentView(R.layout.custom_dialog);
        CheckBox erorr = dig.findViewById(R.id.error);
        CheckBox use = dig.findViewById(R.id.use);
        CheckBox contents = dig.findViewById(R.id.contents);
        CheckBox frequency = dig.findViewById(R.id.frequency);
        dig.show();

        EditText inputPwd = dig.findViewById(R.id.customDi_pwd);
        Button delteBtn = dig.findViewById(R.id.deleteAccountBtn);

        delteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("alert!").setMessage("정말 탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkDeleteAccount(inputPwd.getText().toString());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                                .show();

            }
        });

        Log.d("qqqqqq", String.valueOf(num));

    }

    private void checkDeleteAccount(String pwd){
        deleteApi = RetrofitClient.loginConfig().create(ServiceLoginApi.class);

        final String userEmail = preferenceHelper.getEmail().toString();
        Call<String> call = deleteApi.delete(userEmail, pwd);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonRes = response.body();  //body를 문자열 화

                try{
                    JSONObject jsonObject = new JSONObject(jsonRes);  //제이슨 객체 생성 (다시)
                    if(jsonObject.getString("message").equals("success")) {
                        Toast.makeText(context.getApplicationContext(), "계정 탈퇴 완료", Toast.LENGTH_SHORT).show();
                        setDeleteEx();
                        dig.dismiss();
                    }else if(jsonObject.getString("message").equals("no delete")) {
                        Toast.makeText(context.getApplicationContext(), "탈퇴 오류", Toast.LENGTH_SHORT).show();
                        dig.dismiss();
                    }else{
                        Toast.makeText(context.getApplicationContext(), "비밀번호나 계정이 가입되어있는지 확인하세요", Toast.LENGTH_SHORT).show();
                        dig.dismiss();
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "서버 에러 404", Toast.LENGTH_SHORT).show();
                dig.dismiss();

            }
        });

    }

    public void setDeleteEx(){
        SharedPreferences shd = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shd.edit();
        editor.putString("delete key", "xdss0x");
        editor.commit();
        //preferenceHelper.putDeleteExists("xdss0x");
        //Log.d("delete2", preferenceHelper.getDeleteExists());

    }

  //  @Override
  //  protected void finalize() throws Throwable {
   //     super.finalize();
   //     preferenceHelper.putDeleteExists("xdss0x");

  //  }
}
