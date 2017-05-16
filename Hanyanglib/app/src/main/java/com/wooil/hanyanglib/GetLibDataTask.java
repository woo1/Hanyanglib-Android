package com.wooil.hanyanglib;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wijang on 2017. 5. 3..
 */
public class GetLibDataTask extends AsyncTask<Void, Void, Map<String,String>> {

    @Override
    protected Map<String, String> doInBackground(Void... params) {
        Map<String,String> result = new HashMap<String, String>();
        try {
            Document document = Jsoup.connect("http://libgate.hanyang.ac.kr/seats/domian5.asp").get();
            Elements elements = document.select("table");
            Element tableData = elements.get(1);
            Elements trList   = tableData.select("tr");

            for( int i = 0; i < trList.size(); i++ ) {
                if(i > 2) {
                    Elements tdList = trList.get(i).select("td");

                    String   roomNm = tdList.get(1).select("font").get(0).text().trim();
                    String   linkURL = tdList.get(1).select("font").select("a").get(0).attr("href");
                    String   tot     = tdList.get(2).select("font").get(0).text().trim();
                    String   left    = tdList.get(4).select("font").get(0).text().trim();

                    if(roomNm != null && linkURL != null && tot != null && left != null){
                        Log.d("ResultList", roomNm+"_"+linkURL+"_"+tot+"_"+left);
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
//        tvLatestLotto.setText(map.get("latestLottoCount") + " 회 당첨번호");
//        tvWinGameCount.setText("총 " + map.get("winGameCount") + " 게임 당첨");
//        tvWinGameMoney.setText("1등 : " + map.get("winGameMoney") + " 원");

        for( int i = 1; i < 8; i++) {
//            GetImageTask task1 = new GetImageTask();
//            task1.execute(map.get("number" + i), "number" + i);
        }
    }
}
