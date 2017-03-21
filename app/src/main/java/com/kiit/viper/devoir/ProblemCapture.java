package com.kiit.viper.devoir;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


/**
 * Created by VIPER on 10-Mar-17.
 */

public class ProblemCapture extends Fragment {

    ImageView button,problem;
    TextView txt;
    static final  int CAM_REQUEST=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // get the button view
        button = (ImageView) getView().findViewById(R.id.button);
        problem = (ImageView) getView().findViewById(R.id.Problem);

        txt = (TextView)getView().findViewById(R.id.text);

        // set a onclick listener for when the button gets clicked
        button.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(),
                        Capture.class);
                startActivity(mainIntent);
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);

                File file = getFile();
                mainIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file));
                startActivityForResult(mainIntent,CAM_REQUEST);
            }
        });



    }

    private File getFile(){
        File folder = new File("sdcard/capture");
        if (!folder.exists()){
            folder.mkdir();
        }
        File imageFile = new File(folder,"img.jpg");
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path="sdcard/capture/img.jpg";
        problem.setImageDrawable(Drawable.createFromPath(path));



    }
}