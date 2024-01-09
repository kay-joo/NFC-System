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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalserver.MainMenuActivity;
import com.example.finalserver.R;
import com.example.finalserver.network.RetrofitClient;
import com.example.finalserver.network.ServicePlaceEnrollApi;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceEnrollFragment extends Fragment {
    private EditText pName, pUid, pAddress, pTel;
    Button btnReg;
    ProgressDialog dialog;
    Handler mHandler = new Handler();
    ServicePlaceEnrollApi peApi;

    public PlaceEnrollFragment() {
    }   //생성자

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    //프래그먼트는 onCreateView 메소드를 onCreate메소드로 다룸
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);  //동적 인플레이트 생성
        pName = view.findViewById(R.id.textView5);
        pUid = view.findViewById(R.id.textView8);
        pAddress = view.findViewById(R.id.textView6);
        pTel = view.findViewById(R.id.textView7);
        //btnReg = view.findViewById(R.id.button);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();   //메인 메뉴 액티비티 캐스팅

        btnReg.setOnClickListener(new View.OnClickListener() {   //등록 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                uploadServer();  //서버에 정보를 업로드하는 메소드 호출

                dialog = new ProgressDialog(getActivity());
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("등록...");
                dialog.show();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        // 시간 지난 후 실행할 코딩
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        mainMenuActivity.onFragmentChanged(4);   //출입기록으로 넘어가도록
                    }
                }, 500); // 3.5초후
            }
        });

        return view;
    }

    private void uploadServer() {          //등록시 서버로 데이터 전송하고 받는 통신 메소드
        final String NFC_UID = pName.getText().toString();
        final String placeName = pUid.getText().toString();
        final String placeAdd = pAddress.getText().toString();
        final String placeTel = pTel.getText().toString();

        peApi = RetrofitClient.joinConfig().create(ServicePlaceEnrollApi.class);          //인터페이스를 구현하는 메소드
        Call<String> call = peApi.Place_Enroll(NFC_UID, placeName, placeAdd, placeTel );    //api 메소드 호출


        call.enqueue(new Callback<String>() {                                                   //콜백 메소드 선언(string object로 json parsing하기 위함)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {              //정상적으로 통신이 되어 응답 받을 때
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("onSuccess", response.body());
                    String jsonResponse = response.body();
                    try {
                        parseRegData(jsonResponse);                                             //데이터를 json 형식에서 parsing하기 위한 메소드 호출
                    } catch (JSONException e) {
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
            Toast.makeText(getActivity(), "장소 등록", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
        }
    }
}
