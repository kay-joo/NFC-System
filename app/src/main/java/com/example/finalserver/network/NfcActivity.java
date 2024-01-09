package com.example.finalserver.network;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalserver.MainMenuActivity;
import com.example.finalserver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NfcActivity extends AppCompatActivity {
    Button tagRegister, back;
    private EditText NFC_UID, placeName, placeAdd, placeTel, current_time;
    private TextView go_user_list;
    private TextView timeTitle;
    private String register_exists = "";
    Handler mHandler = new Handler();
    ServiceNfcTagApi nfcTagApi;
    private PreferenceHelper preferenceHelper;
    private String strNfcUid;
    private boolean tag_status;
    // NFC 기술이 감지할 수 있는 태그를 나열합니다.
    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    NdefFormatable.class.getName(),
                    TagTechnology.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(),
                    Ndef.class.getName()
            }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag);

        // tagRegister = findViewById(R.id.button);    //태그 액티비티에서 등록 버튼
        back = findViewById(R.id.button2);
        //btnWrite = findViewById(R.id.btnWrite);
        placeName = findViewById(R.id.editTagPlaceName);
        NFC_UID = findViewById(R.id.editTagNfcUid);
        placeAdd = findViewById(R.id.editTagAddress);
        placeTel = findViewById(R.id.editTagTel);
        current_time = findViewById(R.id.editCurrentTime);
        timeTitle = findViewById(R.id.textView9);
        go_user_list = findViewById(R.id.showList);

        preferenceHelper = new PreferenceHelper(this);

        MainMenuActivity mainMenuActivity = new MainMenuActivity();

        back.setOnClickListener(new View.OnClickListener() {   //홈으로 가기 버튼 눌렀을 시
            @Override
            public void onClick(View view) {
                Intent back_intent = new Intent(NfcActivity.this, MainMenuActivity.class);
                startActivity(back_intent);
                finish();
            }
        });

        go_user_list.setOnClickListener(new View.OnClickListener() {  //리스트 목록보기 버튼을 눌렀을 경우
            @Override
            public void onClick(View view) {
                Intent go_main_intent = new Intent(NfcActivity.this, MainMenuActivity.class);
                go_main_intent.putExtra("ListFragmentForUser", 1);  //일반 유저용 리스트 프래그먼트로 이동할 수 있도록 정수형 데이터 삽입
                startActivity(go_main_intent);

            }
        });


        Toast.makeText(getApplicationContext(), "태그 해주세요", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void uploadTagServer() {          //등록시 서버로 데이터 전송하고 받는 통신 메소드
        final String strUserEmail = preferenceHelper.getEmail();
        final String strNfcUID = NFC_UID.getText().toString();  //현재 nfc_uid값
        Log.d("strttt", strNfcUID);
        String str_statusExists_two = ""; //초기화
        final String str_dataSaved_nfcUid = preferenceHelper.getNfcUID();
        Log.d("strttt2", str_dataSaved_nfcUid);


        if(register_exists.equals("no card")){  //등록되지 않은 카드를 태그했을 경우
            Toast.makeText(getApplicationContext(), "등록되지 않았습니다", Toast.LENGTH_SHORT).show();
            placeName.setText("");
            placeAdd.setText("");
            placeTel.setText("");
            current_time.setText("");
            register_exists = "";
            timeTitle.setText("null");
            Toast.makeText(getApplicationContext(), "다시 한번 태그해주세요", Toast.LENGTH_SHORT).show();
            Log.d("NfcActivity", "no register");
        }else {  //등록된 카드를 태그했을 경우
            if ((str_dataSaved_nfcUid.equals(strNfcUID)) && (preferenceHelper.getExistsStatus().equals("true"))) {
                //이전에 저장된 태그 카드와 새로 찍은 카드의 아이디가 동일하고 동시에 이전에 입실이었던 경우

                //if((preferenceHelper.getEmailTwo().equals(preferenceHelper.getEmailTwo())) == true){
                    //로그아웃 등을 했을 때, 같은 사용자가 로그인했을 경우
                    str_statusExists_two = "퇴실";
                    timeTitle.setText("퇴실 시간");
                    preferenceHelper.removeNfcUID();  //nfcUid를 삭제하는 메소드 호출
                    preferenceHelper.putExistsStatus("false");
                    tag_status = false; //상태를 퇴실했다고 false로 처리
                    Log.d("풋", preferenceHelper.getExistsStatus());

                    Toast.makeText(getApplicationContext(), str_statusExists_two, Toast.LENGTH_SHORT).show();
                    Log.v("NFCACTIVITY", "퇴실 완료 체크");


            } else if((!str_dataSaved_nfcUid.equals(strNfcUID)) && preferenceHelper.getExistsStatus().equals("true")){
                //이전에 입실이었는데 퇴실 처리 안하고 다른 카드 찍었을 경우
                Toast.makeText(getApplicationContext(), "입실", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "이전 강의실 퇴실이 완료되지 않았습니다. 확인하세요", Toast.LENGTH_LONG).show();
                timeTitle.setText("입실 시간");
                str_statusExists_two = "이전 퇴실x,       현재장소 입실";
                tag_status = false;  //임의로 퇴실이라고 강제 설정, 그래야 다른 카드를 태그했을 때 제대로 판정됨.
                preferenceHelper.getExistsStatus().equals(tag_status);
            } else if (tag_status == false && (!str_dataSaved_nfcUid.equals(strNfcUID))) {   //이전에 퇴실 이었던 경우, 태그한 카드가 동일하지 않은 경우
                Log.d("테스트플로우", "입실?");
                Log.d("테스트플로우1", str_dataSaved_nfcUid);
                Log.d("테스플로우2", strNfcUID);
                timeTitle.setText("입실 시간");
                str_statusExists_two = "입실";
                tag_status = true;
                preferenceHelper.putExistsStatus("true");
                Toast.makeText(getApplicationContext(), str_statusExists_two, Toast.LENGTH_SHORT).show();
                Log.v("NFCACTIVITY", "입실 완료 체크");
            }else if(tag_status == false && (str_dataSaved_nfcUid.equals(strNfcUID))){ //이전에 퇴실이었고, 태그한 카드가 동일한 경우
                timeTitle.setText("입실 시간");
                str_statusExists_two = "입실";
                tag_status = true;
                preferenceHelper.putExistsStatus("true");
                Toast.makeText(getApplicationContext(), str_statusExists_two, Toast.LENGTH_SHORT).show();
            }

            /*else if(preferenceHelper.getEmailTwo().equals(preferenceHelper.getEmail()) && (str_dataSaved_nfcUid.equals(strNfcUID))){
                //로그아웃 후, 이전에 같은 사용자 로그인이고 태그한 카드가 동일한 경우

                if(tag_status == true){  //로그아웃 하기전에 입실이었던 상태라면
                    timeTitle.setText("퇴실 시간");
                    str_statusExists_two = "퇴실";
                    preferenceHelper.putExistsStatus("false");
                    preferenceHelper.removeNfcUID(); //nfcUID를 삭제하는 메소드 호출
                    tag_status = false;

                }else if(tag_status == false){  //로그아웃 하기 전에 퇴실 이었던 상태라면
                    timeTitle.setText("입실 시간");
                    str_statusExists_two = "입실";
                    preferenceHelper.putExistsStatus("true");
                    tag_status = true;

                }else{
                    Log.d("nfcActivity inout error", "check!!!!");
                }




            }else if((preferenceHelper.getEmailTwo().equals(preferenceHelper.getEmail())) && ((str_dataSaved_nfcUid.equals(strNfcUID)) == true)){
                //로그아웃 후, 이전에 같은 사용자 로그인 이지만, 태그한 카드가 동일하지 않은 경우

                if(tag_status == true){ //로그아웃 하기전에 입실이었던 상태라면
                    //이전에 입실이었는데 퇴실 처리 안하고 다른 카드 찍었을 경우
                    Toast.makeText(getApplicationContext(), "입실", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "이전 강의실 퇴실이 완료되지 않았습니다. 확인하세요", Toast.LENGTH_LONG).show();
                    timeTitle.setText("입실 시간");
                    str_statusExists_two = "exit error";
                    tag_status = false;  //임의로 퇴실이라고 강제 설정, 그래야 다른 카드를 태그했을 때 제대로 판정됨.
                    preferenceHelper.getExistsStatus().equals(tag_status);

                }else if(tag_status == false){ //로그아웃 하기 전에 퇴실이었던 상태라면
                    timeTitle.setText("입실 시간");
                    str_statusExists_two = "입실";
                    tag_status = true;
                    preferenceHelper.putExistsStatus("true");
                    Toast.makeText(getApplicationContext(), str_statusExists_two, Toast.LENGTH_SHORT).show();
                }

            }else if(((preferenceHelper.getEmailTwo().equals(preferenceHelper.getEmail())) == false) && ((str_dataSaved_nfcUid.equals(strNfcUID)) == true)){
                //로그아웃 후, 다른 사용자 로그인이면서 태그한 카드가 동일한 경우 (회원정보를 수정한 경우)
                if(tag_status == true){  //로그아웃 하기전에 입실이었던 상태라면
                    timeTitle.setText("퇴실 시간");
                    str_statusExists_two = "퇴실";
                    preferenceHelper.putExistsStatus("false");
                    preferenceHelper.removeNfcUID(); //nfcUID를 삭제하는 메소드 호출
                    tag_status = false;
                }else if(tag_status == false){  //로그아웃 하기 전에 퇴실 이었던 상태라면
                    timeTitle.setText("입실 시간");
                    str_statusExists_two = "입실";
                    preferenceHelper.putExistsStatus("true");
                    tag_status = true;
                }else{
                    Log.d("nfcActivity inout error", "check!!!!");
                }
            }else if(((preferenceHelper.getEmailTwo().equals(preferenceHelper.getEmail())) == false) &&
                    ((str_dataSaved_nfcUid.equals(strNfcUID)) == false)){
                //로그아웃하고 다른 사용자 로그인이면서 태그한 카드가 동일하지 않은 경우
                if(tag_status == true){ //로그아웃 하기전에 입실이었던 상태라면
                    //이전에 입실이었는데 퇴실 처리 안하고 다른 카드 찍었을 경우
                    Toast.makeText(getApplicationContext(), "입실", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "이전 강의실 퇴실이 완료되지 않았습니다. 확인하세요", Toast.LENGTH_LONG).show();
                    timeTitle.setText("입실 시간");
                    str_statusExists_two = "exit error";
                    tag_status = false;  //임의로 퇴실이라고 강제 설정, 그래야 다른 카드를 태그했을 때 제대로 판정됨.
                    preferenceHelper.getExistsStatus().equals(tag_status);

                }else if(tag_status == false){ //로그아웃 하기 전에 퇴실이었던 상태라면
                    timeTitle.setText("입실 시간");
                    str_statusExists_two = "입실";
                    tag_status = true;
                    preferenceHelper.putExistsStatus("true");
                    Toast.makeText(getApplicationContext(), str_statusExists_two, Toast.LENGTH_SHORT).show();
                }



            }

         */


        } //큰 else문 닫기

        Log.d("NFCUID", strNfcUID);
        preferenceHelper.putNfcUID(strNfcUID);  //preferences에 일시 저장 (이전 태그 카드 정보)

        // Log.d("테스트404", toString(str_statusExists_t);

        nfcTagApi = RetrofitClient.joinConfig().create(ServiceNfcTagApi.class);
        Call<String> call = nfcTagApi.Nfc_Tag(strUserEmail, strNfcUID, str_statusExists_two);


        call.enqueue(new Callback<String>() {                                                   //콜백 메소드 선언(string object로 json parsing하기 위함)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {              //정상적으로 통신이 되어 응답 받을 때
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("onSuccess", response.body());
                    String jsonResponse = response.body();
                    try {
                        parseRegTaggingData(jsonResponse);                                             //데이터를 json 형식에서 parsing하기 위한 메소드 호출
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

    private void parseRegTaggingData(String response) throws JSONException {                            //json string object에서 parsing하는 메소드
        JSONObject jsonObject = new JSONObject(response);
        String getEmail = preferenceHelper.getEmail();
        if(jsonObject.getString("message").equals("timeout!")){
            Log.d("session timeout", "타임아웃!!세선 만료");
            String getStatus = preferenceHelper.getExistsStatus();
            String getMessageRemove = preferenceHelper.removeAllPreferences(); //로그아웃(데이터 삭제)완료되었다고 알림 받음
            //sharedPreferences에 저장되어 있는 모든 데이터를 삭제함
            //preferenceHelper.putExistsStatus(getStatus); //로그아웃 할 경우 퇴실이어야 하는데 입실임. 코드 삽입
            // Log.d("ttrrPutExists", getStatus);
            //이전 출석 상태를 내부에 저장하는 것
            preferenceHelper.putEmailTwo(getEmail); //로그아웃 할 경우 로그아웃하기 전 사용자 아이디를 내부에 저장
            Log.d("ttrrPutEmailTwo", getEmail);
            Toast.makeText(NfcActivity.this, "세션 만료", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NfcActivity.this, MainMenuActivity.class);
            startActivity(intent);

        }else if (jsonObject.optString("message").equals("complete")) {
            //Toast.makeText(getApplicationContext(), "완료", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            Log.d("nodata?", jsonObject.getString("message"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
// 특정한 이벤트가 발생했을 때 작동하는 인텐트입니다.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
// 특정한 NFC 이벤트가 발생했을 때 인텐트를 생성합니다.
        IntentFilter filter = new IntentFilter();
// 태그가 스마트 폰 근처로 접근했을 때 이벤트가 발생합니다.
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
// NFC 이벤트로부터 인텐트를 얻어 디스패치를 활성화합니다.
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    // Foreground 상태에서 호출되는 새로운 인텐트입니다.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            NFC_UID =  findViewById(R.id.editTagNfcUid);
            strNfcUid = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
            NFC_UID.setText(strNfcUid);
            Log.v("test1234", "onNewIntent 호출");
            //preferenceHelper.putNfcUID(strNfcUid);  //sharedPreferences에 저장



            ProgressDialog dialog = new ProgressDialog(NfcActivity.this);  //getApplicationContext()를 사용하면 오류남
            //android.view.WindowManager$BadTokenException: Unable to add window -- token null is not valid; is your activity running?
            //로그 확인 가능
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("등록 중...");
            dialog.show();

            mHandler.postDelayed(new Runnable() { //서버에 등록 요청하는 시간이 걸리므로 충분한 로딩 지연
                public void run() {
                    // 시간 지난 후 실행할 코딩
                    uploadTagServer();  //서버에 정보를 업로드하는 메소드 호출
                    //placeName.setText(strNfcUid);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }, 1600); // 1.6초 후
            requestPlaceInfo(); //Nfc액티비티에 장소명, 주소, 전화번호, 시간을 다시 내려 받는 응답 메소드 호출
        }
        }

    // 얻어낸 바이트 배열을 문자열로 반환합니다.
    private String ByteArrayToHexString(byte [] array) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < array.length ; ++j)
        {
            in = (int) array[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    public void requestPlaceInfo(){   //nfcUid로 place 테이블에서 정보를 가져오기 위한 메소드
        final String nfcUidToRequest = NFC_UID.getText().toString();

        nfcTagApi = RetrofitClient.joinConfig().create(ServiceNfcTagApi.class);
        Call<String> call = nfcTagApi.placeInFo_response(nfcUidToRequest);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("requestPlaceInfo success", response.body());
                    String jsonResponse = response.body();
                    try {
                        parseRegPlaceInfoData(jsonResponse);                                             //데이터를 json 형식에서 parsing하기 위한 메소드 호출
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
                Toast.makeText(getApplicationContext(), "서버 통신 에러", Toast.LENGTH_SHORT).show();
                preferenceHelper.removeStatus();
                placeName.setText("null");
                placeAdd.setText("null");
                placeTel.setText("null");
                current_time.setText("null");
            }
        });

    }

    public void parseRegPlaceInfoData(String response) throws JSONException{   //Place테이블에서 빼내온 json스트링 객체 배열 데이터를 parsing하기 위한 클래스
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.optString("message").equals("complete select")) {
            //Toast.makeText(getApplicationContext(), "정상적으로 태그 인증되었습니다", Toast.LENGTH_SHORT).show();
            Log.e("parseRegPlaceInfoData", "success parse Place Data");
            JSONArray dataArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++){
                JSONObject dataobj = dataArray.getJSONObject(i);
                placeName.setText(dataobj.getString("placeName"));
                placeAdd.setText(dataobj.getString("placeAdd"));
                placeTel.setText(dataobj.getString("placeTel"));
                current_time.setText(dataobj.getString("current_time"));
            }
        } else {
            //no data를 서버로부터 응답받을 것
            Log.e("parseRegPlaceInfoData", jsonObject.getString("message"));
            Log.d("nodata?2", jsonObject.getString("message"));

            //register_exists = "no card";
        }
    }


    }





