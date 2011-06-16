package org.com.smu.societe.camerapreview;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Camera View
 * 
 * @Project  Campus AR 
 * @File  CameraSurface.java
 * @Date  2010-10-10
 * @author ACE
 * @see android.view.SurfaceView
 */
public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {

	private static SurfaceHolder holder;
	private Camera camera;

	
	public CameraSurface(Context context) {
		super(context);
		try {
			holder = getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		} catch (Exception ex) {
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			
			if (camera != null) {
				try {
					camera.stopPreview();
					
				} catch (Exception ignore) {
				}
				try {
					camera.release();
				} catch (Exception ignore) {
				}
				camera = null;
			}

			camera = Camera.open();
			camera.setPreviewDisplay(holder);
		} catch (Exception ex) {
			try {
				if (camera != null) {
					try {
						camera.stopPreview();
					} catch (Exception ignore) {
					}
					try {
						camera.release();
					} catch (Exception ignore) {
					}
					camera = null;
				}
			} catch (Exception ignore) {

			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			if (camera != null) {
				try {
					camera.stopPreview();
				} catch (Exception ignore) {
				}
				try {
					camera.release();
				} catch (Exception ignore) {
				}
				camera = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		try {
			Camera.Parameters parameters = camera.getParameters();
			try {
				List<Camera.Size> supportedSizes = null;	
				
				supportedSizes = parameters.getSupportedPreviewSizes();
				
				Iterator<Camera.Size> itr = supportedSizes.iterator(); 
				while(itr.hasNext()) {
					Camera.Size element = itr.next(); 
					element.width -= w;
					element.height -= h;
				} 
				Collections.sort(supportedSizes, new ResolutionsOrder());
				parameters.setPreviewSize(w + supportedSizes.get(supportedSizes.size()-1).width, h + supportedSizes.get(supportedSizes.size()-1).height);
			} catch (Exception ex) {
				parameters.setPreviewSize(480 , 320);
			}

			camera.setParameters(parameters);
			camera.startPreview();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void stopPreview(){
		camera.stopPreview();
		camera.release();
		camera = null;
	}
	
	public void startPreview(){
		camera.startPreview();
	}
	
}

class ResolutionsOrder implements java.util.Comparator<Camera.Size> {
	public int compare(Camera.Size left, Camera.Size right) {

		return Float.compare(left.width + left.height, right.width + right.height);
	}
}