package org.com.smu.societe;

import org.com.smu.societe.data.TwitAccTokenDB;
import org.com.smu.societe.networkstate.NetworkState;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 
 * @Project  Campus AR 
 * @File  TwitterView.java
 * @Date  2010-10-10
 * @author ACE
 *
 */
public class TwitterView extends Activity {
	public static String CONSUMERKEY = "UmKw5MpsSlSXAAbgQiYgjw";
	public static String CONSUMERSECRET = "KEKslfMa8E0sd9o7o60WXqEwLfzDtYtqQgGPFzwebY";  
	
	private static Twitter twitter;
	private RequestToken requestToken;

	private WebView webview;
	private EditText editText;
	private Button button;
	
	private TwitAccTokenDB mDB;
	private static SQLiteDatabase db;
	private AccessToken accessToken;
	
	private ConnectivityManager manager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	getWindow().requestFeature(Window.FEATURE_PROGRESS);
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	
    	setContentView(R.layout.twitter);
    	
    	mDB = TwitAccTokenDB.getDBInstance(this);
    	
    	webview = (WebView)findViewById(R.id.webview);
    	webview.clearCache(true);
    	webview.getSettings().setJavaScriptEnabled(true); 
    	
    	editText = (EditText)findViewById(R.id.edit);
    	button = (Button)findViewById(R.id.button);
    	button.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				setAccessToken(requestToken);
			}
		});
    	
	    final Activity activity = this;   
	    webview.setWebChromeClient(new WebChromeClient() {   
	    	public void onProgressChanged(WebView view, int progress) {   
	    		activity.setProgress(progress * 100);   
	    	}    
	    });   
	    webview.setWebViewClient(new WebViewClient() {
	    	public void onPageFinished(WebView view, String url){
	    		super.onPageFinished(view, url);
	          
	    		view.loadUrl("javas-ript:window.PINCODE.getPinCode(document.getElementById('code').innerHTML);"); // 이글루 편집기의 이상으로 오타가 나고 있습니다. javas-ript 가 아니라 자바스크립트(영어로 쓰셔야되요ㅠㅠ) 입니다. 
	        }

	    	public void onReceivedError(WebView view, int errorCode, String description, String fallingUrl) {   
	    		Toast.makeText(activity, "Loading error: "+description, Toast.LENGTH_SHORT).show();   
	    	}   
	    });	
    	
	    manager = 
			(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    if(NetworkState.isNetworkConnected(manager)){
    		
        	requestToken = getRequestToken();
        	
        	directShowNextActivity();
        	
    	} else{
    		Toast.makeText(this, "연결 실패",
    				Toast.LENGTH_LONG).show();
    	}
    	
    }

    /**
     * 
     * 
     */
    public void directShowNextActivity(){
    	runOnUiThread(new Runnable(){

			public void run() {
		    	db = mDB.getReadableDatabase();
				Cursor cursor = db.query("dic", new String[]{"Token", "Secret"}, null
						, null, null, null, null);
		    	
				String Token = null;
				String Secret = null;
				
				while(cursor.moveToNext()){
					Token = cursor.getString(0);
					Secret = cursor.getString(1);
				}
				
				if(Token != null || Secret != null){

					twitter.setOAuthAccessToken(Token, Secret);		
					
					showDialog(0);

					new Handler().postDelayed(new Runnable(){
						public void run() {
							
							dismissDialog(0);
								
							//Start Timeline Activity
							Intent i = new Intent(TwitterView.this, TwitTimeline.class);
							startActivity(i);
							finish();
							
							/* zoom animation */
							overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
						}
					}, 2000);
				}
			}
    	});
    }
 
    /**
     * RequestToken 
     * Twitter OAuth 
     * 
     * @return	requestToken
     */
    public RequestToken getRequestToken(){
    	twitter = new TwitterFactory().getInstance();     
    	twitter.setOAuthConsumer(CONSUMERKEY, CONSUMERSECRET);
    	requestToken = null;
    	
    	try{
    		requestToken = twitter.getOAuthRequestToken(); 
    		//webview.loadUrl(requestToken.getAuthenticationURL());
    		webview.loadUrl(requestToken.getAuthorizationURL());
    	}catch(TwitterException te){
    		te.printStackTrace();
    	}
    	return requestToken;
    }
    
    /**
     * AccessToken
     * @param requestToken	AccessToken
     * @exception TwitterException
     */
    @SuppressWarnings("deprecation")
	public boolean setAccessToken(RequestToken requestToken){
    	String pin = editText.getText().toString();   	
    	
    	/* Twitter Access */
    	try{
    		accessToken = twitter.getOAuthAccessToken(requestToken, pin);
    		twitter.setOAuthAccessToken(accessToken.getToken(), 
    				accessToken.getTokenSecret());
    		
    		new AlertDialog.Builder(this)
    		.setMessage("로그인 정보를 저장하시겠습니까?")
    		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					db = mDB.getWritableDatabase();
					ContentValues row = new ContentValues();
					row.put("Token", accessToken.getToken());
					row.put("Secret", accessToken.getTokenSecret());
					db.insert("dic", null, row);
					db.close();
					
					//Timeline Activity
					Intent i = new Intent(TwitterView.this, TwitTimeline.class);
					startActivity(i);
					finish();
					
					/* zoom animation */
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			})
			.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					//Timeline Activity
					Intent i = new Intent(TwitterView.this, TwitTimeline.class);
					startActivity(i);
					finish();
					
					/* zoom animation */
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			}).show();
			
    		return true;
    		
    	}catch(TwitterException te){
    		Toast.makeText(TwitterView.this, "연결 실패", Toast.LENGTH_SHORT).show();
    		te.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * 
     * @return twitter 	Twitter instance
     */
    public static Twitter getTwitterInstance(){
    	return twitter;
    }
    
    /**
     * SQLiteDatabase instance
     * 
     * @return db 	SQLiteDatebase instance
     */
    public static SQLiteDatabase getDBInstance(){
    	return db;
    }
    
    @Override
    public Dialog onCreateDialog(int num){
    	switch(num){
    	case 0: 
    		ProgressDialog dialog = ProgressDialog.show(this, "", 
    								"Loging, please wait...", true);
    		return dialog;
    	}
		return null;
    	
    } 
    
    @Override
	public void onBackPressed(){
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
	}
    
    class JavaScriptInterface{
	    public void getPinCode(String pin){
	        if (pin.length() > 0){
	        	Toast.makeText(TwitterView.this, pin, Toast.LENGTH_LONG).show();
	        }
	        else{
	        }
	    }
    }
	
}