package kr.ds.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 네트워크 유틸 클래스
 * @author chodong
 * @since 2015.08.27
 *
 */
public class DsNetworkUtils {
	private static DsNetworkUtils networkUtils = null;
	private Context mContext = null;
    public DsNetworkUtils(Context context){
    	mContext = context;
    }
	public static DsNetworkUtils getInstance(Context context){
		if(networkUtils == null){
			synchronized (DsNetworkUtils.class) {
				if(networkUtils == null){
					networkUtils = new DsNetworkUtils(context.getApplicationContext());
				}
			}
		}
		return networkUtils;
	}
	public boolean isOnline() 
	{
		boolean datalive = true;
		try{
			ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = connec
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
					|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING) {
				datalive = true; 
			}
			if (mobile.isConnected() || mobile.isConnectedOrConnecting()) {
				datalive = true;
			} else if (wifi.isConnected() || wifi.isConnectedOrConnecting())
			{
				datalive = true;
			}
			else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
					|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
				datalive = false;
			}
		}catch(Exception e){
		}
		return datalive;
	}
	
	

}
