package com.kiit.viper.devoir;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


/**
 * Created by VIPER on 10-Mar-17.
 */

public class ProblemCapture extends Fragment {

    static String mImageFileLocation="";
    ImageView button,problem;
    EditText ed;
    Bitmap photoCaptureBitmap;
    File photoFile = null;
    private Spinner spinner;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;
    private StorageReference mStorageRef;
    public static final int ACTIVITY_START_CAM_APP=0;
   // TextView txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // get the button view
        button = (ImageView) getView().findViewById(R.id.button);
        progressDialog = new ProgressDialog(getContext());
        mStorageRef= FirebaseStorage.getInstance().getReference();
      //  problem = (ImageView) getView().findViewById(R.id.Problem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callCamAppIntent=new Intent();
                callCamAppIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                try
                {
                    photoFile = createImageFile();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                callCamAppIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(callCamAppIntent,ACTIVITY_START_CAM_APP);

            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(getActivity());
        //AlertDialog alert = logoutBuilder.create();
        final Uri uri = Uri.fromFile(photoFile);
        final StorageReference reference= mStorageRef.child("Photos").child(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.problemcapturedialog);
        dialog.setTitle("    Problem Submission");
        // set the custom dialog components - text, image and button
        //ImageView image = (ImageView) alert.findViewById(R.id.Problem1);
        ed=(EditText)dialog.findViewById(R.id.other);
        spinner = (Spinner)dialog.findViewById(R.id.SpinnerFeedbackType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 5:{
                        Toast.makeText(getContext(), "Enter Your Issue", Toast.LENGTH_SHORT).show();
                        ed.setVisibility(View.VISIBLE);
                        break;
                    }
                    default:{

                        ed.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final ImageView image = (ImageView) dialog.findViewById(R.id.Problem1);
        checkBox = (CheckBox)dialog.findViewById(R.id.CheckBoxResponse);
        final ImageView send = (ImageView) dialog.findViewById(R.id.send);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                   // send.setBackgroundColor(Color.WHITE);
                    send.setEnabled(true);
                    send.setAlpha(1f);

                }
                else {
                    send.setEnabled(false);
                    send.setAlpha(0.5f);
                    send.setClickable(false);
                }
            }
        });
        send.setAlpha(0.5f);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                progressDialog.setMessage("Uploading Image ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Submission Done ...",Toast.LENGTH_LONG).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        dialog.show();

        //alert.setTitle("Problem Capture");
        //alert.show();

         String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(getContext(), galleryPermissions)) {
            try {
                 photoCaptureBitmap = BitmapFactory.decodeFile(mImageFileLocation);

                 image.setImageBitmap(photoCaptureBitmap);

            }
            catch (Exception e) {
                e.getMessage();
            }
        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    101, galleryPermissions);
        }

    }

    File createImageFile() throws IOException {


        String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName="Image_"+timeStamp;

        File storageDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image= new File(storageDirectory,fileName+".jpg");

        mImageFileLocation =image.getAbsolutePath();

        return image;
    }
}