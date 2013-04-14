package com.clashers.infrastructure;

import java.util.HashMap;


import android.os.Message;
import android.os.RemoteException;

public class ServiceCollection {
	private HashMap<String, ServiceManager> serviceManagers;

	public ServiceCollection() {
		this.serviceManagers = new HashMap<String, ServiceManager>();
	}

	/**
	 * 
	 * @param nameOfServiceManager
	 * @param serviceManager
	 * @return true if the serviceManger was added. false if it could be added, previous 
	 * of previous mapping of the name
	 */
	public boolean addServiceManager(String nameOfServiceManager, ServiceManager serviceManager) {
		boolean retVal = false;
		if (!this.serviceManagers.containsKey(nameOfServiceManager)) {
			this.serviceManagers.put(nameOfServiceManager, serviceManager);
			retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param nameOfServiceManager
	 * @return true if the serviceManager was removed. false if there is no service with that name
	 */
	public boolean removeServiceManager(String nameOfServiceManager) {
		boolean retVal = false;
		if (this.serviceManagers.get(nameOfServiceManager) != null) {
			this.serviceManagers.remove(nameOfServiceManager);
			retVal = true;
		}
		
		return retVal;
	}

	public void startService(String nameOfServiceManager) {
		this.serviceManagers.get(nameOfServiceManager).start();
	}

	public void stopService(String nameOfServiceManager) {
		this.serviceManagers.get(nameOfServiceManager).stop();
	}

	public void unbindService(String nameOfServiceManager) {
		this.serviceManagers.get(nameOfServiceManager).unbind();
	}

	/**
	 * Returns true if the message was delivered successfully
	 * 
	 * @param nameOfServiceManager
	 * @param msg
	 * @return
	 */
	public boolean sendMessageToService(String nameOfServiceManager, Message msg) {
		boolean retVal = false;
		try {
			if (serviceManagers.containsKey(nameOfServiceManager)) {
				this.serviceManagers.get(nameOfServiceManager).send(msg);
				retVal = true;
			}
		} catch (RemoteException e) {
			retVal = false;
		}

		return retVal;
	}
	
	public void unbindAll() {
		for (ServiceManager serviceManager : this.serviceManagers.values()){
			serviceManager.unbind();
		}
	}
	
	public void removeAll() {
		this.serviceManagers.clear();
	}
	
	/**
	 * returns the specified key. If not exists returns null
	 * @param key
	 * @return
	 */
	public ServiceManager getService(String key) {
		return this.serviceManagers.get(key);
	}
	
	public void StartAllServices() {
		for (ServiceManager sm : this.serviceManagers.values()) {
			sm.start();
		}
	}
	
	public void StopAllServices() {
		for (ServiceManager sm : this.serviceManagers.values()) {
			sm.stop();
		}
	}

}
