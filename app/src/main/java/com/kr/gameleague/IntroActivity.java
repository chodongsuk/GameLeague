package com.kr.gameleague;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import kr.ds.utils.ProcessManager;

/**
 * Created by Administrator on 2016-05-06.
 */
public class IntroActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTextViewFinish;
    private SharedPreferences sharedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.intro);
        (mTextViewFinish = (TextView) findViewById(R.id.textView_finish)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_finish:
                sharedPreferences.edit().putBoolean(QuickstartPreferences.INTRO_CHECK, true).apply();
                finish();
                break;
        }
    }
}
