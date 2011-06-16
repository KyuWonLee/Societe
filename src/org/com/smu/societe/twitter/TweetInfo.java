package org.com.smu.societe.twitter;

import android.graphics.Bitmap;
import twitter4j.GeoLocation;

public class TweetInfo{
	String userId;
	String text;
	Bitmap profile;
	String createAt;
	GeoLocation geo;
	
	public TweetInfo(String userId, String text, Bitmap profile,
			GeoLocation geo, String createAt){
		this.userId = userId;
		this.text = text;
		this.profile = profile;
		this.geo = geo;
		this.createAt = createAt;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public String getText(){
		return text;
	}
	
	public Bitmap getProfileImg(){
		return profile;
	}
	
	public String getCreateAt(){
		return createAt;
	}
	
	public GeoLocation getTweetGeoLocation(){
		return geo;
	}
}