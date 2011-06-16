package org.com.smu.societe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/**
 * 
 * @Project  Societe
 * @File  	 SplashActivity.java
 * @Date  	 2010-11-22
 * @author 	 ACE
 * 
 */
public class SplashActivity extends Activity{
	private ImageView splash;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        initialize();
        
    }

	/**
	 * 
	 */
    private void initialize()
    {
    	Animation animation = new AlphaAnimation(0.0f, 1.0f);

    	animation.setAnimationListener(new AnimationListener(){

			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				finish();	
				
				Intent intent = new Intent(SplashActivity.this, MainSelectView.class);
		        startActivity(intent);
		        
		        /* zoom animation */
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				
			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			
			}

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	animation.setDuration(2000);

    	splash = (ImageView)findViewById(R.id.splash_image);
    	splash.setAnimation(animation);  
    	
    }
}