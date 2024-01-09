package com.example.finalserver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalserver.fragment.JoinFragment;
import com.example.finalserver.fragment.LoginFragment;
import com.example.finalserver.fragment.MainWebViewFragment;
import com.example.finalserver.fragment.ModifyInfoFragment;
import com.example.finalserver.fragment.UserViewListFragment;
import com.example.finalserver.network.NfcActivity;
import com.example.finalserver.network.PreferenceHelper;
import com.example.finalserver.network.RetrofitClient;
import com.example.finalserver.network.ServiceLoginApi;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuActivity extends AppCompatActivity {         //Drawer view가 있는 액티비티임
    private FragmentManager manager;                               //프래그먼트 매니저 인스턴스 생성
    private FragmentTransaction ft;                                //프래그먼트 트랜잭션 인스턴스 생성
    private PreferenceHelper preferenceHelper;
    private int num = 0; //초기화
    List<String> mSelectedItems;
    ServiceLoginApi logoutApi;
    TextView showUser;
    NavigationView navigationView;
    Boolean check;
    SharedPreferences shd;
    Button deleteAccountBtn;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenuactivity);

        preferenceHelper = new PreferenceHelper(this);

        if (preferenceHelper.getEmail().equals("")) {
            //로그인 되지 않은 상태이므로 그냥 액티비티 유지
        } else {
            set_naviHeader_str(1);
        }

        final DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        MainWebViewFragment wvFragment = new MainWebViewFragment();
        manager = getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.frame_container, wvFragment).commit();  //초기화면을 해당 프래그먼트로 설정

        ImageView showNavBtn = findViewById(R.id.imageMenu);   //네비게이션 드로워 뷰를 오픈하기 위한 버튼 뷰 인스턴스 생성

        showNavBtn.setOnClickListener(new View.OnClickListener() {        //오픈 버튼을 클릭했을 때
            @Override
            public void onClick(View v) {
                // start에 지정된 Drawer 열기
                drawerLayout.openDrawer(GravityCompat.START);  //네비게이션 뷰 드로워 레이아웃 오픈
            }
        });

        navigationView = findViewById(R.id.navigation_view);  //네비게이션 뷰 인스턴스 생성
        navigationView.setItemIconTintList(null);   //리스트 초기화


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {  //navigation View 메뉴 아이템 클릭 콜백 메소드
                int id = item.getItemId();
                manager = getSupportFragmentManager();   //프래그먼트 매니저 객체 생성
                ft = manager.beginTransaction();         //프래그먼트 화면 구현을 위한 코드 선언
                switch (id) {
                    case R.id.nav_home: //홈 버튼을 눌렀을 경우
                        ft.replace(R.id.frame_container, new MainWebViewFragment(), "WebView");
                        ft.commitAllowingStateLoss();
                        break;
                    case R.id.nav_login: //로그인 버튼을 눌렀을 경우
                        if (preferenceHelper.getEmail().equals("")) { //로그인 되지 않은 상태라면
                            ft.replace(R.id.frame_container, new LoginFragment(), "LOGIN");  //프래그먼트를 login 프래그먼트 액티비티로 변경
                            ft.commitAllowingStateLoss();

                            Log.d("유저이메일스", preferenceHelper.getEmailTwo());
                            Log.d("유저이메일스2", preferenceHelper.getEmail());
                            if (!preferenceHelper.getEmailTwo().equals(preferenceHelper.getEmail())) {
                                preferenceHelper.putExistsStatus("false"); //로그아웃을 했거나 다른사람 아이디로 태그한 경우
                                //무조건 퇴실 처리 없이 입실 처리
                            } else {

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "이미 로그인된 상태입니다", Toast.LENGTH_SHORT).show();
                        }


                        break;

                    case R.id.nav_modify: //수정 버튼을 눌렀을 경우
                        if (preferenceHelper.getEmail().length() == 0) { //
                            Log.d("GETEMAIL", preferenceHelper.getEmail());
                            Toast.makeText(getApplicationContext(), "로그인 상태가 아닙니다", Toast.LENGTH_SHORT).show();
                        } else {
                            ft.replace(R.id.frame_container, new ModifyInfoFragment(), "MODIFY");
                            ft.commitAllowingStateLoss();
                        }
                        break;

                    case R.id.nav_join:  //가입 버튼을 눌렀을 경우
                        if (preferenceHelper.getEmail().equals("")) {
                            ft.replace(R.id.frame_container, new JoinFragment(), "JOIN");   //프래그먼트를 join 프래그먼트 액티비티로 변경
                            ft.commitAllowingStateLoss();
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 로그인된 상태로 가입 불가입니다", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.nav_showList:
                        if (preferenceHelper.getEmail().equals("")) {
                            Toast.makeText(getApplicationContext(), "로그인된 상태가 아닙니다", Toast.LENGTH_SHORT).show();
                        } else {
                            ft.replace(R.id.frame_container, new UserViewListFragment(), "VIEWLIST");
                            ft.commitAllowingStateLoss();
                        }
                        break;

                    case R.id.nav_del:  //탈퇴 버튼을 눌렀을 경우
                        check = false;
                        Log.d("delete", "click");
                        if (preferenceHelper.getEmail().equals("")) { //로그인을 하지 않은 것

                        } else {
                            new AlertDialog.Builder(MainMenuActivity.this)
                                    .setTitle("Alert!").setMessage("계정 탈퇴 하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            new AlertDialog.Builder(MainMenuActivity.this)
                                                    .setTitle("Warning! 자동 로그아웃됩니다").setMessage("패스워드를 입력하세요")
                                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            CustomDialog customDialog = new CustomDialog(MainMenuActivity.this);
                                                            customDialog.callFunction();
                                                            // set_naviHeader_str(3);

                                                                shd = PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this);

                                                            //dialogInterface.dismiss(); //다이얼 로그 제거
                                                            if (shd.getString("delete key", null).equals("xdss0x")) {
                                                                set_naviHeader_str(2);
                                                                Log.d("MainSetTTTT", "okay");
                                                                preferenceHelper.removeEmail();
                                                                Log.d("check2", "false!");
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            onFragmentChanged(3); //홈으로 가기
                                                        }
                                                    })
                                                    .show();
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

                        Log.d("check", "true!");

                        break;

                    case R.id.nav_tag:
                        if (preferenceHelper.getEmail().equals("")) {  //로그인 되지 않은 상태라면
                            Toast.makeText(getApplicationContext(), "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show();
                        } else {  //로그인 된 상태라면
                            Intent go_tagActivity_intent = new Intent(MainMenuActivity.this, NfcActivity.class);
                            startActivity(go_tagActivity_intent);
                        }
                        break;
                    case R.id.nav_logout:
                        if (preferenceHelper.getEmail().equals("")) {  //Email 칸이 공백일 경우 (로그인을 하지 않은 것)
                            Toast.makeText(getApplicationContext(), "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show();
                            //로그아웃 비활성화
                        } else {  //Email칸이 공백이 아니므로 로그인 되있는 경우
                            Log.d("vammmmmmmm", "else진입");
                            new AlertDialog.Builder(MainMenuActivity.this)   //다이얼로그 팝업 선언
                                    .setTitle("Logout").setMessage("로그아웃 하시겠습니까?")
                                    .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String getEmail = preferenceHelper.getEmail();
                                            Log.d("ttrrGetEmail1", getEmail);
                                            logoutApi = RetrofitClient.logoutConfig().create(ServiceLoginApi.class);
                                            Call<String> call = logoutApi.logOut("ok"); //logout하도록 서버에 다시 요청 request

                                            call.enqueue(new Callback<String>() {
                                                @Override //서버와 요청 응답 통신 성공일 경우
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    String rb = response.body();
                                                    Log.d("amba", "test1234");
                                                    okCheck(rb);
                                                    dialogInterface.dismiss();  //다이얼로그 삭제

                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    Toast.makeText(MainMenuActivity.this, "로그아웃 불가, 관리자에게 문의 f", Toast.LENGTH_SHORT).show();
                                                    dialogInterface.dismiss();  //다이얼로그 삭제
                                                }
                                            });


                                        }

                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onFragmentChanged(3); //home으로 돌아감
                                        }
                                    })
                                    .show();
                        }
                        break;
                }
                return false;
            }
        });

        Intent fromUserViewListFragment = getIntent(); //userViewListFragment에서 온 데이터를 받는 인텐트 선언
        num = fromUserViewListFragment.getIntExtra("ListFragmentForUser", 0);
        if (num == 1) {
            onFragmentChanged(4);
            num = 0;  //다시 초기화 상태
        }


    }

    public void onFragmentChanged(int index) {      //index값에 따라 프래그먼트 전환 메소드

        if (index == 0) {     //로그인 프래그먼트
            manager.beginTransaction().replace(R.id.frame_container, new LoginFragment()).commit();
        } else if (index == 1) {   //조인 프래그먼트
            manager.beginTransaction().replace(R.id.frame_container, new JoinFragment()).commit();
            // } else if(index == 2){   //태그 프래그먼트
            //여기다가 nfc태그 프래그먼트 이동 코드 작성(인텐트 역할)
            //   manager.beginTransaction().replace(R.id.frame_container, new TagFragment()).commit();
        } else if (index == 3) {  //Home 버튼 클릭하기
            //web view 프래그먼트 이동 코드 작성
            manager.beginTransaction().replace(R.id.frame_container, new MainWebViewFragment()).commit();

        } else if (index == 4) {  //일반 유저만이 볼 수 있는 리스트 목록 프래그먼트
            manager.beginTransaction().replace(R.id.frame_container, new UserViewListFragment()).commit();
        }
    }

    public void set_naviHeader_str(int index) {   //navi_header의 이름을 출력하는 메소드
        navigationView = findViewById(R.id.navigation_view);  //네비게이션 뷰 인스턴스 생성
        View nav_header = navigationView.getHeaderView(0);     //nav_header 뷰 셋업
        //SharedPreferences sharedPreferences = getSharedPreferences("shared", Activity.MODE_PRIVATE); //sharedPreferences 객체 생성
        //String strName = sharedPreferences.getString("NAME", null);  //저장된 유저 이름 불러오기
        String strName = preferenceHelper.getName();
        Log.d("MainMenuActivity", "set up Complete!");
        if (index == 1) {
            showUser = nav_header.findViewById(R.id.showUserDisplay);    //showUser 뷰 인스턴스 생성
            showUser.setText(strName + "님 환영합니다");
        } else if (index == 2) {
            showUser = nav_header.findViewById(R.id.showUserDisplay);
            showUser.setText("로그인 상태가 아닙니다. 로그인 하세요!");
        } else if (index == 3) {
            showUser = nav_header.findViewById(R.id.showUserDisplay);
            showUser.setText("계정 탈퇴 중...");
        } else {
            int c = index;
            c = 2;
            index = c;
        }

    }

    @Override
    protected void onPause() {  //activity가 보이지 않을때 sharedPreferences에 값을 저장함
        super.onPause();
        if (!preferenceHelper.getName().equals("")) {  //null값이 아닌 경우
            set_naviHeader_str(1);
        }

    }

    public void okCheck(String response) {
        String getE = preferenceHelper.getEmail();
        Log.d("floooooowwww", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("flooooowwwww", "yes");
            if (jsonObject.getString("message").equals("200")) {
                //로그아웃이 정상적인 처리(session이 제대로 파괴되었을 경우)
                Log.d("mmmmmm", jsonObject.getString("message"));
                String getStatus = preferenceHelper.getExistsStatus();
                String getMessageRemove = preferenceHelper.removeAllPreferences(); //로그아웃(데이터 삭제)완료되었다고 알림 받음
                //sharedPreferences에 저장되어 있는 모든 데이터를 삭제함
                //preferenceHelper.putExistsStatus(getStatus); //로그아웃 할 경우 퇴실이어야 하는데 입실임. 코드 삽입
                // Log.d("ttrrPutExists", getStatus);
                //이전 출석 상태를 내부에 저장하는 것
                preferenceHelper.putEmailTwo(getE); //로그아웃 할 경우 로그아웃하기 전 사용자 아이디를 내부에 저장
                Log.d("ttrrPutEmailTwo", getE);

                if (getMessageRemove.equals("All delete")) {
                    set_naviHeader_str(2);  //naviHeader의 텍스트를 공백으로 만드는 메소드 호출
                    Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_SHORT).show();
                    onFragmentChanged(3);
                } else {
                    Toast.makeText(getApplicationContext(), "관리자 문의", Toast.LENGTH_SHORT).show();
                }

            } else {  //400을 리턴받을 경우
                Toast.makeText(MainMenuActivity.this, "로그아웃 불가, 관리자에게 문의 e", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
