package com.kr.gameleague;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.buzzvil.buzzad.sdk.BuzzAd;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.UserProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import kr.ds.data.BaseResultListener;
import kr.ds.data.GcmData;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static final String BASE_URL = "http://saving1004.com";
    public static final int FILECHOOSER_RESULTCODE = 2;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int AREA_RESULTCODE = 3;
    private WebView mWebView;
    private ProgressBar pb;
    private MyWebChromeClient mChromeClient;
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BuzzScreen.getInstance().launch();


        setContentView(R.layout.activity_main);

        initViews();
        hideWebView();
        hideProgress();
        configureOfflineWebView(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.getBoolean(QuickstartPreferences.AREA_CODE_CHECK, false) == false){//최초실행이 없는 경우만 실행.
            Intent intent = new Intent(this, Area1Activity.class);
            startActivityForResult(intent, AREA_RESULTCODE);
        }
        if(sharedPreferences.getBoolean(QuickstartPreferences.INTRO_CHECK, false) == false){
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.i(TAG, "Registration Token sucessful " );
                } else {
                    Log.i(TAG, "Registration Token failed " );
                }
            }
        };
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private void initViews() {
        pb = (ProgressBar) findViewById(R.id.progress_bar);
        mWebView = (WebView) findViewById(R.id.webview);
    }

    private void configureOfflineWebView(Bundle savedInstanceState) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);

// Set cache size to 8 mb by default. should be more than enough
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);

        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        String userAgent = mWebView.getSettings().getUserAgentString();
        webSettings.setUserAgentString(userAgent+ "ds_android");

        mChromeClient = new MyWebChromeClient(this);
        mWebView.setWebChromeClient(mChromeClient);
        mWebView.addJavascriptInterface(new AndroidBridge(), "android");

        mWebView.setWebViewClient(new OfflineWebViewClient(this));

        if ( !Utils.isNetworkAvailable(this) ) { // loading offline
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK );
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }

        if (savedInstanceState == null) {
            loadURL();
        }
    }
    private class AndroidBridge {
        @JavascriptInterface
        public void buzzad(final String m_no) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d(TAG, m_no+"");
                    BuzzAd.init("223485077489935", getApplicationContext());
                    BuzzAd.showOfferWall(MainActivity.this, getResources().getString(R.string.app_name)+" 포인트 적립", m_no);

                }
            });
        }

        @JavascriptInterface
        public void login(final String m_no) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d(TAG, m_no + "");
                    Log.d("TEST", "login");
                    sharedPreferences.edit().putString(QuickstartPreferences.MNO, m_no);

                    HashMap<String, String> mHashMap = new HashMap<String, String>();
                    mHashMap.put("m_no", m_no);
                    mHashMap.put("reg_id", sharedPreferences.getString(QuickstartPreferences.TOKEN , ""));
                    mHashMap.put("type", "login");
                    if(mHashMap != null){
                        new GcmData(getApplicationContext()).clear().setCallBack(new BaseResultListener() {

                            @Override
                            public void OnError(String str) {
                                // TODO Auto-generated method stub
                            }
                            @Override
                            public <T> void OnComplete() {
                                // TODO Auto-generated method stub

                                UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
                                // 포인트 적립을 위해서는 setUserId를 반드시 호출해야 함
                                userProfile.setUserId(m_no);
//                    userProfile.setBirthYear(1985);
//                    userProfile.setGender(UserProfile.USER_GENDER_MALE);
//                    userProfile.setRegion("서울특별시 관악구");
                                // 버즈스크린 활성화
                                BuzzScreen.getInstance().activate();

                            }
                            @Override
                            public <T> void OnComplete(ArrayList<T> data) {

                            }


                        }).getViewPost(mHashMap);
                    }


                }
            });
        }
        @JavascriptInterface
        public void loginout() { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("TEST", "loginout");
                    sharedPreferences.edit().putString(QuickstartPreferences.MNO, "");

                    HashMap<String, String> mHashMap = new HashMap<String, String>();
                    mHashMap.put("reg_id", sharedPreferences.getString(QuickstartPreferences.TOKEN , ""));
                    mHashMap.put("type", "logout");
                    if(mHashMap != null) {
                        new GcmData(getApplicationContext()).clear().setCallBack(new BaseResultListener() {

                            @Override
                            public void OnError(String str) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public <T> void OnComplete() {
                                // TODO Auto-generated method stub
                                BuzzScreen.getInstance().deactivate();

                            }

                            @Override
                            public <T> void OnComplete(ArrayList<T> data) {

                            }


                        }).getViewPost(mHashMap);
                    }
                }
            });
        }

        @JavascriptInterface
        public void buzzscreenon(final String m_no) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("TEST", "buzzscreenon");

                    HashMap<String, String> mHashMap = new HashMap<String, String>();
                    mHashMap.put("reg_id", sharedPreferences.getString(QuickstartPreferences.TOKEN , ""));
                    mHashMap.put("m_no", m_no);
                    mHashMap.put("type", "buzzscreen_on");
                    if(mHashMap != null) {
                        new GcmData(getApplicationContext()).clear().setCallBack(new BaseResultListener() {

                            @Override
                            public void OnError(String str) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public <T> void OnComplete() {
                                // TODO Auto-generated method stub

                                // 버즈스크린 활성화
                                UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
                                // 포인트 적립을 위해서는 setUserId를 반드시 호출해야 함
                                userProfile.setUserId(m_no);
//                    userProfile.setBirthYear(1985);
//                    userProfile.setGender(UserProfile.USER_GENDER_MALE);
//                    userProfile.setRegion("서울특별시 관악구");
                                // 버즈스크린 활성화
                                BuzzScreen.getInstance().activate();

                            }

                            @Override
                            public <T> void OnComplete(ArrayList<T> data) {

                            }


                        }).getViewPost(mHashMap);
                    }





                }
            });
        }

        @JavascriptInterface
        public void buzzscreenoff(final String m_no) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("TEST", "buzzscreenoff");
                    HashMap<String, String> mHashMap = new HashMap<String, String>();
                    mHashMap.put("reg_id", sharedPreferences.getString(QuickstartPreferences.TOKEN , ""));
                    mHashMap.put("m_no", m_no);
                    mHashMap.put("type", "buzzscreen_off");
                    if(mHashMap != null) {
                        new GcmData(getApplicationContext()).clear().setCallBack(new BaseResultListener() {

                            @Override
                            public void OnError(String str) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public <T> void OnComplete() {
                                // TODO Auto-generated method stub
                                BuzzScreen.getInstance().deactivate();

                            }

                            @Override
                            public <T> void OnComplete(ArrayList<T> data) {

                            }


                        }).getViewPost(mHashMap);
                    }
                }
            });
        }
    }


    private void loadURL() {
        String strPush;
        Intent i = getIntent();
        if (i != null) {
            Bundle extra = i.getExtras();
            if (extra != null && extra.getString("url") != null) {
                strPush = extra.getString("url");

                mWebView.loadUrl(strPush);
            } else {
                mWebView.loadUrl(BASE_URL);
            }
        }else {
            mWebView.loadUrl(BASE_URL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mChromeClient.getUploadMessage() == null) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mChromeClient.getUploadMessage().onReceiveValue(result);
            mChromeClient.setUploadMessage(null);
        }else if(requestCode == INPUT_FILE_REQUEST_CODE ) {
            if (mChromeClient.mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, intent);
                return;
            }
            Uri[] results = null;
            // Check that the response is a  good one
            if(resultCode == RESULT_OK) {
                        if(intent == null) {
                            // If there is not data, then we may have taken a photo
                            if(mChromeClient.mCameraPhotoPath != null) {
                                results = new Uri[]{Uri.parse(mChromeClient.mCameraPhotoPath)};
                            }
                        } else {
                            String dataString = intent.getDataString();
                            if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mChromeClient.mFilePathCallback.onReceiveValue(results);
            mChromeClient.mFilePathCallback = null;
            return;
        }else if(requestCode == AREA_RESULTCODE){
            if(resultCode == RESULT_OK || requestCode == RESULT_CANCELED) {
                if (checkPlayServices() && sharedPreferences.getString(QuickstartPreferences.TOKEN,"").matches("")) { //토큰이 없는경우..
                    // Start IntentService to register this application with GCM.
                    intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
                }
            }

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of the WebView
        mWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }else {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("게임리그")
                    .setMessage("어플을 종료 하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("아니오", null)
                    .show();
        }
    }
    public void showProgress(){
        pb.setVisibility(View.VISIBLE);
    }

    public void hideProgress(){
        pb.setVisibility(View.GONE);
    }

    public void showWebView(){
        mWebView.setVisibility(View.VISIBLE);
    }

    public void hideWebView(){
        mWebView.setVisibility(View.GONE);
    }

}
