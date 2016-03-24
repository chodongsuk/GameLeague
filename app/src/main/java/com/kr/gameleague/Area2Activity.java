package com.kr.gameleague;

import java.util.ArrayList;

import kr.ds.data.AreaData;
import kr.ds.data.BaseData;
import kr.ds.data.BaseResultListener;
import kr.ds.handler.AreaHandler;
import kr.ds.utils.ProcessManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by Administrator on 2016-03-10.
 */
public class Area2Activity extends BaseActivity {
    private ArrayList<AreaHandler> mData;
    private BaseData mAreaData;
    private GridView mGridView;
    private AreaGridAdapter mAreaGridAdapter;
    private String mAreaCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area);
        Intent intent = getIntent();
        mAreaCode = intent.getStringExtra("area_code");

        mGridView = (GridView)findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("area_code",mData.get(position).getCode());
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(QuickstartPreferences.AREA_CODE, mData.get(position).getCode()).apply();
                setResult(RESULT_OK, intent);
                finish();
                ProcessManager.getInstance().allEndActivity();
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
        }).setParam("?setp=2&code="+mAreaCode).getView();



    }

}
