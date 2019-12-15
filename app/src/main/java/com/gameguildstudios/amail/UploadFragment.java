package com.gameguildstudios.amail;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;

import com.opencsv.CSVReader;

public class UploadFragment extends Fragment {

    View inflatedView;
    ImageButton uploadBtn;
    Intent myFileIntent;
    TextView uploadText;
    Uri u;
    SendMessage SM;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_upload, container,false);
        uploadText = (TextView) inflatedView.findViewById(R.id.uploadText);
        uploadBtn = (ImageButton) inflatedView.findViewById(R.id.uploadBtn);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        return inflatedView;
    }

    //Upload Button that allows user to select an csv file from their storage
    public void uploadFile(){
        myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        myFileIntent.setType("*/*");
        String[] mimetypes = {"text/comma-separated-values", "text/csv"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            myFileIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        startActivityForResult(Intent.createChooser(myFileIntent, "Open CSV"), 10);

    }


    //After uploading the csv file, get the file data
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case 10:
                if(resultCode == Activity.RESULT_OK){
                    u = data.getData();
                    String filePath = getActualPath(this.getActivity(), u);
                    ActivityCompat.requestPermissions(this.getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    proImportCSV(new File(filePath));
                }
        }

    }

    //Custom interface for fragments to interact
    interface SendMessage {
        void sendData(String message);
    }

    //Initialize the interface
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    //Read the csv file and send the emails to NewMailFragment
    private void proImportCSV(File from){
        try {
            CSVReader reader = new CSVReader(new FileReader(from));

            StringBuilder mails = new StringBuilder();
            String[] emails = null;
            while((emails = reader.readNext())!=null){
                for(String email:emails){
                    mails.append(email + ", ");
                }
            }
            SM.sendData(mails.toString());
            Toast.makeText(getContext(), "Emails uploaded. Tap 'New Mail' below.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Get the path of the csv file
    public String getActualPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    //Extract the data from file
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    //Checks the file source
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
