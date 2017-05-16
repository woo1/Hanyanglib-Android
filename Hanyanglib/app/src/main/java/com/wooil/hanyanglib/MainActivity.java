package com.wooil.hanyanglib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListViewAdapter adapter;
    private Boolean transaction = false; //데이터 조회 완료여부
    private TextView libTxtv;
    private TextView campusTxtv;
    private Boolean isSeoul = true; //서울캠인지
    private String libType = "0"; //0: 백남, 1: 법학

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView;

        campusTxtv = (TextView) findViewById(R.id.main_campus_name);
        libTxtv    = (TextView) findViewById(R.id.main_lib_name);

        campusTxtv.setText("서울");
        libTxtv.setText("백남도서관 빈자리");

        libTxtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSeoul){
                    Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                    intent.putExtra("data1", "백남학술정보관");
                    intent.putExtra("data2", "법학학술정보관");
                    intent.putExtra("tag", "1"); //"1" : 도서관, "2": 캠퍼스
                    startActivityForResult(intent, 1);
                }
            }
        });

        campusTxtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                intent.putExtra("data1", "서울");
                intent.putExtra("data2", "ERICA");
                intent.putExtra("tag", "2"); //"1" : 도서관, "2": 캠퍼스
                startActivityForResult(intent, 1);
            }
        });

        getTempValue();

        //adapter 생성
        adapter = new ListViewAdapter();

        listView = (ListView) findViewById(R.id.listView1) ;
        listView.setAdapter(adapter) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String name = item.getName();
                String linkURL = item.getLinkURL();

                Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
                String url = "http://libgate.hanyang.ac.kr/seats/"+linkURL;
                String js  = "";
                if(isSeoul == false){
                    url = "http://166.104.209.78/EZ5500/SEAT/RoomStatus.aspx";
                    js  = linkURL;
                }
                intent.putExtra("js", js);
                intent.putExtra("url", url);
                intent.putExtra("left", item.getLeft());
                intent.putExtra("tot", item.getTot());
                intent.putExtra("name", item.getName());
                startActivity(intent);
            }
        });

        PermissionRequester.Builder request = new PermissionRequester.Builder(this);
        request.create().request(Manifest.permission.INTERNET, 10000, new PermissionRequester.OnClickDenyButtonListener() {
            @Override
            public void onClick(Activity activity) {
                Toast.makeText(activity, "인터넷 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });

    }

    private void saveTempValue(){
        SharedPreferences pref = getSharedPreferences("PreferenceMain", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("is_seoul", isSeoul);
        editor.putString("lib_type", libType);
        editor.commit();
    }

    private void getTempValue(){
        SharedPreferences pref = getSharedPreferences("PreferenceMain", Activity.MODE_PRIVATE);
        String lib_type = pref.getString("lib_type", "0");
        Boolean is_seoul = pref.getBoolean("is_seoul", true);
        if(lib_type != null){
            libType = lib_type;
            isSeoul = is_seoul;

            if(!isSeoul) {
                libTxtv.setText("열람실 빈자리");
                campusTxtv.setText("ERICA");
            } else {
                if(libType.equals("0")){
                    libTxtv.setText("백남도서관 빈자리");
                } else if(libType.equals("1")){
                    libTxtv.setText("법학도서관 빈자리");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                //데이터 받기
                String rsltStr = data.getStringExtra("indexNo");
                String tag = data.getStringExtra("tag");
                if(rsltStr != null && rsltStr != ""){
                    if(tag != null && tag.equals("1")){
                        libType = rsltStr;
                        if(rsltStr.equals("0")){
                            libTxtv.setText("백남도서관 빈자리");

                            getData();
                        } else if(rsltStr.equals("1")) {
                            libTxtv.setText("법학도서관 빈자리");

                            getData();
                        }
                    } else if(tag != null && tag.equals("2")){
                        if(rsltStr.equals("0")){
                            isSeoul = true;
                            campusTxtv.setText("서울");
                            libType = "0";
                            libTxtv.setText("백남도서관 빈자리");

                            getData();
                        } else if(rsltStr.equals("1")) {
                            isSeoul = false;
                            campusTxtv.setText("ERICA");
                            libTxtv.setText("열람실 빈자리");

                            getData();
                        }
                    }
                }
            }
        }
    }

    private void getData(){
        saveTempValue();
        GetLibDataTask task = new GetLibDataTask();
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(transaction == false){
            GetLibDataTask task = new GetLibDataTask();
            task.execute();
            transaction = true;
        }
    }

    /**
     * Created by wijang on 2017. 5. 3..
     */
    private class GetLibDataTask extends AsyncTask<Void, Void, Map<String,String>> {

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            Map<String,String> result = new HashMap<String, String>();
            try {

                String url = "";
                if(isSeoul && libType.equals("0")){ //백남
                    url = "http://libgate.hanyang.ac.kr/seats/domian5.asp";
                } else if(isSeoul && libType.equals("1")){ //법학
                    url = "http://libgate.hanyang.ac.kr/seatl/domian5.asp";
                } else if(isSeoul == false){ //ERICA
                    url = "http://166.104.209.78/EZ5500/SEAT/RoomStatus.aspx";
                }
                Document document = Jsoup.connect(url).get();
                Elements elements = document.select("table");
                Element tableData = elements.get(1);
                Elements trList   = tableData.select("tr");
                Integer realIndex = 0;

                if(isSeoul == true){


                    for( int i = 0; i < trList.size(); i++ ) {
                        if(i > 2) {
                            Elements tdList = trList.get(i).select("td");

                            String   roomNm = tdList.get(1).select("font").get(0).text().trim();
                            String   linkURL = tdList.get(1).select("font").select("a").get(0).attr("href");
                            String   tot     = tdList.get(2).select("font").get(0).text().trim();
                            String   left    = tdList.get(4).select("font").get(0).text().trim();

                            if(roomNm != null && linkURL != null && tot != null && left != null){
                                result.put("data_"+realIndex, roomNm + "///" + left + "///" + tot + "///" + linkURL);
                                result.put("count", (realIndex+1)+ "");
                                realIndex++;
                            }
                        }
                    }
                } else {

                    for( int i = 0; i < trList.size(); i++ ) {
                        if(i > 0) {
                            Elements tdList = trList.get(i).select("td");

                            String   roomNm = tdList.get(0).text().trim();
                            String   linkURL = trList.get(i).attr("onclick");
                            String   tot     = tdList.get(1).text().trim();
                            String   left    = tdList.get(3).text().trim();

                            if(linkURL != "" && tot != null && left != null){
                                //Log.d("TEST", linkURL);
                                result.put("data_"+realIndex, roomNm + "///" + left + "///" + tot + "///" + linkURL);
                                result.put("count", (realIndex+1)+ "");
                                realIndex++;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            adapter.clearData(); //초기화
            String count = stringStringMap.get("count");

            Integer nCnt = Integer.valueOf(count);

            if(nCnt != null) {
                for(int i=0; i<nCnt; i++){
                    String data = stringStringMap.get("data_"+i);
                    String[] dataStr = data.split("///");

                    adapter.addItem(dataStr[0], dataStr[1], dataStr[2], dataStr[3]);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
