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
 * �ǹ� ��ü���� ����Ʈ��� �����ִ� ��Ƽ��Ƽ
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
		
		//��� ��ü�� �̹����� �̸�, ��ġ������ ����
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
			
			//���� ��ġ�� ��ü�� ���� ��ġ���� �Ÿ��� ����
			Location.distanceBetween(objectLocation[0], objectLocation[1],
					location[0], location[1], dist);
			
			//Log �׽�Ʈ
			Log.d("Distance", dist[0]+" ");
			
			//����Ʈ�� �׸� �߰�
			arrayItem.add( new MyItem(bit, Name, (int)dist[0], i ) );
			
		}
		
		//��� ����
		MyListAdapter MyAdapter = new MyListAdapter(this, R.layout.icontext, arrayItem);
		ListView MyList = (ListView)findViewById(R.id.list);
		MyList.setAdapter(MyAdapter);	//����Ʈ��� ��͸� ����

	}
	
	@Override
	public void onBackPressed(){
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
	}
	
	/**
	 * ����Ʈ�� ������ �׸��� ä�� ������ Ŭ����
	 * �̹����� �̸�, �Ÿ��� �Ӽ����� ��
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
	 * ��� Ŭ����, ListBuildView�� ����Ŭ����
	 * ���� �� ��Ƽ��Ƽ������ ���Ǳ� ������ private ���� ������
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
		 * ����Ʈ �׸� ���� ����
		 * 
		 * @return size of ArrayList
		 */ 
		public int getCount() {
			return arraySrc.size();
		}

		/**
		 * ������ ����Ʈ �׸� �̸� ����
		 * 
		 * @return name of ArrayList
		 */
		public String getItem(int position) {
			return arraySrc.get(position).Name;
		}

		/**
		 * ���° �׸����� �����ϴ� �Լ�
		 * 
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
			txt2.setText("�Ÿ�:" + arraySrc.get(position).distance + "m");

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