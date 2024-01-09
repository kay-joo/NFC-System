package com.example.finalserver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.finalserver.MainMenuActivity;
import com.example.finalserver.R;
import com.example.finalserver.network.LoginResponse;
import com.example.finalserver.network.NfcActivity;
import com.example.finalserver.network.PreferenceHelper;
import com.example.finalserver.network.RetrofitClient;
import com.example.finalserver.network.ServiceLoginApi;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {          //로그인 프래그먼트 액티비티
    Button loginBtn, goJoinBtn;
    private EditText loginEmail, loginPwd;
    private String strEmail, strPwd;
    private ServiceLoginApi service;
    ProgressDialog dialog;
    //MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
    private PreferenceHelper preferenceHelper;
    private TextView showUser;

    public LoginFragment(){}


    //requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**뒤로가기 백버튼 방지 **/
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  //프래그먼트를 뷰 객체로 만드는 콜백 메소드
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        loginEmail=  view.findViewById(R.id.loginEmail);
        loginPwd = view.findViewById(R.id.loginPassword);
        loginBtn = view.findViewById(R.id.goLoginBtn);
        goJoinBtn = view.findViewById(R.id.goJoinBtn);



        preferenceHelper = new PreferenceHelper(getActivity());

            loginBtn.setOnClickListener(new View.OnClickListener() {  //로그인 하기 버튼
                @Override
                public void onClick(View view) {
                    //InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //버튼 클릭하면 키보드 내리기
                    // imm.showSoftInput(loginBtn, 0);
                    // imm.hideSoftInputFromWindow(loginBtn.getWindowToken(), 0);
                    upLoadToServer_LoginData();
                }
            });


        goJoinBtn.setOnClickListener(new View.OnClickListener() { //회원가입 하기 버튼
            @Override
            public void onClick(View view) {
                MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
                mainMenuActivity.onFragmentChanged(1);
            }
        });

        return view;
    }
    private void upLoadToServer_LoginData() {               //로그인 데이터를 서버로 보내고 다시 응답받아 로그인 성공 여부를 확인하는 메소드
        Handler responseHandler = new Handler();
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로그인 중...");
        dialog.show();
        responseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final String strEmail = loginEmail.getText().toString().trim();
                final String strPwd = loginPwd.getText().toString().trim();

                service = RetrofitClient.loginConfig().create(ServiceLoginApi.class); //ServiceLoginApi 인터페이스 구현
                //호출하여 서버에 요청하고 받을 수 있도록 인스턴스를 생성함
                Call<String> call = service.userLogin(strEmail, strPwd); //ServiceLoginApi 인터페이스를 호출하여 이 인터페이스의 userLogin 메소드를 호출
                //userLogin 메소드에 인자를 실어 보냄
                call.enqueue(new Callback<String>() {   //콜백 메소드 선언
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) { //서버로부터 응답받는 메소드(통신 성공시)
                        String jsonResResult = response.body();
                        Log.e("onSuccess", jsonResResult);
                        if (dialog != null) {
                            dialog.dismiss();   //다이얼로그 없애버림
                        }
                        parseLoginData(jsonResResult);  //파싱하는 메소드 호출
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {       //통신 미성공시
                        Toast.makeText(getContext(), "에러 발생 404", Toast.LENGTH_SHORT).show();
                        Log.e("로그인 에러 발생 404", t.getMessage());
                        if (dialog != null) {
                            dialog.dismiss();   //다이얼로그 없애버림
                        }
                       // dialog.dismiss();
                    }
                });
            }
        }, 1300);  //1.3초 지연
    }

    private void parseLoginData(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("message").equals("success")){
                Log.d("parse!!!!!!", jsonObject.getString("message"));
                preferenceHelper.putIsLogin(true);
                saveInfo(response);
                Log.d("getLogin???", preferenceHelper.getLoginCheck());
               // preferenceHelper.putLoginCheck("true");


                if(preferenceHelper.getLoginCheck().equals("true")){
                    //saveInfo(response);
                    Log.d("getCHeck!!!!", preferenceHelper.getLoginCheck());
                    saveInfo(response);
                    MainMenuActivity mainMenuActivitys = (MainMenuActivity) getActivity();
                    mainMenuActivitys.set_naviHeader_str(1);
                    Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), NfcActivity.class); //태그 액티비티로 이동
                    startActivity(intent);
                }
            }else{
                Toast.makeText(getContext(), "아이디나 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void saveInfo(String response){
        //preferenceHelper.putIsLogin(true);

        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("message").equals("success")){
                JSONArray dataArray = jsonObject.getJSONArray("data");
                preferenceHelper.putLoginCheck("true");

                for (int i = 0; i < dataArray.length(); i++){
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putEmail(dataobj.getString("userEmail"));
                    Log.d("dataobjGet??", preferenceHelper.getEmail());
                    Log.d("obj", dataobj.getString("userEmail"));
                    preferenceHelper.putPassword(dataobj.getString("userPwd"));
                    preferenceHelper.putName(dataobj.getString("userName"));
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


}
