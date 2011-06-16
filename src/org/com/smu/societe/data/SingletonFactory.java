package org.com.smu.societe.data;


public class SingletonFactory{
	/**
	 * � Ŭ���� �ν��Ͻ��� ������ ������ �������ִ� Ŭ����
	 * 
	 * @param name			Ŭ���� ���� ����
	 */
	public static SingletonBase create(String name){
		if(name == null){
			throw new IllegalArgumentException("Error");
		}
		
		if(name.equals("ARView")){
			return SingletonDB.createInstance();
		} else if(name.equals("Twitter")){
			return SingletonTwitter.createInstance();
		} else{
			return null;
		}
	}	
	
}