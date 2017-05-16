package com.wooil.hanyanglib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by wijang on 2017. 5. 4..
 */
public class PopupActivity extends Activity {
    String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        //UI 객체 생성
        TextView text01 = (TextView)findViewById(R.id.popup_text1);
        TextView text02 = (TextView)findViewById(R.id.popup_text2);

        //데이터 가져오기
        Intent intent = getIntent();
        String data1   = intent.getStringExtra("data1");
        String data2   = intent.getStringExtra("data2");
        tag            = intent.getStringExtra("tag");

        text01.setText(data1);
        text02.setText(data2);

        text01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //액티비티 닫기
                Intent intent = new Intent();
                intent.putExtra("indexNo", "0");
                intent.putExtra("tag", tag);
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        text02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //액티비티 닫기
                Intent intent = new Intent();
                intent.putExtra("indexNo", "1");
                intent.putExtra("tag", tag);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥 레이어 클릭 시 안 닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    //백버튼 막기
    @Override
    public void onBackPressed() {
        return;
    }
}
