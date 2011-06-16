package org.com.smu.societe.twitter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

public class SearchTweet {
	private Twitter twitter;
	private ArrayList<TweetInfo> tweetArray;
	public static HashMap<String, Bitmap> imageCache;
	
	private double radius = 1000;
	
	public SearchTweet(){
		twitter = new TwitterFactory().getInstance();
		tweetArray = new ArrayList<TweetInfo>();
		imageCache = new LinkedHashMap<String, Bitmap>();
	}
	
	public ArrayList<TweetInfo> searchTweets(){
		try{
			
			Query query = new Query();	
			query.setGeoCode(getMyGeoLocation(), getRadius(), Query.KILOMETERS);	
			query.setQuery("#SMUGuide");		// ������� ���ϴ� �ܾ� ����(�ؽ��±� ��)
			query.rpp(20);
			List<Tweet> results = twitter.search(query).getTweets();
			Tweet u;
			
			GeoLocation geo = null;
			for(ListIterator<Tweet> itor = results.listIterator(); 
												itor.hasNext();){
    			u = itor.next(); 			
    			
    			if(u.getGeoLocation() != null){
    				geo = u.getGeoLocation();
    				Log.d("TTTTTTT", "zzzz");
    			}
    			
    			String imageURL = u.getProfileImageUrl();
    			Bitmap bit = null;
    			try {
					bit = BitmapFactory.decodeStream(new URL(imageURL)
																.openStream());
					imageCache.put(imageURL, bit);
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
    			
    			String tempS = u.getFromUser() + ": " + u.getText().replace("#SMUGuide", "");
    			String creatAt = u.getCreatedAt().toLocaleString();
    			tweetArray.add(new TweetInfo(u.getFromUser(), tempS, bit, geo, creatAt));
			
			}
			return tweetArray;
			
		}catch (TwitterException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * �б����� �ݰ� 1km�̳��� Ʈ�������ؾ� ��. �׷��� ������ ��ǥ �ʿ�!
	 * 
	 * @return	GeoLocation	���� ��ġ(����)
	 */
	private GeoLocation getMyGeoLocation(){
		Location hardFix = new Location("reverseGeocoded");
		hardFix.setLatitude(37.60192850146395);
		hardFix.setLongitude(126.95474624633789);
		hardFix.setAltitude(20);
		
		return new GeoLocation(hardFix.getLatitude(),
						hardFix.getLongitude());
	}
	
	/**
	 * �ݰ� ����
	 * 
	 * @return	radius	
	 */
	private double getRadius()
	{
		return radius * 0.001;	// Ʈ���� API �� �°� �ݰ� ����.
	}
	
	/**
	 * ã�Ƴ� Ʈ�� ����
	 * 
	 * @return tweetArray	Search results
	 */
	public ArrayList<TweetInfo> getTweets(){
		return tweetArray;
	}

}