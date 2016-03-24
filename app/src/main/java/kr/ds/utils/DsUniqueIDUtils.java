package kr.ds.utils;

import java.util.UUID;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 고유 id
 * @author chodong
 * @since 2015.08.27
 *
 */
public class DsUniqueIDUtils {
	
	private static DsUniqueIDUtils uniqueIDUtils = null;
	private Context mContext = null;
    public DsUniqueIDUtils(Context context){
    	mContext = context;
    }
    public static DsUniqueIDUtils getInstance(Context context){
        if(uniqueIDUtils == null){
            synchronized (DsUniqueIDUtils.class){
                if(uniqueIDUtils == null){
                	uniqueIDUtils = new DsUniqueIDUtils(context.getApplicationContext());
                }
            }
        }
        return uniqueIDUtils;
    }
	public String getUniqueID() {
		final TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, tmPhone, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						mContext.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		return deviceId;
	} 

}
