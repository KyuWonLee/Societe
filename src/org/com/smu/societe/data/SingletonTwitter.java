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
	private static SingletonBase singleInstance;	// �ڽ� ��ü�� static���� �����
													// �ϳ��� �����ǵ��� ��
	public ArrayList<Marker> markers = new ArrayList<Marker>();
	
	/**
	 * new �����ڸ� ���� �ν��Ͻ��� ������ �� ������ ��!
	 */
	private SingletonTwitter() {
		super.createInstance();
	}
	
	/**
	 * �ν��Ͻ� �����Լ���, ���� �ν��Ͻ��� �������� ���� ���¶��
	 * �ν��Ͻ��� �����ϰ�, �����ͺ��̽����� ������ �����´�.
	 * ��������� ������ ���� �ν��Ͻ��� �����Ѵ�.
	 * 
	 * @return	JsonInstance	
	 */
	public static SingletonBase createInstance(){
		if(singleInstance == null){
			singleInstance = new SingletonTwitter();
			
			// ���� DB�κ��� �����͸� �������� �Լ�
			getDataFromSearchTweet();	
		}
		return singleInstance;		
	}
	
	/**
	 * 
	 * @return boolean ������ �ε� �Ϸ� ����
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
					
					/* Marker ���� */
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