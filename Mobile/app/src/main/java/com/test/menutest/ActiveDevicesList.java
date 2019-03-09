package com.test.menutest;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

/**
 * Class of active devices. It contains 3 lists. First one is device name, second device IP and third device MAC.
 * Should be another class Device containing this three strings and only one list but it's easier in the future to
 * show active device list as an observable list.
 */
public class ActiveDevicesList extends Observable {

    //it's singleton, that's why there is instance
    private static ActiveDevicesList instance = null;
    public ArrayList<String> devicesList = null;
    public ArrayList<String> deviceIPList = null;
    public ArrayList<String> deviceMACList = null;

    //private constructor
    private ActiveDevicesList()
    {
        devicesList = new ArrayList<>();
        deviceIPList = new ArrayList<>();
        deviceMACList = new ArrayList<>();
    }

    //main function of singleton
    public static synchronized ActiveDevicesList getInstance()
    {
        if(instance == null)
            instance = new ActiveDevicesList();
        return instance;
    }

    //clear all list, done before searching active devices to avoid showing not active devices
    //and avoid adding one more time same device. One device is only once in the lists.
    public void clearLists()
    {
        devicesList.clear();
        deviceIPList.clear();
        deviceMACList.clear();
    }

    //add device to lists
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
