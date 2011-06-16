package org.com.smu.societe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * 상명대학교 학사공지 및 상명뉴스 리스트뷰
 * 
 * @Project Societe
 * @File 	SMUNoticeView.java
 * @Date 	2011-05-01
 * @author  ACE
 */
public class SMUNoticeView extends Activity {
	private ListView noticeList;
	private BaseAdapter Adapter;
	private ImageButton homeBtn;
	
	private ArrayList<NoticeItem> arrayList;
	
	private final String URL1 = "http://www.smu.ac.kr/Common/MessageBoard/ArticleList.do?forum=11180&cat=10141016";
	private final String URL2 = "http://www.smu.ac.kr/Common/MessageBoard/ArticleList.do?forum=11450&cat=10141016";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list);
        
        noticeList = (ListView)findViewById(R.id.noticeList);
        
        arrayList = new ArrayList<NoticeItem>();
        arrayList.add(new NoticeItem("학사공지", URL1));
        arrayList.add(new NoticeItem("행정공지", URL2));        
 
        Adapter = new ListAdapter(this, R.layout.notice_text, arrayList);
        
        noticeList.setAdapter(Adapter);
        noticeList.setOnItemClickListener(mItemClickListener);
        
        homeBtn = (ImageButton)findViewById(R.id.NoticeHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SMUNoticeView.this, MainSelectView.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
				overridePendingTransition(0, R.anim.zoom_out);
			}
		});
        
    }
    
    AdapterView.OnItemClickListener mItemClickListener = new 
    		AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					String url = Adapter.getItem(position).toString();
					
					Intent intent = new Intent(SMUNoticeView.this, NoticeDetailView.class);
					intent.putExtra("NOTICEURL", url);
					startActivity(intent);
					
					/* zoom animation */
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
	};
	
	@Override
	public void onBackPressed(){
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
	}
	
	class NoticeItem{
		String category;
		String url;
		
		NoticeItem(String category, String url){
			this.category = category;
			this.url = url;
		}	
	}
    
	class ListAdapter extends BaseAdapter {
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<NoticeItem> arraySrc;
		int layout;

		public ListAdapter(Context context, int alayout, ArrayList<NoticeItem> aarSrc) {
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
			return arraySrc.get(position).url;
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
			
			TextView txt = (TextView)convertView.findViewById(R.id.noticeText);
			txt.setText(arraySrc.get(position).category);

			return convertView;
		}

		protected Context createPackageContext(String string,
				int contextIgnoreSecurity) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}