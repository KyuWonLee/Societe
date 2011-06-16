package org.com.smu.societe.bus;

import java.util.ArrayList;

import org.com.smu.societe.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BusAdapter extends BaseAdapter{
	Context context;
	int layoutId;
	ArrayList<BusInfo> arrayList;
	
	public BusAdapter(Context context, int layoutId,
			ArrayList<BusInfo> arrayList){
		this.context = context;
		this.layoutId = layoutId;
		this.arrayList = arrayList;
	}
	
	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutId, parent, false);
		}
		
		TextView txt = (TextView)convertView.findViewById(R.id.text);
		txt.setText(arrayList.get(position).getBusNumber());
		
		TextView txt2 = (TextView)convertView.findViewById(R.id.text2);
		txt2.setText("약 " + arrayList.get(position).getTime() + 
				"분 후 도착(" + arrayList.get(position).getCount() + "번째 전)" );
		
		TextView txt3 = (TextView)convertView.findViewById(R.id.text3);
		txt3.setText("현재위치: " + arrayList.get(position).getName());
		
		return convertView;
	}
	
}