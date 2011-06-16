package org.com.smu.societe.data;

import java.util.ArrayList;

import org.com.smu.societe.Marker;

public class SingletonBase{
	private static SingletonBase baseInstance = null;
	public ArrayList<Marker> markers = new ArrayList<Marker>();
	
	public static boolean isComplete = false;
	
	protected SingletonBase(){		
	}
	
	protected static SingletonBase createInstance(){
		if(baseInstance == null){
			baseInstance = new SingletonBase();
		}
		return baseInstance;
	};
	
	/**
	 * 
	 * @return boolean ������ �ε� �Ϸ� ����
	 */
	public boolean isCompleteLoading(){
		return isComplete; 
	}
	
}