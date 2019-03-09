package com.test.menutest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;

public class SettingsFragment extends Fragment {

    View view;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        CheckBox deleteCheckBox = view.findViewById(R.id.deletePhotosAfterSending);
        RadioGroup orderGroud = view.findViewById(R.id.radioGroup);
        final EditText sourceFolder = view.findViewById(R.id.sourceFolder);
        Button sourceFolderButton = view.findViewById(R.id.pickFolder);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = prefs.edit();

        boolean shouldDelete = prefs.getBoolean("shouldDelete", true);
        boolean order = prefs.getBoolean("order", true);
        sourceFolder.setText(prefs.getString("sourceFolder","/sdcard/DCIM/Camera/"));
        editor.commit();

        if(order)
            orderGroud.check(R.id.fromNewestToOldest);
        else orderGroud.check(R.id.fromOldestToNewest);

        if(shouldDelete) deleteCheckBox.setChecked(true);

        deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("shouldDelete", isChecked);
                editor.commit();
            }
        });

        orderGroud.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean checked = (checkedId==R.id.fromNewestToOldest) ? true : false;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("order", checked);
                editor.commit();
            }
        });

        sourceFolder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);
                    view.clearFocus();

                    String folderPath = sourceFolder.getText().toString();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("sourceFolder", folderPath);
                    editor.commit();

                    return true;
                }
                return false;
            }
        });

        sourceFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(i, 2);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1)
        {
            EditText editText = view.findViewById(R.id.sourceFolder);
            editText.setText(data.getData().toString());
        }
    }
}
