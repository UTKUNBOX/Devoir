package com.kiit.viper.devoir;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
      //  problem = (ImageView) getView().findViewById(R.id.Problem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callCamAppIntent=new Intent();
                callCamAppIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
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
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.problemcapturedialog);
        dialog.setTitle("Problem Submission");

        // set the custom dialog components - text, image and button
        //ImageView image = (ImageView) alert.findViewById(R.id.Problem1);
        ImageView image = (ImageView) dialog.findViewById(R.id.Problem1);

        dialog.show();

        //alert.setTitle("Problem Capture");
        //alert.show();

         String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(getContext(), galleryPermissions)) {
            try {
                Bitmap photoCaptureBitmap = BitmapFactory.decodeFile(mImageFileLocation);

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