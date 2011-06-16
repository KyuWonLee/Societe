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
 * ������ �����ǿ� ���� �¼� �̹��� ��Ȳ�� �����ִ� ��Ƽ��Ƽ
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
		
		/* status bar �� Progress Bar ǥ�� */
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
	    
		if(NetworkState.isNetworkConnected(conManger)){	//��Ʈ��ũ ���Ῡ�� Ȯ��
        	// �񵿱� ������ ����
            new DownloadAsynctask().execute(URL);
            
        } else {
        	Toast.makeText(this, "��Ʈ��ũ�� ������� �ʾҽ��ϴ�.",
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
	 * ���� web ���� �¼������� �������� 
	 * �񵿱� ������ Ŭ����
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
        	//���α׷����� �����
			//mProgress.dismiss();	
		}	
		
		@Override 
		protected void onCancelled(){
			mProgress.dismiss();
		}
		
	}
	
	/**
	 * �־��� URL�� HTML�Ľ��Ͽ� �̹��� ��θ� ����
	 * 
	 * @param sourceUrlString			�Ľ��� URL�ּ�
	 * @return imgPath 					����� �̹��� ���
	 * @exception MalformedURLException	�߸��� ������ URL�� ��� �߻�
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
		
		/* �̹��� ���ҽ� ��� ���� ���� */
		Pattern imgTagPattern = Pattern.compile("(?i)<iframe(\\s+[a-zA-Z0-9_]*=[^>]*)*(\\s)*(/)?>");
		Matcher match = imgTagPattern.matcher(renderedText);
		
		String temp = null;
		
		if(match.find()){	//�̹��� ��θ� ã���� ���
			temp = match.group();
			String[] ss = temp.split(" ");
			temp = ss[ss.length-1].replaceAll("src=|\"|'|>", "");
			imgPath = temp.replaceFirst("\\.", "");	//���� �̹��� ���
			imgPath = imgPath.replaceAll(" ", "");
		}
		
		try {
			//�ѱ� ���ڵ�		
			renderedText = new String(renderedText.getBytes(
								source.getEncoding()), "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}							
		
		return imgPath;
		
	}
}