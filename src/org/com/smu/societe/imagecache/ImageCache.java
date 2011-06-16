package org.com.smu.societe.imagecache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * Bitmap Image Cache class
 * SoftReference를 이용하여 메모리 부족현상이 
 * 발생하지 않도록 가장 사용되지 않는 것부터 없앤다.
 * 이것은 LinkedHashMap으로 가능해진다. 
 * ps. Singleton pattern 적용
 * 
 * @author YMH
 *
 */
public class ImageCache{
	//자기 자신의 인스턴스 생성
	private static ImageCache instance = null;
	
	//SoftReference를 이용한 cache
	private final HashMap<String, SoftReference<Bitmap>> cache;
	private static final int HARD_CACHE_CAPACITY = 100;
	
	private ImageCache(){
		int hashTableCapacity = (int)Math.ceil(HARD_CACHE_CAPACITY / 0.75f) + 1;
		cache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				hashTableCapacity, 0.75f, true){           
				private static final long serialVersionUID = 1;      
				@Override 
				protected boolean removeEldestEntry (	//최대 100개를 유지할 수 있도록 오래된 것을 삭제함
						Map.Entry<String, SoftReference<Bitmap>> eldest) {
					return size() > HARD_CACHE_CAPACITY; 
				}
			}; 	
	};
	
	/**
	 * Singleton pattern을 이용하여 cache는 하나만 존재해야 
	 * 하기 때문에 가지고 있는 자신의 인스턴스를 리턴함
	 * 
	 * @return	instance	
	 */
	public static ImageCache getInstance(){
		if(instance == null){	//인스턴스가 생성되지 않았을 경우
			instance = new ImageCache();
		}
		
		return instance;
	}
	
	/**
	 * Cache 에  Bitmap 이미지 추가
	 * 
	 * @param key		추가할 이미지의 key data
	 * @param bitmap	추가할 이미지의 Bitmap data
	 */
	public synchronized void put(String key, Bitmap bitmap){
		cache.put(key, new SoftReference<Bitmap>(bitmap));
	}
	
	/** Clear cache */
	public void clear(){
		cache.clear();
	}
	
	/**
	 * 전달 받은 userId를 key값으로 하는 Bitmap 이미지 리턴
	 * 없을 시에 null값 리턴
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