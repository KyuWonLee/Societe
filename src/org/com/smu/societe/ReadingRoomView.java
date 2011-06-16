package org.com.smu.societe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Source;

import org.com.smu.societe.networkstate.NetworkState;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * 
 * 선택한 열람실에 대한 좌석 이미지 현황을 보여주는 액티비티
 * 
 * @Project Societe
 * @File 	ReadingRoomView.java
 * @Date 	2011-05-01
 * @author  ACE
 */
public class ReadingRoomView extends Activity{
	private ProgressDialog mProgress;
	private WebView web;
	private String baseUrl = "http://61.72.129.163";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		

		requestWindowFeature(Window.FEATURE_PROGRESS);
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.room_image);
		
		Intent intent = getIntent();
		String URL = intent.getStringExtra("SELECTEDURL");
		
		web = (WebView)findViewById(R.id.roomImg);
		WebSettings set = web.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);
		
		/* status bar 에 Progress Bar 표시 */
		final Activity activity = this;   
		web.setWebChromeClient(new WebChromeClient() {   
	    	public void onProgressChanged(WebView view, int progress) {   
	    	   activity.setProgress(progress * 100);   
	    	}    
	    });   
		web.setWebViewClient(new WebViewClient() {   
	    	public void onReceivedError(WebView view, int errorCode, String description, String fallingUrl) {   
	    	   Toast.makeText(activity, "Loading error: "+description, Toast.LENGTH_SHORT).show();   
	    	}   
	    });
		
		ConnectivityManager conManger = 
				(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    
		if(NetworkState.isNetworkConnected(conManger)){	//네트워크 연결여부 확인
        	// 비동기 쓰레드 실행
            new DownloadAsynctask().execute(URL);
            
        } else {
        	Toast.makeText(this, "네트워크가 연결되지 않았습니다.",
        			Toast.LENGTH_LONG).show();
        }  
		
	}
	
	@Override
	public void onBackPressed(){
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
	}
	
	/**
	 * AsyncTask class
	 * 상명대 web 에서 좌석데이터 가져오는 
	 * 비동기 쓰레드 클래스
	 * 
	 * @author YMH
	 *
	 */
	class DownloadAsynctask extends AsyncTask<String, Integer, String>{
		
		@Override
		protected void onPreExecute(){
			/*
			mProgress = new ProgressDialog(ReadingRoomView.this);
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setMessage("Updating, please wait...");
			mProgress.setProgress(0);
			mProgress.setCancelable(false);
			mProgress.show();
			*/
			setProgressBarIndeterminateVisibility(true);
		}	

		@Override
		protected String doInBackground(String... params) {
			
			String result = getImgUrlPath(params[0]);
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result){
		
			web.loadUrl(baseUrl+result);
			
			setProgressBarIndeterminateVisibility(false);
        	//프로그래스바 사라짐
			//mProgress.dismiss();	
		}	
		
		@Override 
		protected void onCancelled(){
			mProgress.dismiss();
		}
		
	}
	
	/**
	 * 주어진 URL을 HTML파싱하여 이미지 경로를 얻음
	 * 
	 * @param sourceUrlString			파싱할 URL주소
	 * @return imgPath 					추출된 이미지 경로
	 * @exception MalformedURLException	잘못된 형식의 URL일 경우 발생
	 * 
	 */
	private String getImgUrlPath(String sourceUrlString){
		String imgPath = null;
		
		if (sourceUrlString.indexOf(':') == -1){
			sourceUrlString = "file: " + sourceUrlString;
		}
		
		Source source = null;
		
		try {			
			source = new Source(new URL(sourceUrlString));
					
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
		String renderedText = source.toString();
		
		/* 이미지 리소스 경로 패턴 설정 */
		Pattern imgTagPattern = Pattern.compile("(?i)<iframe(\\s+[a-zA-Z0-9_]*=[^>]*)*(\\s)*(/)?>");
		Matcher match = imgTagPattern.matcher(renderedText);
		
		String temp = null;
		
		if(match.find()){	//이미지 경로를 찾았을 경우
			temp = match.group();
			String[] ss = temp.split(" ");
			temp = ss[ss.length-1].replaceAll("src=|\"|'|>", "");
			imgPath = temp.replaceFirst("\\.", "");	//최종 이미지 경로
			imgPath = imgPath.replaceAll(" ", "");
		}
		
		try {
			//한글 인코딩		
			renderedText = new String(renderedText.getBytes(
								source.getEncoding()), "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}							
		
		return imgPath;
		
	}
}