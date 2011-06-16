package org.com.smu.societe;

import java.util.ArrayList;

import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonDB;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 건물 객체들을 리스트뷰로 보여주는 액티비티
 * 
 * @Project Societe
 * @File  	ListBuildView.java
 * @Date  	2011-04-30
 * @author 	ACE
 */
public class ListBuildView extends Activity{
	private ArrayList<MyItem> arrayItem;
	private double[] objectLocation;
	private ImageButton homeBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listtest);

		homeBtn = (ImageButton)findViewById(R.id.BuildListHomeBtn);
		homeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListBuildView.this, MainSelectView.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
				overridePendingTransition(0, R.anim.zoom_out);
			}
		});
		
		arrayItem = new ArrayList<MyItem>();
		
		Intent intent = getIntent();
		double[] location = new double[2];
		location = intent.getDoubleArrayExtra("Location");
		
		//모든 객체의 이미지와 이름, 위치정보를 얻음
		SingletonBase j = SingletonDB.createInstance();
		for(int i=0; i < j.markers.size(); i++){
			String Name = j.markers.get(i).mText;
			Bitmap bit = j.markers.get(i).bit;
			bit = Bitmap.createScaledBitmap(bit, bit.getWidth() - 25, bit.getHeight() - 25, false);
			
			float[] dist = new float[1];
			dist[0] = 0.0f;
			
			objectLocation = new double[2];
			objectLocation[0] = j.markers.get(i).mGeoLoc.getLatitude();
			objectLocation[1] = j.markers.get(i).mGeoLoc.getLongitude();
			
			//현재 위치와 물체의 실제 위치간의 거리를 구함
			Location.distanceBetween(objectLocation[0], objectLocation[1],
					location[0], location[1], dist);
			
			//Log 테스트
			Log.d("Distance", dist[0]+" ");
			
			//리스트에 항목 추가
			arrayItem.add( new MyItem(bit, Name, (int)dist[0], i ) );
			
		}
		
		//어뎁터 생성
		MyListAdapter MyAdapter = new MyListAdapter(this, R.layout.icontext, arrayItem);
		ListView MyList = (ListView)findViewById(R.id.list);
		MyList.setAdapter(MyAdapter);	//리스트뷰와 어뎁터를 연결

	}
	
	@Override
	public void onBackPressed(){
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
	}
	
	/**
	 * 리스트의 각각의 항목을 채울 아이템 클래스
	 * 이미지와 이름, 거리를 속성으로 함
	 * 
	 * @author ACE
	 *
	 */
	private class MyItem {
		private Bitmap img;
		private String Name;
		private int distance;
		private int id;
		
		MyItem(Bitmap aImg, String aName, int distance, int id) {
			this.img = aImg;
			this.Name = aName;
			this.distance = distance;
			this.id = id;
		}
	}

	/**
	 * 어뎁터 클래스, ListBuildView의 내부클래스
	 * 현재 이 액티비티에서만 사용되기 때문에 private 으로 생성함
	 * 
	 * @author ACE
	 *
	 */
	private class MyListAdapter extends BaseAdapter {
		private Context maincon;
		private LayoutInflater Inflater;
		private ArrayList<MyItem> arraySrc;
		private int layout;

		public MyListAdapter(Context context, int alayout, ArrayList<MyItem> aarSrc) {
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			arraySrc = aarSrc;
			layout = alayout;
		}
		
		/** 
		 * 리스트 항목 개수 리턴
		 * 
		 * @return size of ArrayList
		 */ 
		public int getCount() {
			return arraySrc.size();
		}

		/**
		 * 선택한 리스트 항목 이름 리턴
		 * 
		 * @return name of ArrayList
		 */
		public String getItem(int position) {
			return arraySrc.get(position).Name;
		}

		/**
		 * 몇번째 항목인지 리턴하는 함수
		 * 
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
		 * 
		 */
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
			}
			ImageView img = (ImageView)convertView.findViewById(R.id.img);
			img.setImageBitmap(arraySrc.get(position).img);

			TextView txt = (TextView)convertView.findViewById(R.id.text);
			txt.setText(arraySrc.get(position).Name);
			
			TextView txt2 = (TextView)convertView.findViewById(R.id.text2);
			txt2.setText("거리:" + arraySrc.get(position).distance + "m");

			ImageButton btn = (ImageButton)convertView.findViewById(R.id.btn);
			btn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent i = new Intent(maincon, BuildInformView.class);
					i.putExtra("Name", arraySrc.get(position).Name);
					i.putExtra("ARView", arraySrc.get(position).id);
					i.putExtra("Location", objectLocation);
					maincon.startActivity(i);
					
					/* zoom animation */
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			});
			return convertView;
		}

		@SuppressWarnings("unused")
		protected Context createPackageContext(String string,
				int contextIgnoreSecurity) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}