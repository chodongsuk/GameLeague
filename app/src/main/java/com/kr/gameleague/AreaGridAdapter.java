package com.kr.gameleague;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ds.handler.AreaHandler;
import kr.ds.utils.DsObjectUtils;

/**
 * Created by Administrator on 2016-03-10.
 */
public class AreaGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AreaHandler> mData;
    private View mView;
    private ListView mListView;
    private LayoutInflater mInflater;

    public AreaGridAdapter(Context context, ArrayList<AreaHandler> data){
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.area_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(!DsObjectUtils.getInstance(mContext).isEmpty(mData.get(position).getName())) {
            holder.textView.setText(mData.get(position).getName());
        }
        return convertView;
    }
    class ViewHolder{
        private TextView textView;
    }

}
