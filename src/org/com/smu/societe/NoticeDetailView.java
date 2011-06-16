package org.com.smu.societe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 공지사항 액티비티
 * 
 * @Project Societe
 * @File 	NoticeDetailView.java
 * @Date 	2011-05-01 
 * @author  ACE
 */
public class NoticeDetailView extends Activity {
	private WebView webView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.notice_web);
        
        Intent intent = getIntent();
        String url = intent.getStringExtra("NOTICEURL");    
        
        webView = (WebView)findViewById(R.id.NoticeWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        
        final Activity activity = this;   
        webView.setWebChromeClient(new WebChromeClient() {   
	    	public void onProgressChanged(WebView view, int progress) {   
	    	   activity.setProgress(progress * 100);   
	    	}    
	    });      
        
        webView.loadUrl(url);  
        
    }
    
    @Override
	public void onBackPressed(){	
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
	}
    
}