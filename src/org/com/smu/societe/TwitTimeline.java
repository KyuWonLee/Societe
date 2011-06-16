package org.com.smu.societe;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.com.smu.societe.data.TwitAccTokenDB;
import org.com.smu.societe.imagecache.ImageCache;

import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
/**
 * 
 * @author YMH
 *
 */
public class TwitTimeline extends Activity implements LocationListener{
	private ArrayList<MyTweet> tweetItems;
	private TimelineListAdapter MyAdapter;
	private ListView MyList;
	private TextView textLength;
	private EditText tweet;	
	private ImageButton tweetBtn;
	private ImageButton searchBtn;
	private LinearLayout loadingLayout;
    private ProgressBar progressBar;
    
    private int pageNumber = 1;
	private int lastItem;
	private boolean isGroupTweet = false;		//Group Tweet flag
	private boolean isProcessing = false;		//Thread Processing flag
	private boolean isUpdateStop = false;				
    private boolean isLoading = false;				

    private static final int perPageNumber = 15;	
    
    private static Twitter twitter;			//Twitter instance
    private static ImageCache imageCache;	//Image Cache instance
	private SQLiteDatabase db;				
    private TwitAccTokenDB tweetDB;
	
    private LocationManager locationMgr;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinelist2);

		//Twitter instance 
		twitter = TwitterView.getTwitterInstance();
		
		//DB instace 
		tweetDB = TwitAccTokenDB.getDBInstance(this);
		db = TwitterView.getDBInstance();
		
		//Cache instance 
		imageCache = ImageCache.getInstance();
		
		
		tweetItems = new ArrayList<MyTweet>();	

    	LinearLayout searchLayout = new LinearLayout(TwitTimeline.this);
    	searchLayout.setOrientation(LinearLayout.HORIZONTAL);
    	searchLayout.setLayoutParams(new LinearLayout.LayoutParams(
    	           LinearLayout.LayoutParams.WRAP_CONTENT, 20));
    	
    	progressBar = new ProgressBar(TwitTimeline.this);
        progressBar.setPadding(0, 0, 15, 0);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
        searchLayout.addView(progressBar,new LinearLayout.LayoutParams(
           LinearLayout.LayoutParams.WRAP_CONTENT,
           LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        TextView textView = new TextView(TwitTimeline.this);
        textView.setText("Loading...");
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 20));
        searchLayout.addView(textView, new LinearLayout.LayoutParams(
           LinearLayout.LayoutParams.FILL_PARENT,
           LinearLayout.LayoutParams.FILL_PARENT
        ));
        searchLayout.setGravity(Gravity.CENTER);
        
        loadingLayout = new LinearLayout(TwitTimeline.this);
        loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(
           LinearLayout.LayoutParams.WRAP_CONTENT,
           LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        loadingLayout.setGravity(Gravity.CENTER);
        
        MyList = (ListView)findViewById(R.id.timelinelist);
        registerForContextMenu(MyList);				
        MyList.addFooterView(loadingLayout);		
        MyList.setDivider(new ColorDrawable(Color.LTGRAY));
    	MyList.setDividerHeight(1);
    	MyList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(!isLoading && lastItem == tweetItems.size() &&
						scrollState == OnScrollListener.SCROLL_STATE_IDLE){ 
					
					if(!isGroupTweet){		
						isLoading = true;	
						++pageNumber;	
						
						//Timeline update
						updateListAndAdapter();	
					} else{		
						MyList.removeFooterView(loadingLayout);
					}
			    }
			}
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount -1 ;
			}
		});
    	
    	tweetBtn = (ImageButton)findViewById(R.id.tweetBtn);
    	tweetBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showTweetEditDialog();
			}
		});

    	/*
    	searchBtn = (ImageButton)findViewById(R.id.searchBtn);
    	searchBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TwitTimeline.this, MapOverlay.class);
				intent.putExtra("ARView", "TweetSearch");
				startActivity(intent);
			}
		});*/
    	
		MyAdapter = new TimelineListAdapter(TwitTimeline.this,
											R.layout.profiletext, tweetItems);
    	MyList.setAdapter(MyAdapter);	
    	
    	//Timeline loading
    	updateListAndAdapter();
	
	}
	
	private void showTweetEditDialog(){
		LayoutInflater inflater = 
				(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.customedit, null);

		textLength = (TextView)view.findViewById(R.id.textNumber);
		
		tweet = (EditText)view.findViewById(R.id.status);
		tweet.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textLength.setText((140 - tweet.length()) + "");	
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub		
			}
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		Button updateBtn = (Button)view.findViewById(R.id.updateBtn);
		updateBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(tweet.getText() != null && tweet.length() <= 140){				
					String updateState = tweet.getText().toString() + " #SMUGuide";
					
					//Status update
					updateStatusToTimeline(updateState);
					
					tweet.setText("");
					
					MyList.setSelectionAfterHeaderView();

				}
				else{
					Toast.makeText(TwitTimeline.this, "failed",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Button gpsBtn = (Button)view.findViewById(R.id.gpsBtn);
		gpsBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});
		
		new AlertDialog.Builder(this)
		.setView(view)
		.setCancelable(true)
		.show();
	}
	
	/**
	 * Update Thread for user's tweet
	 * 
	 * @param updateState
	 */
	private void updateStatusToTimeline(final String updateState){
		runOnUiThread(new Runnable(){
			public void run() {
				try{
					Status status = null;
					
					if(isNetworkConnected()){	
						//Update Status
						//status = twitter.updateStatus(updateState);
						status = twitter.updateStatus(updateState, 
								new GeoLocation(37.60192850146395,126.95474624633789));
						String Tweets = null;
						Bitmap icon = null;
	
						String name = status.getUser().getScreenName();
						Tweets = "<b>" + name + "</b>" + " " + status.getText();		
						Date d = status.getCreatedAt();
						
		    			
			    		icon = imageCache.get(name);
						
						if(icon == null){	
							//Profile URL down thread
							profileImageDownThread(0, status, name);
							
							//Default image setting
							icon = BitmapFactory.decodeResource(getResources(),
																R.drawable.profile);
						}	
						
						//add item to list
						tweetItems.add(0, new MyTweet(icon, Tweets, d));
						
						//notify
						MyAdapter.notifyDataSetChanged();
						
						Toast.makeText(TwitTimeline.this, "Update Complete",
								Toast.LENGTH_LONG).show();
					
					} else {
						Toast.makeText(TwitTimeline.this, "failed", 
								Toast.LENGTH_LONG).show();
					}
				}catch(TwitterException te){
					Toast.makeText(TwitTimeline.this, "Failed",
							Toast.LENGTH_LONG).show();
					te.printStackTrace();
				}
			}	
		});	
	}
	
	/**
	 * User list update UI Thread 
	 */
	private void updateAdapterUIThread() {
        // Post this back on the UI thread
		runOnUiThread(new Runnable() {
            public void run() {    		
            	//notify
            	MyAdapter.notifyDataSetChanged();
            	
                isLoading = false;
            }
        });
    }
	
	/*
	 * 
	 * @param	page number for update
	 * @exception	TwitterException
	 */
	private void updateListAndAdapter() {
		if(isNetworkConnected()){
			Thread timelineThread = new Thread(null, doBackgroundThreadProcessing,
			"Background");
			timelineThread.start();
		} else{
			Toast.makeText(this, "failed",
					Toast.LENGTH_LONG).show();
		}
        
    }
	
	private Runnable doBackgroundThreadProcessing = new Runnable(){   
        public void run() {
            	List<Status> timeline = null;
				try {
					
					isProcessing = true;
					isUpdateStop = false;
						
					/* Paging - get Timeline */
					timeline = twitter.getFriendsTimeline(new Paging(pageNumber, perPageNumber));	
					
	            	Status s;
	            	String Tweets = null;
	            	Bitmap icon = null;      			
						
	            	/* Iterator  */
	            	for(ListIterator<Status> itor = timeline.listIterator(); itor.hasNext()
	            					&& isProcessing;){
	            		s = itor.next();
	            		String name = s.getUser().getScreenName();
	            		Tweets = "<b>" + name + "</b>" + " " + s.getText();	
	            		Date d = s.getCreatedAt();
	            			
			    		
				    	icon = imageCache.get(name);
	            			
						if(icon == null){	
							//pfofile image download thread
							profileImageDownThread(tweetItems.size(), s, name);
								
							//default image
							icon = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
							
						}
						synchronized (tweetItems) {		
							//ListView
							tweetItems.add(new MyTweet(icon, Tweets, d));
						}
						//ListView update - UIThread
						updateAdapterUIThread();
							
	            	}
	            	isProcessing = false;
            		
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
	};
	
	/**
	 * Profile image download thread
	 * 
	 * @param position
	 * @param s
	 * @param name
	 */
	private void profileImageDownThread(final int position,
			final Status s, final String name){
		new Thread(){
			public void run(){
				try {
					if(isNetworkConnected()){
						URL profileUrl = null;
						profileUrl = s.getUser().getProfileImageURL();
						Bitmap icon = BitmapFactory.decodeStream(profileUrl.openStream());
						
						if(icon != null){
							//UI Thread
							profileImageUIThread(position, icon);
						
							//cache 
							imageCache.put(name, icon);
						}
					} else {
						Toast.makeText(TwitTimeline.this, "failed", 
								Toast.LENGTH_LONG).show();
					}
					
				} catch (IOException e) {
					Toast.makeText(TwitTimeline.this, "Failed image loading", 
							Toast.LENGTH_LONG).show();
				
					e.printStackTrace();
				}			
			}
		}.start();
		
	}
	
	/**
	 * Profile Image Update UI Thread
	 * 
	 * @param position		
	 * @param icon			
	 */
	private synchronized void profileImageUIThread(final int position, final Bitmap icon){
		runOnUiThread(new Runnable(){
			public void run() {
				if(!isUpdateStop){		
					//Image Setting
					MyAdapter.setImage(position, icon);
					
					//notify
					MyAdapter.notifyDataSetChanged();
				}
			}		
		});
	}
	
	/**
	 *  Search SMUGuide Group' Tweets
	 */
	private void updateGroupTweets(){
		new Thread(){
			public void run(){
				Bitmap icon = null;
				
				try {
					if(isNetworkConnected()){
						isProcessing = true;
						
						Query query = new Query();
						
						query.setQuery("#SMUGuide");		
						query.rpp(50);
						List<Tweet> results = twitter.search(query).getTweets();
						Tweet u;
						
						for(ListIterator<Tweet> itor = results.listIterator(); itor.hasNext()
								&& isProcessing;){
			    			u = itor.next();
			    			String name = u.getFromUser();
			    			String Tweets = "<b>" + name + "</b>" + " " + u.getText();	
			    			Date d = u.getCreatedAt();

			    			//cache 
				    		icon = imageCache.get(name);	

							if(icon == null){	//Cache 
								//default image
								icon = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
							
							}
							synchronized(tweetItems){
								//ListView
								tweetItems.add(new MyTweet(icon, Tweets, d));
							}
							
							//ListView update - UIThread
							updateAdapterUIThread();
			    			
						}
					
						isProcessing = false;
					} else{
						Toast.makeText(TwitTimeline.this, "failed", 
								Toast.LENGTH_LONG).show();
					}
					
				} catch (TwitterException e) {
					Toast.makeText(TwitTimeline.this, "Failed search", 
							Toast.LENGTH_LONG).show();
				
					e.printStackTrace();
				}
			}
		}.start();

	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		/*
		menu.add(0, 1, 0, "All Tweets")
		.setIcon(R.drawable.alltwit)
		.setVisible(isGroupTweet)
		.setEnabled(!isProcessing);
*/
		menu.add(0, 2, 0, "SMUGuide")
		.setIcon(R.drawable.miniteamlogo)
		.setVisible(!isGroupTweet)
		.setEnabled(!isProcessing);

		menu.add(0, 3, 0, "Logout")
		.setIcon(R.drawable.logout);
		
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 1:
			if(isNetworkConnected()){	//network connected
				tweetItems.removeAll(tweetItems);
				MyAdapter.notifyDataSetChanged();
				
				if(MyList.getFooterViewsCount() == 0)
					MyList.addFooterView(loadingLayout);
				
				isGroupTweet = false;
				pageNumber = 1;		
			
				//Timeline loading
				updateListAndAdapter();
				
			}
			return true;
			
		case 2:	
			if(isNetworkConnected()){
				isUpdateStop = item.isEnabled();

				tweetItems.removeAll(tweetItems);
				MyAdapter.notifyDataSetChanged();
				
				isGroupTweet = true;

				updateGroupTweets();
				
			}
			return true;
			
		case 3:
			db = tweetDB.getWritableDatabase();
			db.delete("dic", null, null);
			tweetDB.close();
			finish();
			return true;
			
		}
		return false;
		
	}
	
	/**
	 * 3G or Wifi 
	 * 
	 * @return	boolean		
	 */
	private boolean isNetworkConnected(){
		ConnectivityManager manager = 
				(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
							.isConnectedOrConnecting();
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
							.isConnectedOrConnecting();
		
		return (isMobile || isWifi);
		
	}
	
	/**
	 * GPS 
	 */
	public void startLocationLisetener(){
		
		locationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		String provider = LocationManager.GPS_PROVIDER;
		//currentLoc = locationMgr.getLastKnownLocation(provider);
		
		if(locationMgr.isProviderEnabled(provider)){
			locationMgr.requestLocationUpdates(provider, 1000, 0, this);
		} 
	}
	
	/**
	 * GPS 
	 */
	public void endLocationListener(){
		locationMgr.removeUpdates(this);
	}

	public void onLocationChanged(Location location) {
		//currentLoc = location;
	}

	public void onProviderDisabled(String provider) {
		
	}

	public void onProviderEnabled(String provider) {
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
}

/**
 * Adapter - MyTweet ListView binding
 * 
 * @author YMH
 *
 */
class TimelineListAdapter extends BaseAdapter {
	private Context maincon;
	private ArrayList<MyTweet> arraySrc;
	private int layout;

	public TimelineListAdapter(Context context, int alayout, ArrayList<MyTweet> aarSrc) {
		maincon = context;
		arraySrc = aarSrc;
		layout = alayout;
	}
	
	/** 
	 * 
	 * 
	 * @return size of ArrayList
	 */ 
	public int getCount() {
		return arraySrc.size();
	}

	/**
	 * 
	 * 
	 * @return name of ArrayList
	 */
	public String getItem(int position) {
		return arraySrc.get(position).Name;
	}
	
	/**
	 * 
	 * 
	 * @param position
	 * @param img
	 */
	public void setImage(int position, Bitmap img){
		arraySrc.get(position).img = img;
	}

	/**
	 * 
	 * 
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 
	 * @param	position	
	 * @param	convertView	
	 * @param	parent
	 * @return	convertView
	 * 
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater Inflater = (LayoutInflater)maincon.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = Inflater.inflate(layout, parent, false);
			convertView.setClickable(true);
		
		}
		ImageView img = (ImageView)convertView.findViewById(R.id.profileimg);
		img.setImageBitmap(arraySrc.get(position).img);

		TextView txt = (TextView)convertView.findViewById(R.id.timeline);
		txt.setText(Html.fromHtml(arraySrc.get(position).Name));
		
		TextView txt2 = (TextView)convertView.findViewById(R.id.date);
		txt2.setText(arraySrc.get(position).date.toLocaleString());
		
		return convertView;
	}

	protected Context createPackageContext(String string,
			int contextIgnoreSecurity) {
		return null;
	}
	
	public void onLongPress(MotionEvent e) {
	//	Toast.makeText(getApplicationContext(), "aa", Toast.LENGTH_SHORT).show();
	}
	
}

/**
 * 
 * @author YMH
 *
 */
class MyTweet {
	Bitmap img;
	String Name;
	Date date;
	
	MyTweet(Bitmap aImg, String aName, Date date) {
		this.img = aImg;
		this.Name = aName;
		this.date = date;
	}
}