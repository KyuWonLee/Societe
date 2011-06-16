package org.com.smu.societe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonBusDB;
import org.com.smu.societe.data.SingletonFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * Main Screen
 * 
 * @Project  	Societe
 * @File  		MainSelectView.java
 * @Date  		2011-04-12  
 * @author 		ACE
 */
public class MainSelectView extends Activity{
	private FrameLayout progressFrame;
	private ImageButton arViewBtn;
	private ImageButton libraryViewBtn;
	private ImageButton twitterViewBtn;
	private ImageButton busViewBtn;
	private ImageButton noticeViewBtn;
	private ImageButton couponViewBtn;
	private ImageButton scheduleViewBtn;
	private ImageButton restaurantViewBtn;
	
	public SingletonBase jLayer;
	public SingletonBusDB busDB;
	public SingletonBase twitterData;
	
	public static Bitmap bit, greenBit;
	
	public static String viewType;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		
		/* Copy files from Assets to sdcard */
		fileCopyToSDcard();	
		
		/* Create marker image */
		bit = BitmapFactory.decodeResource(getResources(), R.drawable.bus_marker);
		greenBit = BitmapFactory.decodeResource(getResources(), R.drawable.bus_marker_green);
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		
		
		/* Inflate ProgressBar */
		progressFrame = (FrameLayout)inflater.inflate(R.layout.progress_frame, null);
		progressFrame.setVisibility(ProgressBar.GONE);
		
		addContentView(progressFrame, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
		
		//ARView Button
		arViewBtn = (ImageButton)findViewById(R.id.IBcampus);
		arViewBtn.setBackgroundResource(R.drawable.arview_clicked);
		
		//Coupon Button
		couponViewBtn = (ImageButton)findViewById(R.id.IBcoupon);
		couponViewBtn.setBackgroundResource(R.drawable.coupon_clicked);	
		
		//Twitter Button
		twitterViewBtn = (ImageButton)findViewById(R.id.IBtwitter);
		twitterViewBtn.setBackgroundResource(R.drawable.twitter_clicked);
		
		//Library Button
		libraryViewBtn = (ImageButton)findViewById(R.id.IBlib);
		libraryViewBtn.setBackgroundResource(R.drawable.library_clicked);
		
		//Bus Button
		busViewBtn = (ImageButton)findViewById(R.id.IBbus);
		busViewBtn.setBackgroundResource(R.drawable.bus_clicked);
		
		//Restaurant Button
		restaurantViewBtn = (ImageButton)findViewById(R.id.IBrest);
		restaurantViewBtn.setBackgroundResource(R.drawable.restaurant_clicked);
		
		//Schedule Button
		scheduleViewBtn = (ImageButton)findViewById(R.id.IBschedule);
		scheduleViewBtn.setBackgroundResource(R.drawable.schedule_clicked);
		
		//Notice Button
		noticeViewBtn = (ImageButton)findViewById(R.id.IBinfo);
		noticeViewBtn.setBackgroundResource(R.drawable.notice_clicked);
		
		/* ARView Button */
		arViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				progressFrame.setVisibility(ProgressBar.VISIBLE);
				
				/* Data download */
				//jLayer = SingletonDB.createInstance();
				jLayer = SingletonFactory.create("ARView");
				
				/* Downloading Check Thread */
				buidlDownloadCheckThread();
				
			}
		});

		/* Library Button */
		libraryViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainSelectView.this, SMULibraryView.class);
				startActivity(intent);
				
				/* zoom animation */
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});

		/* Twitter Button */
		twitterViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				//Intent intent = new Intent(MainSelectView.this, TwitterView.class);
				//startActivity(intent);	
				
				/* zoom animation */
				//overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				progressFrame.setVisibility(ProgressBar.VISIBLE);
				
				twitterData = SingletonFactory.create("Twitter");
				
				searchTweetThread();		
				
			}
		});
		
		/* Bus Button */
		busViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				progressFrame.setVisibility(ProgressBar.VISIBLE);
				
				busDB = SingletonBusDB.createInstance();
				
				busDownloadCheckThread();
				
			}
		});
		
		/* Notice Button */
		noticeViewBtn.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainSelectView.this, SMUNoticeView.class);
				startActivity(intent);
				
				/* zoom animation */
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});	
		
	}
	
	/**
	 * Assets 폴더에 있는 이미지들을 sdcard 로 복사
	 */
	public void fileCopyToSDcard(){
		
		String[] imageName = {"A.png", "D.png", "E.png", "F.png", "G.png", "H.png",
							  "I.png", "J.png", "K.png", "L.png", "M.png", "N.png", 
							  "O.png", "S.png", "T.png", "U.png", "V.png", "W.png"};
	
		addFilesToSdcard(Environment.getExternalStorageDirectory().getPath() +"/ARview",
						"buildicon/", imageName);
		
		String[] buildImage = {"AA.jpg","DD.jpg", "FF.jpg", "GG.jpg", "HH.jpg", "II.jpg",
					"JJ.jpg", "KK.jpg", "MM.jpg", "NN.jpg", "SS.jpg", "TT.jpg", "UU.jpg"};
		
		addFilesToSdcard(Environment.getExternalStorageDirectory().getPath() +"/ARview",
						"buildicon/", buildImage);
		
	}
	
	/**
	 * assets 
	 * 
	 * @param inputPath				
	 * @param outputPath
	 * @exception	IOException
	 */
	public void addFilesToSdcard(String inputPath, String outputPath, String[] args ){
	
		File dir = new File(inputPath);
		if(!dir.exists())
			dir.mkdir();
		      
		for(int i = 0; i < args.length; i++){
		    String finalInputPath = inputPath + File.separator + args[i];
		    String finalOutputPath = outputPath + args[i];
		    	  
		    File outfile = new File(finalInputPath);
				   
			AssetManager assetManager = getResources().getAssets();
			InputStream is;
			try {
				is = assetManager.open(finalOutputPath, AssetManager.ACCESS_BUFFER);
				
				long filesize = is.available();
				
				if(outfile.length() < filesize){
				   	byte[] tempdata = new byte[(int) filesize];
				   	is.read(tempdata);
				   	is.close();
				   	outfile.createNewFile();
				   	FileOutputStream fo = new FileOutputStream(outfile);
				   	fo.write(tempdata);
				   	fo.close();
				   	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 데이터 다운로드가 완료 되었는지 검사하는 쓰레드
	 */
	public void busDownloadCheckThread(){
		new Thread(){
			public void run(){
				while(true){
					if(busDB.isCompleteLoading()){
						dismissBusUIThread();	//UI쓰레드를 통해 UI를 핸들링
						break;
					}
				}	
			}
		}.start();
	}
	
	/**
	 * ProgressDialog를 dissmiss, 
	 * 증강현실 액티비티 전환해주는 UI Thread
	 */
	public void dismissBusUIThread(){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				progressFrame.setVisibility(View.GONE);
						
				Intent intent = new Intent(MainSelectView.this, MapOverlay.class);
				intent.putExtra("MAPTAG", "BusView");
				startActivity(intent);
				
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				
			}
		});
	}
	
	/**
	 * 데이터 다운로드가 완료 되었는지 검사하는 쓰레드
	 */
	public void buidlDownloadCheckThread(){
		new Thread(){
			public void run(){
				while(true){
					if(jLayer.isCompleteLoading()){
						dismissBuildUIThread();
						break;
					}
				}	
			}
		}.start();
	}
	
	/**
	 * ProgressDialog를 dissmiss시키고, 
	 * 증강현실 액티비티 전환해주는 UI Thread
	 */
	public void dismissBuildUIThread(){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				progressFrame.setVisibility(View.GONE);
				
				Intent intent = new Intent(MainSelectView.this, ARView.class);
				viewType = "ARView";
				startActivity(intent);
				
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				
			}
		});
	}
	
	/**
	 * 데이터 다운로드가 완료 되었는지 검사하는 쓰레드
	 */
	public void searchTweetThread(){
		new Thread(){
			public void run(){
				while(true){
					if(twitterData.isCompleteLoading()){
						dismissTwitterUIThread();
						break;
					}
				}	
			}
		}.start();
	}
	
	/**
	 * ProgressDialog를 dissmiss시키고, 
	 * 증강현실 액티비티 전환해주는 UI Thread
	 */
	public void dismissTwitterUIThread(){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				progressFrame.setVisibility(View.GONE);
				
				Intent intent = new Intent(MainSelectView.this, ARView.class);
				viewType = "Twitter";
				startActivity(intent);
				
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		new AlertDialog.Builder(this)
		.setMessage("Societe를 종료하시겠습니까?")
		.setIcon(R.drawable.miniteamlogo)
		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			/** 확인버튼 누른 경우 */
			public void onClick(DialogInterface dialog, int which) {
				int myprocessid = Process.myPid();
				Process.killProcess(myprocessid);	//Kill process
			}
		})
		.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			/** 취소버튼 누른 경우 */
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();			//Dismiss dialog
			}
		}).show();		 
	}
	
}