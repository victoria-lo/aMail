package com.gameguildstudios.amail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    View inflatedView;
    Button saveBtn;
    EditText email;
    EditText appPass;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_settings, container,false);
        saveBtn = (Button)inflatedView.findViewById(R.id.saveBtn);
        email = (EditText)inflatedView.findViewById(R.id.email_input);
        appPass = (EditText)inflatedView.findViewById(R.id.ap_input);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
            }
        });
        return inflatedView;
    }

    public void saveInfo(){
        Utils.setEmail(email.getText().toString().trim());
        Utils.setPassword(appPass.getText().toString().trim());
        Toast.makeText(getContext(),"Information Saved", Toast.LENGTH_SHORT).show();
    }
}
