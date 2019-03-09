package com.test.menutest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class NewPhotoFragment extends Fragment {

    View view;
    ImageView image;
    FloatingActionButton retake;
    FloatingActionButton send;
    Spinner spinner;
    String path = "";
    Boolean isPicturetoDelete = false;
    Button refreshButton;
    //ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.after_taking_photo, container, false);

        image = view.findViewById(R.id.photoPreview);
        retake = view.findViewById(R.id.photoRetake);
        send = view.findViewById(R.id.photoSend);
        spinner = view.findViewById(R.id.spinner);
        refreshButton = view.findViewById(R.id.refresh);
        //progressBar = view.findViewById(R.id.progressBar);

        //clear list to avoid devices that used to be active in the past being showed in the list
        ActiveDevicesList activeDevicesList = ActiveDevicesList.getInstance();
        activeDevicesList.clearLists();
        activeDevicesList.addDevice("0.0.0.0", "--Choose device--");

        //Start the multicast process to find active devices
        final MulticastRequest multicastRequest = new MulticastRequest();
        multicastRequest.start();

        //match list to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, activeDevicesList.devicesList);
        spinner.setAdapter(adapter);

        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPicturetoDelete) {
                    MyApplication.deletePhoto(path);
                    isPicturetoDelete = false;
                }
                newPhoto();
            }
        });

        send.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                try {
                    //progressBar.setVisibility(View.VISIBLE);
                    if(!isPicturetoDelete) MyApplication.showToast("Retake photo.");
                    else if(spinner.getSelectedItem()==null || spinner.getSelectedItem().toString().equals("--Choose device--"))
                        MyApplication.showToast("Choose device.");
                    else
                        sendPhoto();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#cccccc"));
                String serverName = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveDevicesList.getInstance().clearLists();
                ActiveDevicesList.getInstance().addDevice("0.0.0.0", "--Choose device--");
                MulticastRequest multicastRequest1 = new MulticastRequest();
                multicastRequest1.start();
            }
        });

        newPhoto();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1) {
            isPicturetoDelete = true;
            path = takeLastPhoto();
        }
    }

    private void newPhoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null)
            startActivityForResult(intent, 1);
    }

    private void sendPhoto() throws InterruptedException {
        String serverName = spinner.getSelectedItem().toString();
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
        Client client = new Client(ip, 1313, path, true);
        client.start();

        while(client.isAlive()) TimeUnit.SECONDS.sleep(1);
        isPicturetoDelete = false;
        MyApplication.showToast("Photo sent!");
        //progressBar.setVisibility(View.INVISIBLE);
    }

    private String takeLastPhoto()
    {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.ORIENTATION
        };

        String imageLocation = "";
        final Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

// Put it in the image view
        if (cursor.moveToFirst()) {
            imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(imageLocation);

                //Scale the picture
                int viewHeight = image.getHeight();
                int viewWidth = image.getWidth();
                int bmHeight = bm.getHeight();
                int bmWidth = bm.getWidth();
                float scale;

                //is photo horizontal
                if(bmHeight < bmWidth)
                {
                    scale = (float)viewWidth/(float)bmWidth;
                }
                else
                {
                    scale = (float)viewHeight/(float)bmHeight;
                }

                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                matrix.postRotate(cursor.getInt(5));
                Bitmap scaledBitmap = Bitmap.createBitmap(bm, 0, 0, bmWidth, bmHeight, matrix, true);

                BitmapDrawable result = new BitmapDrawable(scaledBitmap);

                image.setImageDrawable(result);
            }
        }
        return imageLocation;
    }

    @Override
    public void onDestroy() {
        if(isPicturetoDelete)
            MyApplication.deletePhoto(takeLastPhoto());
        super.onDestroy();
    }
}
