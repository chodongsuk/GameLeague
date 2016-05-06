package com.kr.gameleague;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import kr.ds.data.AreaData;
import kr.ds.data.BaseData;
import kr.ds.data.BaseResultListener;
import kr.ds.handler.AreaHandler;
import kr.ds.utils.ProcessManager;
import kr.ds.widget.ScrollGridView;

/**
 * Created by Administrator on 2016-03-10.
 */
public class Area1Activity extends BaseActivity {
    private ArrayList<AreaHandler> mData;
    private BaseData mAreaData;
    private ScrollGridView mGridView;
    private AreaGridAdapter mAreaGridAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area);

        //지역 선택 최초실행
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean(QuickstartPreferences.AREA_CODE_CHECK, true).apply();

        ProcessManager.getInstance().addActivity(Area1Activity.this);

        mGridView = (ScrollGridView)findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Area2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("area_code",mData.get(position).getCode());
                startActivity(intent);
            }
        });
        mAreaData = new AreaData(getApplicationContext());
        mAreaData.clear().setCallBack(new BaseResultListener() {
            @Override
            public <T> void OnComplete() {

            }

            @Override
            public <T> void OnComplete(ArrayList<T> data) {
                mData = (ArrayList<AreaHandler>) data;
                if (mData != null) {
                    mAreaGridAdapter = new AreaGridAdapter(getApplicationContext(), mData);
                    mGridView.setAdapter(mAreaGridAdapter);
                }
            }

            @Override
            public void OnError(String str) {

            }
        }).setParam("?setp=1").getView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
    }
}
