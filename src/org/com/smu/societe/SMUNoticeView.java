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
 * �����б� �л���� �� ����� ����Ʈ��
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
        arrayList.add(new NoticeItem("�л����", URL1));
        arrayList.add(new NoticeItem("��������", URL2));        
 
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
			return arraySrc.get(position).url;
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