package com.nbd.network.networkstatus;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * 网络状态管理
 * @author richerHe
 *
 */
public class NetworkStatusManager {
	private static NetworkStatusManager instance;
	
	private NetworkStatus networkStatus; 
	private Context context;
	
	/**
	 * 在网络状态改变时调用改监听器通知监听类进行相应处理
	 */
	private ArrayList<OnNetworkStatusListener> networkStatusListeners;
	
	/**
	 * 接受网络状态改变时系统发送的广播消息
	 */
	private BroadcastReceiver netWorkReceiver;
	private boolean firstNetwokrBro;
	
	private NetworkStatusManager() { }
	
	/**
	 * 获取单例对象进行网络状态监听的注册
	 * @return
	 */
	public static NetworkStatusManager getInstance() {
		if(null == instance) {
			instance = new NetworkStatusManager();
		}
		return instance;
	}
	
	/**
	 * 初始化管理类
	 */
	public void init(Context context) {
		this.context = context;
		networkStatusListeners = new ArrayList<OnNetworkStatusListener>();
		networkStatus = NetworkStatus.ON;
		firstNetwokrBro = true;
		checkNetworkStatus();
		registNetworkReceiver();
	}
	
	public void registerNetworkStatusChangeLisener(OnNetworkStatusListener lisener) {
		networkStatusListeners.add(lisener);
	}
	
	public void unregisterNetworkStatusChangeLisner(OnNetworkStatusListener lisener) {
		networkStatusListeners.remove(lisener);
	}
	
	public NetworkStatus getNetWorkStatus() {
		return networkStatus;
	}
	
	/**
	 * 判断网络是否连通
	 */
	public void checkNetworkStatus() {
		
		ConnectivityManager connectivity = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						networkStatus = NetworkStatus.ON;
						return;
					}
				}
			}
		}
		networkStatus = NetworkStatus.OFF;
	}
	
	/**
	 * 注册广播接受，在网络状态改变时接受系统发送的广播
	 */
	protected void registNetworkReceiver() {
		if (netWorkReceiver == null) {
			netWorkReceiver = new BroadcastReceiver() {
				private State preState;

				@Override
				public void onReceive(Context context, Intent intent) {
					if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
						if (firstNetwokrBro) {
							firstNetwokrBro = false;
							return;
						}
						NetworkInfo tmpInfo = 
								(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
						NetworkInfo.State state = tmpInfo.getState();

						if (state == State.DISCONNECTED && preState != State.DISCONNECTED) {
							networkStatus = NetworkStatus.OFF;
							preState = State.DISCONNECTED;
							
							for(OnNetworkStatusListener listener : networkStatusListeners) {
								listener.onNetworkStatusChange(NetworkStatus.OFF);
							}
						} else if (state == State.CONNECTED && preState != State.CONNECTED) {
							networkStatus = NetworkStatus.ON;
							preState = State.CONNECTED;
							for(OnNetworkStatusListener listener : networkStatusListeners) {
								listener.onNetworkStatusChange(NetworkStatus.ON);
							}
						}
					}
				}
			};
		}

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(netWorkReceiver, mFilter);
	}
	
	/**
	 * 注销监听网络连接的广播接受器
	 */
	public void unRegisterReceiver() {
		if (netWorkReceiver != null) {
			context.unregisterReceiver(netWorkReceiver);
			netWorkReceiver = null;
		}
	}
}
