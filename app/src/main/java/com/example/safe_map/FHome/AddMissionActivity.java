package com.example.safe_map.FHome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.safe_map.Login.ChildnumItem;
import com.example.safe_map.Login.Signup;
import com.example.safe_map.Login.StdRecyclerAdapter;
import com.example.safe_map.NetworkStatus;
import com.example.safe_map.R;
import com.example.safe_map.common.ProfileData;
import com.example.safe_map.http.CommonMethod;
import com.example.safe_map.http.RequestHttpURLConnection;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AddMissionActivity extends AppCompatActivity {

    // 초기변수설정
    public Integer childnum1 = 0;
    int y1=0, m1=0, d1=0, h1=0, mi1=0;
    int y=0, m=0, d=0, h=0, mi=0;

    TextView edit_addr, date_view, time_view;
    EditText edit_content;
    Button addDate, addTime, addAddrBtn1, addAddrBtn2, checkDanger;

    private RecyclerView mRecyclerView;
    private StdRecyclerAdapter mRecyclerAdapter;
    private ArrayList<ChildnumItem> mChildnum;

    // 주소 요청코드 상수 requestCode
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mission);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        /* initiate adapter */
        mRecyclerAdapter = new StdRecyclerAdapter(this);

        final String[] childnum = {fetchChildNum(ProfileData.getUserId())};
        final int[] childNum = {Integer.parseInt(childnum[0])};
        /* adapt data */
        mChildnum = new ArrayList<>();
        for(int i = 1; i<= childNum[0]; i++){
            if (i==1){
                mChildnum.add(new ChildnumItem("첫째아이"));
            } else if (i==2){
                mChildnum.add(new ChildnumItem("둘째아이"));
            } else if (i==3){
                mChildnum.add(new ChildnumItem("셋째아이"));
            } else if (i==4){
                mChildnum.add(new ChildnumItem("넷째아이"));
            } else if (i==5) {
                mChildnum.add(new ChildnumItem("다섯째아이"));
            }

        }
        //mChildnum.add(new ChildnumItem("첫째아이"));
        //mChildnum.add(new ChildnumItem("둘째아이"));
        //mChildnum.add(new ChildnumItem("셋째아이"));

        mRecyclerAdapter.setChildNum(mChildnum);
        /* initiate recyclerview */
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));


        mRecyclerAdapter.setOnItemClickListener(new StdRecyclerAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                childnum1 = a_position;
                Toast.makeText(AddMissionActivity.this, childnum1, Toast.LENGTH_LONG).show();
            }
        });

        //자녀UUID 검색
        List<String> child = fetchUUID(ProfileData.getUserId());
        String childUUID = child.get(childnum1);

        //심부름 내용
        edit_content = findViewById(R.id.errandContent);

        //심부름 날짜 선택
        Calendar cal = Calendar.getInstance();
        y1 = cal.get(Calendar.YEAR);
        m1 = cal.get(Calendar.MONTH) +1;
        d1 = cal.get(Calendar.DAY_OF_MONTH);
        h1 = cal.get(Calendar.HOUR);
        mi1 = cal.get(Calendar.MINUTE);
        addDate = findViewById(R.id.date_btn);
        date_view = findViewById(R.id.date_view);
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMissionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        y = year;
                        m = month+1;
                        d = dayOfMonth;
                        date_view.setText(y+"년  "+m+"월  "+d + "일");
                    }
                },y1, m1, d1);
                datePickerDialog.setMessage("심부름 날짜");
                datePickerDialog.show();
            }

        });



        //심부름 시간 선택
        addTime = findViewById(R.id.time_btn);
        time_view = findViewById(R.id.timeView);
        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddMissionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        h = hourOfDay;
                        mi = minute;
                        time_view.setText(h+"시  "+mi+"분");
                    }
                }, h1, mi1, true);
                timePickerDialog.setMessage("출발 시각");
                timePickerDialog.show();
            }
        });

        String E_date = y+"-"+m+"-"+d+"T"+h+":"+"mi";
        // UI 요소 연결
        edit_addr = findViewById(R.id.editaddr_target);
        addAddrBtn1 = findViewById(R.id.add_adr_button1);
        addAddrBtn2 = findViewById(R.id.add_addr_button2);

        // 터치 안되게 막기
        //edit_addr.setFocusable(false);
        // 주소입력창 클릭
        addAddrBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지", "주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    Log.i("주소설정페이지", "주소입력창 클릭");
                    Intent i = new Intent(AddMissionActivity.this, AddressApiActivity.class);
                    // 화면전환 애니메이션 없애기
                    overridePendingTransition(0, 0);
                    // 주소결과
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);

                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("test", "onActivityResult");

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        Log.i("test", "data:" + data);
                        edit_addr.setText(data);
                    }
                }
                break;
        }
    }

    //리스트로 반환
    public List fetchUUID(String userId){
        String url = CommonMethod.ipConfig + "/api/fetchUUID";
        List<String> data = new ArrayList<String>();;

        try{
            String jsonString = new JSONObject()
                    .put("userId", userId)
                    .toString();

            //REST API
            RequestHttpURLConnection.NetworkAsyncTask networkTask = new RequestHttpURLConnection.NetworkAsyncTask(url, jsonString);
            data = Collections.singletonList(networkTask.execute().get());

//          Toast.makeText(getActivity(), "자녀 등록을 완료하였습니다.", Toast.LENGTH_SHORT).show();
//            Log.i(TAG, String.format("가져온 Phonenum: (%s)", rtnStr));

        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //자녀 정보 가져오기 -> Child DB정보 가져옴
    public String fetchChild(String UUID){
        String url = CommonMethod.ipConfig + "/api/fetchChild";
        String rtnStr= "";

        try{
            String jsonString = new JSONObject()
                    .put("UUID", UUID)
                    .toString();

            //REST API
            RequestHttpURLConnection.NetworkAsyncTask networkTask = new RequestHttpURLConnection.NetworkAsyncTask(url, jsonString);
            rtnStr = networkTask.execute().get();

//          Toast.makeText(getActivity(), "자녀 등록을 완료하였습니다.", Toast.LENGTH_SHORT).show();
//           Log.i(TAG, String.format("가져온 Phonenum: (%s)", rtnStr));

        }catch(Exception e){
            e.printStackTrace();
        }

        return rtnStr;

    }

    public void registerErrand(String user_Id, String UUID, String E_date, String E_content,
                               double target_latitude, double target_longitude,
                               double start_latitude, double start_longitude,
                               boolean checking){
        String url = CommonMethod.ipConfig + "/api/registerErrand";

        try{
            String jsonString = new JSONObject()
                    .put("userId", user_Id)
                    .put("E_date", E_date)
                    .put("E_content", E_content)
                    .put("target_latitude", target_latitude)
                    .put("target_longitude", target_longitude)
                    .put("start_latitude", start_latitude)
                    .put("start_longitude", start_longitude)
                    .put("checking", checking)
                    .toString();

            //REST API
            RequestHttpURLConnection.NetworkAsyncTask networkTask = new RequestHttpURLConnection.NetworkAsyncTask(url, jsonString);
            networkTask.execute().get();
            Toast.makeText(AddMissionActivity.this, "심부름이 설정되었습니다", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String fetchChildNum(String userId){
        String url = CommonMethod.ipConfig + "/api/fetchChildNum";
        String rtnStr= "";

        try{
            String jsonString = new JSONObject()
                    .put("userId", ProfileData.getUserId())
                    .toString();
            //REST API
            RequestHttpURLConnection.NetworkAsyncTask networkTask = new RequestHttpURLConnection.NetworkAsyncTask(url, jsonString);
            rtnStr = networkTask.execute().get();
//            Toast.makeText(Signup.this, "자녀 등록을 완료하였습니다.", Toast.LENGTH_SHORT).show();
 //           Log.i(TAG, String.format("가져온 childNum: (%s)", rtnStr));
        }catch(Exception e){
            e.printStackTrace();
        }
        return rtnStr;
    }
}