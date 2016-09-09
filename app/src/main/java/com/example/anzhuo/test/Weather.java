package com.example.anzhuo.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class Weather extends AppCompatActivity {
    EditText et_city;
    Button bt_cx;
    TextView tv_content;


    StringBuffer stringBuffer;
    private static final int MSG = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG:
                    try {
                        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        JSONObject jsonObject3 = jsonObject2.getJSONObject("realtime");
                        String cityname = "城市：" + jsonObject3.getString("city_name");
                        String time = "更新时间：" + jsonObject3.getString("time");
                        String week = "星期" + jsonObject3.getString("week");
                        String moon = "农历" + jsonObject3.getString("moon");
                        JSONObject jsonObject4 = jsonObject3.getJSONObject("weather");
                        String temp = "温度" + jsonObject4.getString("temperature") + "℃";
                        String info = jsonObject4.getString("info");
                        tv_content.setText(cityname + "\n" + time + "\n" + week + "\n" + moon + "\n" + temp + "\n" + info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        et_city = (EditText) findViewById(R.id.et_city);
        bt_cx = (Button) findViewById(R.id.bt_cx);
        tv_content = (TextView) findViewById(R.id.tv_content);
        bt_cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThread();
            }
        });
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Weather.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setThread() {
        new Thread() {
            @Override
            public void run() {
                String url = "";
                try {
                    String city = et_city.getText().toString();
                    url = "http://op.juhe.cn/onebox/weather/query?cityname=" + URLEncoder.encode(city, "utf-8") + "&dtype=json&key=3b7922fa1cfc3b3e7904bc594770fe60";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                netWork(url);

            }
        }.start();


    }

    private void netWork(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            InputStream i = new BufferedInputStream(c.getInputStream());
            stringBuffer = new StringBuffer();
            byte[] bytes = new byte[4 * 1024];
            int len;
            while ((len = i.read(bytes)) != -1) {
                stringBuffer.append(new String(bytes, 0, len));
            }
            handler.sendEmptyMessage(MSG);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
