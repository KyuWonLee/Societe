package org.com.smu.societe.imagecache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * Bitmap Image Cache class
 * SoftReference�� �̿��Ͽ� �޸� ���������� 
 * �߻����� �ʵ��� ���� ������ �ʴ� �ͺ��� ���ش�.
 * �̰��� LinkedHashMap���� ����������. 
 * ps. Singleton pattern ����
 * 
 * @author YMH
 *
 */
public class ImageCache{
	//�ڱ� �ڽ��� �ν��Ͻ� ����
	private static ImageCache instance = null;
	
	//SoftReference�� �̿��� cache
	private final HashMap<String, SoftReference<Bitmap>> cache;
	private static final int HARD_CACHE_CAPACITY = 100;
	
	private ImageCache(){
		int hashTableCapacity = (int)Math.ceil(HARD_CACHE_CAPACITY / 0.75f) + 1;
		cache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				hashTableCapacity, 0.75f, true){           
				private static final long serialVersionUID = 1;      
				@Override 
				protected boolean removeEldestEntry (	//�ִ� 100���� ������ �� �ֵ��� ������ ���� ������
						Map.Entry<String, SoftReference<Bitmap>> eldest) {
					return size() > HARD_CACHE_CAPACITY; 
				}
			}; 	
	};
	
	/**
	 * Singleton pattern�� �̿��Ͽ� cache�� �ϳ��� �����ؾ� 
	 * �ϱ� ������ ������ �ִ� �ڽ��� �ν��Ͻ��� ������
	 * 
	 * @return	instance	
	 */
	public static ImageCache getInstance(){
		if(instance == null){	//�ν��Ͻ��� �������� �ʾ��� ���
			instance = new ImageCache();
		}
		
		return instance;
	}
	
	/**
	 * Cache ��  Bitmap �̹��� �߰�
	 * 
	 * @param key		�߰��� �̹����� key data
	 * @param bitmap	�߰��� �̹����� Bitmap data
	 */
	public synchronized void put(String key, Bitmap bitmap){
		cache.put(key, new SoftReference<Bitmap>(bitmap));
	}
	
	/** Clear cache */
	public void clear(){
		cache.clear();
	}
	
	/**
	 * ���� ���� userId�� key������ �ϴ� Bitmap �̹��� ����
	 * ���� �ÿ� null�� ����
	 * 
	 * @param 	userId	key data
	 * @return	Bitmap	
	 */
	public synchronized Bitmap get(String userId){
		
		Bitmap bit = null;
		SoftReference<Bitmap> reference = cache.get(userId);
		
		if(reference != null){
			bit = reference.get();
		}
		
		return bit;
	}
	
}