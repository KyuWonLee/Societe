package org.com.smu.societe.data;

import java.util.ArrayList;

import org.com.smu.societe.Marker;
import org.com.smu.societe.reality.RealLocation;
import org.com.smu.societe.twitter.SearchTweet;
import org.com.smu.societe.twitter.TweetInfo;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 
 * @Project  Societe
 * @File  SingletonTwitter.java
 * @Date  2011-05-21
 * @author ACE
 *
 */
public class SingletonTwitter extends SingletonBase{  
	private static SingletonBase singleInstance;	// 자신 객체를 static으로 만들어
													// 하나만 생성되도록 함
	public ArrayList<Marker> markers = new ArrayList<Marker>();
	
	/**
	 * new 연산자를 통해 인스턴스를 생성할 수 없도록 함!
	 */
	private SingletonTwitter() {
		super.createInstance();
	}
	
	/**
	 * 인스턴스 생성함수로, 현재 인스턴스를 생성하지 않은 상태라면
	 * 인스턴스를 생성하고, 데이터베이스에서 정보를 가져온다.
	 * 멤버변수로 선언한 단일 인스턴스를 리턴한다.
	 * 
	 * @return	JsonInstance	
	 */
	public static SingletonBase createInstance(){
		if(singleInstance == null){
			singleInstance = new SingletonTwitter();
			
			// 서버 DB로부터 데이터를 가져오는 함수
			getDataFromSearchTweet();	
		}
		return singleInstance;		
	}
	
	/**
	 * 
	 * @return boolean 데이터 로딩 완료 여부
	 */
	public boolean isCompleteLoading(){
		return isComplete; 
	}
	
	/**
	 * Twitter Search
	 */
	public static void getDataFromSearchTweet(){
		
		new Thread(){
			public void run(){
				SearchTweet searchTweet = new SearchTweet();
				ArrayList<TweetInfo> tweetList = searchTweet.searchTweets();
				
				Log.d("Tweet", tweetList.size() + "");
				
				for(int i=0; i < tweetList.size(); i++){
					TweetInfo tweets = (TweetInfo)tweetList.get(i);
					
					/* Marker 생성 */
					createMarker(i, tweets.getUserId(), tweets.getProfileImg(), tweets.getTweetGeoLocation().getLatitude(),
							tweets.getTweetGeoLocation().getLongitude(), 0, tweets.getText());
					
				}
				
				isComplete = true;
				
			}
		}.start();
		
	}
	
	private static void createMarker(int id, String user, Bitmap profile ,double latitude,
			double longitude, double elevation, String tweet) {
		
			RealLocation refpt = new RealLocation();
			Marker ma = new Marker();	
			
			if(profile != null){
				profile = Bitmap.createScaledBitmap(profile, profile.getWidth() + 30,
						profile.getHeight() + 30, false);		
			}
				
			ma.objectId = id;
			ma.mText = user;
			refpt.setLatitude(latitude);
			refpt.setLongitude(longitude);
			refpt.setAltitude(elevation);
			ma.mGeoLoc.setTo(refpt);
			ma.bit = profile;
			ma.tweet = tweet;
				
			singleInstance.markers.add(ma);	
		
	}
	
}