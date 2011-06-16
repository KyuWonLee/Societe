package org.com.smu.societe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import net.htmlparser.jericho.Source;

import org.com.smu.societe.networkstate.NetworkState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 도서관 좌석 현황 보여주는 클래스
 * Jerico 파서를 이용하여 데이터를 받아옴
 * (AsynkTask를 이용하여 비동기방식으로 쓰레드 처리함)
 * 
 * @Project  Societe
 * @File  	 SMUALibraryView.java
 * @Date  	 2010-12-17
 * @author 	 ACE
 */
public class SMULibraryView extends Activity{
	private FrameLayout progressFrame;
	private ListView myList;
	private ImageButton homeBtn;
	private ImageButton refreshBtn;
	
	private ArrayList<RoomDataItem> arrayItem;

	private String[] urlSet = { "http://61.72.129.163/roomview5.asp?room_no=1", 
					   			"http://61.72.129.163/roomview5.asp?room_no=2",
								"http://61.72.129.163/roomview5.asp?room_no=3" };
	private int mValue;
	private ConnectivityManager conManger;
	
	/** 액티비디 생성시 호출 */
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seatlist);
		
		conManger = 
			(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		/* Home Button */
		homeBtn = (ImageButton)findViewById(R.id.SeatHomeBtn);
		homeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SMULibraryView.this, MainSelectView.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
				overridePendingTransition(0, R.anim.zoom_out);
			}
		});
		
		/* Refresh Button */
		refreshBtn = (ImageButton)findViewById(R.id.SeatRefreshBtn);
		refreshBtn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(NetworkState.isNetworkConnected(conManger)){
					arrayItem.removeAll(arrayItem);
					
					new DownloadAsynctask().execute(urlSet);
				} else{
					Toast.makeText(SMULibraryView.this, "네트워트가 연결되지 않았습니다.", 
							Toast.LENGTH_LONG).show();
				}
			}
		});

		
		LayoutInflater inflater = (LayoutInflater)getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		/* ProgressBar FrameLysout */
		progressFrame = (FrameLayout)inflater.inflate(R.layout.progress_frame, null);
		progressFrame.setVisibility(ProgressBar.GONE);
		
		/* Add progress frame */
		addContentView(progressFrame, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));

		myList = (ListView)findViewById(R.id.list);
		myList.setOnItemClickListener(mItemClickListener);
		
		arrayItem = new ArrayList<RoomDataItem>();
        
        /* Downloads seats data */
        DownloadHTMLThread(conManger);

	}
	
	/**
	 * 리스트뷰 아이템 클릭 이벤트 리스너 
	 */
	AdapterView.OnItemClickListener mItemClickListener = 
		new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(SMULibraryView.this, ReadingRoomView.class);
			
			if(position == 0){
				intent.putExtra("SELECTEDURL", urlSet[0]);
			} else if(position == 1){
				intent.putExtra("SELECTEDURL", urlSet[1]);
			} else{
				intent.putExtra("SELECTEDURL", urlSet[2]);
			}
			
			startActivity(intent);
			
			/* zoom animation */
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		}
	};
	
	/**
	 * HTML download execute
	 */
	private void DownloadHTMLThread(ConnectivityManager conManager){
		if(NetworkState.isNetworkConnected(conManger)){	//네트워크 연결여부 확인
        	/* Execute Asynctask */
            new DownloadAsynctask().execute(urlSet);
            
        } else {
        	Toast.makeText(this, "네트워크가 연결되지 않았습니다.",
        			Toast.LENGTH_LONG).show();
        }   
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
		
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, 1, 0, "새로고침")
		.setIcon(R.drawable.refresh);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 1:
			if(NetworkState.isNetworkConnected(conManger)){
				arrayItem.removeAll(arrayItem);
				
				new DownloadAsynctask().execute(urlSet);
			} else{
				Toast.makeText(this, "네트워트가 연결되지 않았습니다.", 
						Toast.LENGTH_LONG).show();
			}
			return true;

		}
		return false;
	}
	*/
	/**
	 * AsyncTast 클래스에 전달될 
	 * Result 파라미터를 위한 클래스 
	 * 
	 * @author YMH
	 *
	 */
	class Result{
		String[] url = new String[3];
		Result(String[] url){
			this.url[0] = url[0];
			this.url[1] = url[1];
			this.url[2] = url[2];
		}
		
		public String get(int i){
			if( i<=3 )
				return url[i];
			else
				return null;
		}
	}
	
	/**
	 * AsyncTask class
	 * 상명대 web 에서 좌석데이터 가져오는 
	 * 비동기 쓰레드 클래스
	 * 
	 * @author YMH
	 *
	 */
	class DownloadAsynctask extends AsyncTask<String, Integer, Result>{
		
		@Override
		protected void onPreExecute(){
			mValue = 0;
			progressFrame.setVisibility(ProgressBar.VISIBLE);
		}	

		@Override
		protected Result doInBackground(String... params) {
			String[] tempResult = new String[3];
			int i = 0;
			while(isCancelled() == false){
				mValue += 33;
				if( mValue < 100){
					// 좌석 결과를 받아온다
					tempResult[i] = delHtmlTag(params[i].toString());
					publishProgress(mValue);
				} else{
					break;
				}
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){;}
				
				i++;
			}	
			//반환할 결과값
			Result result = new Result(tempResult);
			
			return result;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress){
		}
		
		@Override
		protected void onPostExecute(Result result){		
			String[] temp = new String[3];			
			for(int i = 0; i<3; i++)
				temp[i] = result.get(i);
			
			String[][] dataSet = new String[3][3];
			
			int i = 0;
			for( String string : temp ){
				dataSet[i] = string.split(" ");
				++i;
			}				
			
			arrayItem.add(0, new RoomDataItem("전산원 열람실", "Total: " + dataSet[2][0],
					"Using: " + dataSet[2][1], "Remain: " + dataSet[2][2], 
					(int)(Float.parseFloat(dataSet[2][1])/Float.parseFloat(dataSet[2][0])*100)
					+"%"));
			arrayItem.add(0, new RoomDataItem("5층 열람실", "Total: " + dataSet[1][0],
					"Using: " + dataSet[1][1], "Remain: " + dataSet[1][2],
					(int)(Float.parseFloat(dataSet[1][1])/Float.parseFloat(dataSet[1][0])*100)
					+"%"));	
			arrayItem.add(0, new RoomDataItem("4층 열람실", "Total: " + dataSet[0][0],
					"Using: " + dataSet[0][1], "Remain: " + dataSet[0][2], 
					(int)(Float.parseFloat(dataSet[0][1])/Float.parseFloat(dataSet[0][0])*100)
					+"%"));
			
			BaseAdapter MyAdapter = new ListAdapter(SMULibraryView.this,
										R.layout.seattext, arrayItem);
        	myList.setAdapter(MyAdapter);	//리스트뷰와 어뎁터를 연결
        	
        	/* Gone progress frame */
        	progressFrame.setVisibility(ProgressBar.GONE);
			
		}	
		
		@Override 
		protected void onCancelled(){
			/* Gone progress frame */
			progressFrame.setVisibility(ProgressBar.GONE);
		}
		
		/**
		 * 전달받은 URL주소에서 Jerico 파서를 이용하여 
		 * 좌석수에 대한 데이터를 반환하는 함수
		 * 
		 * @param param		URL
		 * @return	builder	현재 좌석수를 스트링으로 
		 */
		public String delHtmlTag(final String param){
			StringBuilder builder = null;
			
			String sourceUrlString = param;
					
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
					
			String renderedText = source.getRenderer().toString();
					
			try {
				//한글 인코딩		
				renderedText = new String(renderedText.getBytes(
									source.getEncoding()), "EUC-KR");
						
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
					
			String[] argument = renderedText.split(" ");
					
			builder = new StringBuilder();
					
			int i = -1;
			for(String string : argument){
				++i;
				try{
					int num = Integer.parseInt(string);
					builder.append(num + " ");
							
				}catch(NumberFormatException nfe){
					continue;
				}
			}
			return builder.toString();
	    }

	}
	
	class ListAdapter extends BaseAdapter {
		Context context;
		LayoutInflater inflater;
		ArrayList<RoomDataItem> arraySrc;
		int layout;

		public ListAdapter(Context context, int alayout,
						ArrayList<RoomDataItem> aarSrc) {
			this.inflater = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;	
			this.arraySrc = aarSrc;
			this.layout = alayout;
		}
		
		/** 
		 * 리스트 항목 개수 리턴
		 * 
		 * @return size		리스트 크기
		 */ 
		public int getCount() {
			return arraySrc.size();
		}

		/**
		 * 선택한 리스트 항목 이름 리턴
		 * 
		 * @param  position		인덱스
		 * @return name			리스트 아이템의 이름 
		 */
		public String getItem(int position) {
			return arraySrc.get(position).Name;
		}

		/**
		 * 몇번째 항목인지 리턴하는 함수
		 * 
		 * @param	position	인덱스
		 * @return	position	Id
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * 리스트 항목뷰 생성
		 * 
		 * @param	position	몇번째 항목인지
		 * @param	convertView	바꿔줄 뷰, 여기선 ListView로 바꿔주기 위한 그릇역할
		 * @param	parent
		 * @return	convertView
		 */
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(layout, parent, false);
			}
			
			TextView txt = (TextView)convertView.findViewById(R.id.text);
			txt.setText(arraySrc.get(position).Name);
			
			TextView txt2 = (TextView)convertView.findViewById(R.id.text2);
			txt2.setText(arraySrc.get(position).total);
			
			TextView txt3 = (TextView)convertView.findViewById(R.id.text3);
			txt3.setText(arraySrc.get(position).using);
			
			TextView txt4 = (TextView)convertView.findViewById(R.id.text4);
			txt4.setText(arraySrc.get(position).remain);
			
			TextView percentTxt = (TextView)convertView.findViewById(R.id.percentText);
			percentTxt.setText(arraySrc.get(position).percent);

			return convertView;
		}

		protected Context createPackageContext(String string,
				int contextIgnoreSecurity) {
			return null;
		}
		
	}
	
	/* 리스트 뷰에 출력할 항목 클래스 */
	class RoomDataItem {
		String Name;
		String percent;
		String total;
		String using;
		String remain;
		
		RoomDataItem(String aName, String total, String using,
				String remain, String percent) {
			this.Name = aName;
			this.total = total;
			this.using = using;
			this.remain = remain;
			this.percent = percent;
		}
	}
	
}