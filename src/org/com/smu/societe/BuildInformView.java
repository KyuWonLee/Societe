package org.com.smu.societe;

import org.com.smu.societe.data.SingletonBase;
import org.com.smu.societe.data.SingletonDB;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * �ǹ������� �����ִ� Ŭ����
 * ������ �ǹ��� ���� DB���� �ǹ� ������ ��ȸ�Ͽ� �����ִ� ��Ƽ��Ƽ
 * 
 * @Project Societe
 * @File 	BuildInformView.java
 * @Date 	2011-04-30
 * @Author 	ACE
 */
public class BuildInformView extends Activity {
	private TextView mText;
	private TextView description;
	private TextView telephone;
	private TextView rest;
	private ImageView img;
	private ImageButton callBtn;
	private ImageButton homeBtn;

	//private double[] location;
	private int id;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building);
        
        // �ǹ��� ������ �޾ƿ� 
        Intent intent = getIntent();
        id = intent.getIntExtra("ARView", -1);
        String name = intent.getStringExtra("Name");
        //location = intent.getDoubleArrayExtra("Location");
        
        // DB instance 
        SingletonBase json = SingletonDB.createInstance();
        
        // ���õ� Marker 
        Marker ma = json.markers.get(id);
 
        // �ǹ� �̹���
        img = (ImageView)findViewById(R.id.ImageView01);
        Bitmap bit = BitmapFactory.decodeFile(
				Environment.getExternalStorageDirectory().getPath() + "/ARview/" +
				ma.build_picture + ".jpg");
        img.setImageBitmap(bit);
        
        // �ǹ��� �̸�
        mText = (TextView)findViewById(R.id.buildText2);
        mText.setText(name);
        
        // �ǹ��� ����
        description = (TextView)findViewById(R.id.buildDescript);
        description.setText(ma.descript);
        
        // �ǹ��� ���ǽü�
        rest = (TextView)findViewById(R.id.buildRest);
        rest.setText(ma.comforts);
        
        //�ǹ��� ��ȭ��ȣ
        telephone = (TextView)findViewById(R.id.buildTele);
        telephone.setText(ma.phone); 
        
        callBtn = (ImageButton)findViewById(R.id.callBtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(telephone.getText() != null){
					startActivity(new Intent(Intent.ACTION_DIAL, 
							Uri.parse("tel:" + telephone.getText().toString())));
				}
			}
		});
        
        homeBtn = (ImageButton)findViewById(R.id.LittleHomeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BuildInformView.this, MainSelectView.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
				overridePendingTransition(0, R.anim.zoom_out);
			}
		});
        
    }
    
    @Override
	public void onBackPressed(){
		
		finish();
		
		overridePendingTransition(0, R.anim.zoom_out);
    }  
    
}