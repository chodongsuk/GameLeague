package kr.ds.utils;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

/**
 * 디버그 유틸
 * 
 * @author chodong
 * @since 2015.08.27
 * 
 */
public class DsDebugUtils {
	private static DsDebugUtils debugUtils = null;
	private Context mContext = null;
    public DsDebugUtils(Context context){
    	mContext = context;
    }
    public static DsDebugUtils getInstance(Context context){
        if(debugUtils == null){
            synchronized (DsDebugUtils.class){
                if(debugUtils == null){
                	debugUtils = new DsDebugUtils(context.getApplicationContext());
                }
            }
        }
        return debugUtils;
    }
	public void Message(String str) {
		Log.d("TAG", "[" + new Throwable().getStackTrace()[0].getFileName()
				+ "][" + new Throwable().getStackTrace()[0].getMethodName()
				+ "][" + new Throwable().getStackTrace()[0].getLineNumber()
				+ "] : " + str);
	}
}
