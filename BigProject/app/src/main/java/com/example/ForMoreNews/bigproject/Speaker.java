package com.example.ForMoreNews.bigproject;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by liminyan on 06/09/2017.
 * 只是一个全局功能类
 * 通过setText 设置朗读的内容
 * setcur 设置朗读的界面
 * 调用start 进行朗读 朗读失败会反回"不支持当前语言"
 * 语言当前默认为中文
 */

public class Speaker {

    private String text = null;
    private AppCompatActivity cur;
    private TextToSpeech mTextToSpeech;

    static Speaker now = null;

    public void setText(String text) {
        this.text = text;
    }

    public void setCur(AppCompatActivity cur) {
        this.cur = cur;
    }

    public void start()
    {
        mTextToSpeech=new TextToSpeech(cur, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS) {
                    //设置朗读语言
                    int supported=mTextToSpeech.setLanguage(Locale.CHINESE);

                    if ((supported!=TextToSpeech.LANG_AVAILABLE)&&(supported!= TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(cur, "不支持当前语言！", 1).show();
                    }

                    mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            }

        });
    }

    public void stop() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    public static Speaker getInstance() {

        if (now == null)
            now = new Speaker();
        return now;
    }

}