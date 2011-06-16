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
 * ������ �¼� ��Ȳ �����ִ� Ŭ����
 * Jerico �ļ��� �̿��Ͽ� �����͸� �޾ƿ�
 * (AsynkTask�� �̿��Ͽ� �񵿱������� ������ ó����)
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
	
	/** ��Ƽ��� ������ ȣ�� */
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
					Toast.makeText(SMULibraryView.this, "��Ʈ��Ʈ�� ������� �ʾҽ��ϴ�.", 
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
	 * ����Ʈ�� ������ Ŭ�� �̺�Ʈ ������ 
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
		if(NetworkState.isNetworkConnected(conManger)){	//��Ʈ��ũ ���Ῡ�� Ȯ��
        	/* Execute Asynctask */
            new DownloadAsynctask().execute(urlSet);
            
        } else {
        	Toast.makeText(this, "��Ʈ��ũ�� ������� �ʾҽ��ϴ�.",
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
		menu.add(0, 1, 0, "���ΰ�ħ")
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
				Toast.makeText(this, "��Ʈ��Ʈ�� ������� �ʾҽ��ϴ�.", 
						Toast.LENGTH_LONG).show();
			}
			return true;

		}
		return false;
	}
	*/
	/**
	 * AsyncTast Ŭ������ ���޵� 
	 * Result �Ķ���͸� ���� Ŭ���� 
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
	 * ���� web ���� �¼������� �������� 
	 * �񵿱� ������ Ŭ����
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
					// �¼� ����� �޾ƿ´�
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
			//��ȯ�� �����
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
			
			arrayItem.add(0, new RoomDataItem("����� ������", "Total: " + dataSet[2][0],
					"Using: " + dataSet[2][1], "Remain: " + dataSet[2][2], 
					(int)(Float.parseFloat(dataSet[2][1])/Float.parseFloat(dataSet[2][0])*100)
					+"%"));
			arrayItem.add(0, new RoomDataItem("5�� ������", "Total: " + dataSet[1][0],
					"Using: " + dataSet[1][1], "Remain: " + dataSet[1][2],
					(int)(Float.parseFloat(dataSet[1][1])/Float.parseFloat(dataSet[1][0])*100)
					+"%"));	
			arrayItem.add(0, new RoomDataItem("4�� ������", "Total: " + dataSet[0][0],
					"Using: " + dataSet[0][1], "Remain: " + dataSet[0][2], 
					(int)(Float.parseFloat(dataSet[0][1])/Float.parseFloat(dataSet[0][0])*100)
					+"%"));
			
			BaseAdapter MyAdapter = new ListAdapter(SMULibraryView.this,
										R.layout.seattext, arrayItem);
        	myList.setAdapter(MyAdapter);	//����Ʈ��� ��͸� ����
        	
        	/* Gone progress frame */
        	progressFrame.setVisibility(ProgressBar.GONE);
			
		}	
		
		@Override 
		protected void onCancelled(){
			/* Gone progress frame */
			progressFrame.setVisibility(ProgressBar.GONE);
		}
		
		/**
		 * ���޹��� URL�ּҿ��� Jerico �ļ��� �̿��Ͽ� 
		 * �¼����� ���� �����͸� ��ȯ�ϴ� �Լ�
		 * 
		 * @param param		URL
		 * @return	builder	���� �¼����� ��Ʈ������ 
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
				//�ѱ� ���ڵ�		
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
		 * ����Ʈ �׸� ���� ����
		 * 
		 * @return size		����Ʈ ũ��
		 */ 
		public int getCount() {
			return arraySrc.size();
		}

		/**
		 * ������ ����Ʈ �׸� �̸� ����
		 * 
		 * @param  position		�ε���
		 * @return name			����Ʈ �������� �̸� 
		 */
		public String getItem(int position) {
			return arraySrc.get(position).Name;
		}

		/**
		 * ���° �׸����� �����ϴ� �Լ�
		 * 
		 * @param	position	�ε���
		 * @return	position	Id
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * ����Ʈ �׸�� ����
		 * 
		 * @param	position	���° �׸�����
		 * @param	convertView	�ٲ��� ��, ���⼱ ListView�� �ٲ��ֱ� ���� �׸�����
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
	
	/* ����Ʈ �信 ����� �׸� Ŭ���� */
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