package com.example.finalserver.fragment;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class JoinFragment extends Fragment {         //Join Fragment Activity (회원가입 창 프래그먼트 액티비티)
    private EditText email, epwd, epwdCheck, ename, eadd, etel;
    Button btnregister, btnBack;
    ProgressDialog dialog;
    Handler mHandler = new Handler();
    private PreferenceHelper preferenceHelper;

    public JoinFragment(){}   //생성자

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

    @Nullable
    @Override
    //프래그먼트는 onCreateView 메소드를 onCreate메소드로 다룸
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_fragment, container, false);  //동적 인플레이트 생성
        email = view.findViewById(R.id.editEmail);
        epwd = view.findViewById(R.id.editPassword);
        epwdCheck = view.findViewById(R.id.editPwck);
        ename = view.findViewById(R.id.editName);
        eadd = view.findViewById(R.id.editAddress);
        etel = view.findViewById(R.id.editTel);
        btnregister = view.findViewById(R.id.register);
        btnBack = view.findViewById(R.id.cancel_button);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();   //메인 메뉴 액티비티 캐스팅


        /*******
        여기에 위 뷰 컴포넌트 들에 따른 회원 가입 (이메일, 비밀번호, 이름) 기입이 제대로 되었는지 체크하는
         메소드를 기입해주세요
         다른건 안만지셔도 됩니다.

         */

        btnregister.setOnClickListener(new View.OnClickListener() {   //회원가입 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                registerMe();  //서버에 정보를 업로드하는 메소드 호출 및 빈칸 없이 입력했는지 확인 하는 메소드
                dialog = new ProgressDialog(getActivity());
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("회원 가입중...");
                dialog.show();
                mHandler.postDelayed(new Runnable()  {
                    @Override
                    public void run() {
                        // 시간 지난 후 실행할 코딩
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                            mainMenuActivity.onFragmentChanged(0);   //회원가입 등록 후, 로그인 프래그먼트 액티비티로 전환

                    }
                }, 500); // 3.5초후
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {   //취소 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                mainMenuActivity.onFragmentChanged(0);     //다른 프래그먼트로 이동하게 하는 메소드 호출
            }
        });
        return view;
    }
    private void registerMe(){          //회원가입시 서버로 데이터 전송하고 받는 통신 메소드
        final String userEmail = email.getText().toString();
        final String userPwd = epwd.getText().toString();
        final String userName = ename.getText().toString();
        final String userAdd = eadd.getText().toString();
        final String userTel = etel.getText().toString();
       // boolean bool; //1로 초기화

        ServiceJoinApi api = RetrofitClient.joinConfig().create(ServiceJoinApi.class);          //인터페이스를 구현하는 메소드
        Call<String> call = api.newUserJoin(userEmail, userPwd, userName, userAdd, userTel);    //api 메소드 호출


                call.enqueue(new Callback<String>() {                                                   //콜백 메소드 선언(string object로 json parsing하기 위함)
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {              //정상적으로 통신이 되어 응답 받을 때
                        if(response.isSuccessful() && response.body() != null){
                            Log.e("onSuccess connected", response.body());
                            String jsonResponse = response.body();
                            //saveInfoRegister(userEmail, userPwd, userName, userAdd, userTel);
                            try{
                                parseRegData(jsonResponse);                                             //데이터를 json 형식에서 parsing하기 위한 메소드 호출
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {                             //정상적으로 통신이 불가할때
                        Log.e(TAG, "에러 = " + t.getMessage());
                    }
                });
    }

    private void parseRegData(String response) throws JSONException {                            //json string object에서 parsing하는 메소드
        JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("status").equals("true")) {
                    Toast.makeText(getActivity(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }

}
