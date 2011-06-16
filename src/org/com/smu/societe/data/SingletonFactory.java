package org.com.smu.societe.data;


public class SingletonFactory{
	/**
	 * 어떤 클래스 인스턴스를 생성할 것인지 결정해주는 클래스
	 * 
	 * @param name			클래스 종류 구분
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