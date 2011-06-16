package org.com.smu.societe.networkstate;

import android.net.ConnectivityManager;

public class NetworkState{
	public static boolean isNetworkConnected(ConnectivityManager manager){
		boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
							.isConnectedOrConnecting();
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
							.isConnectedOrConnecting();
		
		return (isMobile || isWifi);
	
	}
}