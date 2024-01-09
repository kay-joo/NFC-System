package com.example.finalserver.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalserver.MainMenuActivity;
import com.example.finalserver.R;
import com.example.finalserver.network.PreferenceHelper;
import com.example.finalserver.network.RetrofitClient;
import com.example.finalserver.network.ServiceJoinApi;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyInfoFragment extends Fragment {  //계정 수정 화면 프래그먼트 클래스
    private EditText inputEmail, inputPwd, inputName, inputAdd, inputTel;
    Button modifyBtn;
    TextView cancleBtn;
    private ServiceJoinApi modifyApi;
    ProgressDialog dialog;
    PreferenceHelper preferenceHelper;

    public ModifyInfoFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceHelper = new PreferenceHelper(getActivity());
        /**뒤로가기 백버튼 방지 **/
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modify_fragment, container, false);

        inputEmail = view.findViewById(R.id.inputEmail);
        inputPwd = view.findViewById(R.id.inputPwd);
        inputName = view.findViewById(R.id.inputName);
        inputAdd = view.findViewById(R.id.inputAdd);
        inputTel = view.findViewById(R.id.inputTel);
        modifyBtn = view.findViewById(R.id.modifyBtn);
        cancleBtn = view.findViewById(R.id.cancleView);

        //이 부분에 데이터 수정 (이메일 여부 체크, 비밀번호 체크)
        //다른건 건들이지 마세요. onCreateView메소드에 너무 길게 작성하면 가독성이 떨어지니
        //외부 메소드를 선언해서 호출 및 상호작용 하도록 해주세요.

        modifyBtn.setOnClickListener(new View.OnClickListener() {  //수정하기 버튼을 눌렀을 경우
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Alert!").setMessage("계정 수정을 하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Alert!").setMessage("정말 수정하시겠습니까?")
                                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                modifyWithServer();
                                                                dialogInterface.dismiss();

                                                               // preferenceHelper.removeAllPreferences();
                                                                MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
                                                                mainMenuActivity.set_naviHeader_str(2);
                                                                //preferenceHelper.removeAllPreferences(); //강제로 로그아웃 처리
                                                            }
                                                        })
                                                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                    }
                                                                }).show();

                                        dialogInterface.dismiss();


                                    }
                                })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();



            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {  //취소 버튼을 눌렀을 경우
            @Override
            public void onClick(View view) {
                MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
                mainMenuActivity.onFragmentChanged(3); //홈으로 이동
            }
        });


        return view;
    }

    private void modifyWithServer(){   //수정하기 위해 데이터를 서버에 올리는 메소드

        Handler responseHandlers = new Handler();

        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("서버에서 수정 중...");
        dialog.show();

        responseHandlers.postDelayed(new Runnable() {
            @Override
            public void run() {
                final String email = inputEmail.getText().toString();
                final String pwd = inputPwd.getText().toString();
                final String name = inputName.getText().toString();
                final String add = inputAdd.getText().toString();
                final String tel = inputTel.getText().toString();
                final String past_email = preferenceHelper.getEmail();

                if(email.equals("") || pwd.equals("") || name.equals("") || tel.equals("")){
                   // return;
                }

                modifyApi = RetrofitClient.joinConfig().create(ServiceJoinApi.class);
                Call<String> call = modifyApi.modifyAccountRequest(past_email, email, pwd, name, add, tel);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) { //서버와 네트워크 통신이 성공하였을 때
                        String jsonRes = response.body();
                        if (dialog != null) {
                            dialog.dismiss();   //다이얼로그 없애버림
                        }
                        parseModifyDataInfo(jsonRes);  //서버로부터 응답 받은 데이터를 해체하기 위한 메소드 호출
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {  //서버와 네트워크 통신이 불가할 때
                        Toast.makeText(getContext(), "서버 통신 불가",  Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }, 1400); //1.4초 지연


    }

    private void parseModifyDataInfo(String response){  //수정한 데이터가 제대로 올바르게 기입되었는지 응답받아 해체하는 메소드
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("message").equals("modify Complete")){
                Toast.makeText(getContext(), "계정 수정 완료", Toast.LENGTH_SHORT).show();

            }else if(jsonObject.getString("message").equals("null")){
                Toast.makeText(getContext(), "이메일, 비밀번호, 이름, 전화번호가 모두 기입되어야 합니다", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "로그아웃 처리되었습니다", Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException e){
           e.printStackTrace();
        }

        preferenceHelper.removeAllPreferences();

    }

    //외부 메소드 쓰는 곳
    //반드시 private void로 작성바랍니다
}
