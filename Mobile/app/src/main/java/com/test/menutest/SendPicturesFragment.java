package com.test.menutest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 * This fragment is responsible for sending photos directly to selected device in the same loocal network which is online.
 */
public class SendPicturesFragment extends Fragment implements Observer {

    View view;
    Spinner serverChooser;
    EditText memText;
    String path;
    boolean order;
    boolean shouldDelete;
    private ProgressDialog progressDialog;
    Handler handler;
    Button refreshButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_send_pictures, container, false);

        memText = view.findViewById(R.id.memmoryAmount);
        final SeekBar memSeek = view.findViewById(R.id.seekBar);

        serverChooser = view.findViewById(R.id.serverChoose);
        final Button button = view.findViewById(R.id.startButton);
        refreshButton = view.findViewById(R.id.refreshButton);

        //get all data from shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        path = prefs.getString("sourceFolder", "");
        shouldDelete = prefs.getBoolean("shouldDelete", false);
        order = prefs.getBoolean("order", true);

        //clear list to avoid devices that used to be active in the past being showed in the list
        ActiveDevicesList activeDevicesList = ActiveDevicesList.getInstance();
        activeDevicesList.clearLists();
        activeDevicesList.addDevice("0.0.0.0", "--Choose device--");

        //Start the multicast process to find active devices
        final MulticastRequest multicastRequest = new MulticastRequest();
        multicastRequest.start();

        //match list to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, activeDevicesList.devicesList);
        serverChooser.setAdapter(adapter);

        //listener for deleting focus for text field after writing amount of mem to send in MB
        memText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        //listener for setting memmory size to send for the seekbar
        memSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(memText.isFocused()) return;
                memText.setText(memSeek.getProgress()*2+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //choosing device from the list, it checks too if button send should be enabled or disables
        serverChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#cccccc"));
                String serverName = serverChooser.getSelectedItem().toString();
                //this is the default value, it it's set it means
                //that there is no other device in local network or user didn't pick any from the list
                if(serverName.equals("--Choose device--"))
                {
                    button.setEnabled(false);
                    return;
                }
                button.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                button.setEnabled(false);
            }
        });

        //refresh button is there to refresh list of active device.
        //after pressing it mobile device is searching devices one more time.
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveDevicesList.getInstance().clearLists();
                ActiveDevicesList.getInstance().addDevice("0.0.0.0", "--Choose device--");
                MulticastRequest multicastRequest1 = new MulticastRequest();
                multicastRequest1.start();
            }
        });

        //listener for setting memmory size to send for text field
        memText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!memText.isFocused()) return;
                String val = memText.getText().toString();
                int value;
                if(val.length()==0) value = 0;
                    else value = Integer.parseInt(val);
                if(value > 200) memSeek.setProgress(100);
                    else memSeek.setProgress(value/2);
            }
        });

        //After pressing send button, pop-up with progress and sending in other thread
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int sizeToSend = Integer.parseInt(memText.getText().toString());
                button.setVisibility(view.INVISIBLE);
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMax(sizeToSend);
                progressDialog.setMessage("Sending pictures...");
                progressDialog.setTitle("Already sent MB:");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();

                //thread for sending photos
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        double sizeToSend = (double)Integer.parseInt(memText.getText().toString());
                        double alreadySent = 0;
                        String serverName = serverChooser.getSelectedItem().toString();
                        String ip="";

                        ActiveDevicesList list = ActiveDevicesList.getInstance();
                        Iterator<String> iterator = list.devicesList.iterator();
                        Iterator<String> iterator1 = list.deviceIPList.iterator();

                        while (iterator.hasNext() && iterator1.hasNext())
                        {
                            String nameServ = iterator.next();
                            String ipServ = iterator1.next();
                            if(nameServ.equals(serverName))
                            {
                                ip = ipServ;
                                break;
                            }
                        }

                        //setting projection for getting photos by query
                        String[] projection = new String[]{
                                MediaStore.Images.ImageColumns._ID,
                                MediaStore.Images.ImageColumns.DATA,
                                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                MediaStore.Images.ImageColumns.DATE_TAKEN,
                                MediaStore.Images.ImageColumns.MIME_TYPE
                        };

                        String orderString="";
                        String imageLocation;
                        if(order)
                            orderString = " DESC";
                        String[] args = {path+"%"};

                        //cursor with all the photos from source folder in correct order inc or desc
                        Cursor cursor = getContext().getContentResolver()
                                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,  MediaStore.Images.ImageColumns.DATA + " LIKE ?",
                                        args, MediaStore.Images.ImageColumns.DATE_TAKEN + orderString);

                        //sending photos
                        if(cursor.moveToFirst())
                        do {
                            imageLocation = cursor.getString(1);
                            File imageFile = new File(imageLocation);
                            if(imageFile.exists())
                            {
                                //send + progress bar update, eventually deleting just send photo
                                double size = ((double)imageFile.length())/1024.0/1024.0;
                                try {
                                        Client client = new Client(ip ,1313, imageLocation, shouldDelete);
                                        client.start();
                                        client.join();
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    alreadySent+=size;
                                    Message msg = new Message();

                                    Bundle bundle = new Bundle();
                                    bundle.putDouble("size", alreadySent);
                                    msg.setData(bundle);

                                    handler.sendMessage(msg);
                                    cursor.moveToNext();
                            }
                        }while(sizeToSend - alreadySent > 0);
                        Bundle bundle = new Bundle();
                        bundle.putString("end", "koniec");
                        Message msg = new Message();
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();


                //progress bar updating and showing information that all photos were sent correctly
                handler = new Handler()
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        super.handleMessage(msg);
                        String string = msg.getData().getString("end");
                        if(string!=null)
                        {
                            progressDialog.hide();
                            MyApplication.showToast("Photos sent!");
                        }
                        else
                            progressDialog.setProgress((int)msg.getData().getDouble("size"));
                    }
                };
            }
        });

        return view;
    }

    @Override
    public void update(Observable o, Object arg) {
        //update list of active devices if possible
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) serverChooser.getAdapter();
        adapter.notifyDataSetChanged();
    }
}
