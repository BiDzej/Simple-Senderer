package com.test.menutest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This is controller to adding new device to saved devices list. You can add a name for device
 * or rename device which already exist in your database.
 */
public class AddDeviceFragment extends Fragment {

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_device, container, false);

        final Spinner spinner = view.findViewById(R.id.deviceSpinner);
        final EditText editText = view.findViewById(R.id.DeviceName);
        final Button button = view.findViewById(R.id.setNameButton);
        final Button refreshButton = view.findViewById(R.id.refreshButton2);

        final ActiveDevicesList activeDevicesList = ActiveDevicesList.getInstance();

        activeDevicesList.clearLists();
        activeDevicesList.addDevice("0.0.0.0", "--Choose device--");

        MulticastRequest multicastRequest = new MulticastRequest();
        multicastRequest.start();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, activeDevicesList.deviceMACList);
        spinner.setAdapter(adapter);

        //listener for spinner containing observable list of active devices
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#cccccc"));
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                String ip = spinner.getSelectedItem().toString();
                if(ip.equals("--Choose device--"))
                    return;
                button.setEnabled(true);
                editText.setText(prefs.getString(ip, ""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editText.setText("");
            }
        });

        //listener for text field containing device name
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);
                    view.clearFocus();

                    return true;
                }
                return false;
            }
        });

        //listener for refresh devices list and search one more time all devices in local network
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveDevicesList.getInstance().clearLists();
                ActiveDevicesList.getInstance().addDevice("0.0.0.0", "--Choose device--");
                MulticastRequest multicastRequest1 = new MulticastRequest();
                multicastRequest1.start();
            }
        });

        //listener for save device button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                SharedPreferences.Editor editor = prefs.edit();

                String newName = editText.getText().toString();
                String macAddr = spinner.getSelectedItem().toString();

                editor.putString(macAddr, newName);
                editor.commit();
                MyApplication.showToast("Device seved!");
            }
        });

        return view;
    }
}
