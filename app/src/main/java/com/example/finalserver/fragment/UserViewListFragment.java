package com.example.finalserver.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalserver.CustomAdapter;
import com.example.finalserver.MainMenuActivity;
import com.example.finalserver.R;
import com.example.finalserver.TagList;
import com.example.finalserver.network.PreferenceHelper;
import com.example.finalserver.network.RetrofitClient;
import com.example.finalserver.network.ServiceEntList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewListFragment extends Fragment {
    private EditText showUserName;
    private PreferenceHelper preferenceHelper;
    private ServiceEntList serviceEnt;
    ListView list;
    ArrayList<TagList> tl;
    private CustomAdapter customAdapter;
    private TextView reset;
    private Button homeBtn;


    public UserViewListFragment() {
    };
    //UserViewListFragment 생성자

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceHelper = new PreferenceHelper(getActivity());

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) { //뒤로 가기 방지
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        tl = new ArrayList<>();  //리스트를 배열 형식으로 보여주기 위한 어레이 리스트 객체 생성

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_list_view, container, false);  //동적 인플레이트 생성

        MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();

        list = rootView.findViewById(R.id.listView);

        homeBtn = rootView.findViewById(R.id.button3);

        showUserName = (EditText) rootView.findViewById(R.id.showUserName); //이름에 데이터 넣기
        tl = new ArrayList<>();

        downloadFromServerData();

        list = (ListView) rootView.findViewById(R.id.listView);
        customAdapter = new CustomAdapter(getContext(), tl);
        list.setAdapter(customAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //String selectedItem = (String) view.findViewById(R.id.textPlaceName).getTag().toString();
                //Toast.makeText(getContext(), "Clicked: " + position + " " + selectedItem, Toast.LENGTH_SHORT).show();

            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainMenuActivity.onFragmentChanged(3);  //홈버튼을 누르면 home fragment페이지로 이동
            }
        });


        //showUserName.setText();
        return rootView;
    }


    private void downloadFromServerData() {   //서버로부터 json데이터를 받는 메소드
        final String str_userEmail = preferenceHelper.getEmail();
        //Toast.makeText(getContext(), str_userEmail, Toast.LENGTH_SHORT).show();
        Log.d("strEmails", str_userEmail);
        serviceEnt = RetrofitClient.loginConfig().create(ServiceEntList.class); //ServiceLoginApi 인터페이스 구현
        //호출하여 서버에 요청하고 받을 수 있도록 인스턴스를 생성함
        Call<String> calls = serviceEnt.request_onlyUser_list(str_userEmail); //ServiceLoginApi 인터페이스를 호출하여 이 인터페이스의 userLogin 메소드를 호출

        calls.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // String jsonTagResult = response.body();
                Log.e("UserViewListFragment", "response성공");
                try {
                    parseUserTag(response.body());
                    Log.e("tttt", response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> calls, Throwable t) {
                Toast.makeText(getContext(), "에러 발생 404", Toast.LENGTH_SHORT).show();
                Log.e("통신 불가", t.getMessage());

            }
        });
    }


    private void parseUserTag(String response) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(response);   //JSON object 객체 생성
            showUserName.setText(jsonObject.getString("datas")); //사용자 이름 출력하기
            Log.d("dsdsdss",jsonObject.getString("datas"));

            //String testStr = jsonObject.getString("message");
            Log.d("testFlow111", jsonObject.getString("message"));

            if (jsonObject.getString("message").equals("found")) {  //성공일 시
                //showUserName.setText(jsonObject.getString("userNameData")); //userName 출력
                Log.d("pase!!!!!", jsonObject.toString());

                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataObj = dataArray.getJSONObject(i);  //장소 이름, 시간, 입실 퇴실 여부를 뽑아냄(한 데이터 튜플을 하나씩 파싱)
                    Log.d("pase!!!", "error??");

                    tl.add(new TagList(dataObj.getString("placeName"), dataObj.getString("current_time"),
                            dataObj.getString("status_exists")));

                }

            } else if (jsonObject.getString("message").equals("UID NODATA")) {
                Toast.makeText(getActivity(), "UID에 해당되는 데이터가 없습니다", Toast.LENGTH_SHORT).show();
            } else if (jsonObject.getString("message").equals("NOUSER")) {
                Toast.makeText(getActivity(), "저장된 유저 정보가 없습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "parseUserTagDatas", Toast.LENGTH_SHORT).show();
                Log.d("parseUserTagDatas", "error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}







