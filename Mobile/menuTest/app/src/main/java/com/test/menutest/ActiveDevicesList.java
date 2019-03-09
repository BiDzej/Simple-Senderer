package com.test.menutest;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

public class ActiveDevicesList extends Observable {

    private static ActiveDevicesList instance = null;
    public ArrayList<String> devicesList = null;
    public ArrayList<String> deviceIPList = null;
    public ArrayList<String> deviceMACList = null;

    private ActiveDevicesList()
    {
        devicesList = new ArrayList<>();
        deviceIPList = new ArrayList<>();
        deviceMACList = new ArrayList<>();
    }

    public static synchronized ActiveDevicesList getInstance()
    {
        if(instance == null)
            instance = new ActiveDevicesList();
        return instance;
    }

    public void clearLists()
    {
        devicesList.clear();
        deviceIPList.clear();
        deviceMACList.clear();
    }

    public void addDevice(String deviceIP, String deviceMAC)
    {
        Iterator<String> itr = deviceMACList.iterator();
        while(itr.hasNext())
        {
            if(itr.next().equals(deviceMAC))
                return;
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        //Checking if device has been added by name to devices list, if yes we put it's name, if not we put ip addr
        devicesList.add(prefs.getString(deviceMAC, deviceMAC));
        deviceIPList.add(deviceIP);
        deviceMACList.add(deviceMAC);
        setChanged();
        notifyObservers();
    }
}
