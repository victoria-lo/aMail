package com.gameguildstudios.amail;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NewMailFragment extends Fragment {

    View inflatedView;
    EditText mEmail;
    EditText mSubject;
    EditText mMessage;
    Button sendBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflatedView = inflater.inflate(R.layout.fragment_newmail, container,false);
        return inflatedView;
    }

    public void sendMail() {

        String mail = mEmail.getText().toString().trim();
        String[] mails = mail.split(",");

        String message = mMessage.getText().toString();
        String subject = mSubject.getText().toString().trim();

        //Send Mail
        try {
            if (!mail.matches("") && !subject.matches("")&& !message.matches("")) {
                if (Utils.getEmail().equals("") || Utils.getPassword().equals("")) {
                    Toast.makeText(getContext(),
                            "Please set up your email and app password first!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                for (String email : mails) {
                    mailSend(email, subject, message);
                }
            } else {
                Toast.makeText(getContext(), "Something is left blank.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    protected void mailSend(String email, String subject, String message){
        JavaMailAPI javaMailAPI = new JavaMailAPI(inflatedView.getContext(),
                email, subject, message);

        javaMailAPI.execute();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmail = (EditText) inflatedView.findViewById(R.id.mailID);
        mMessage = (EditText) inflatedView.findViewById(R.id.messageID);
        mSubject = (EditText) inflatedView.findViewById(R.id.subjectID);
        sendBtn = (Button) inflatedView.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        if(MainActivity.send){
            displayReceivedData(MainActivity.mails);
        }
    }

    protected void displayReceivedData(String message)
    {
        mEmail.setText(message);
    }
}
