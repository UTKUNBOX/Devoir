package com.kiit.viper.devoir;

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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * Created by VIPER on 10-Mar-17.
 */

public class ProblemCapture extends Fragment {

    static String mImageFileLocation="";
    ImageView button,problem;
    public static final int ACTIVITY_START_CAM_APP=0;
    TextView txt;
    //static final  int CAM_REQUEST=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //problem = (ImageView) getView().findViewById(R.id.Problem);
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
     /*   button.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(),
                        Capture.class);
                startActivity(mainIntent);
            }
        });*/
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
           /* public void onClick(View view) {

                Intent mainIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File file = null;
                try {
                    file = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mainIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider", file));

                startActivityForResult(mainIntent,CAM_REQUEST);
            }
*/
            public void onClick(View v)
            {
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

   /* private File getFile(){
        File folder = new File("sdcard/capture");
        if (!folder.exists()){
            folder.mkdir();
        }
        File imageFile = new File(folder,"img.jpg");
        return imageFile;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // String path="sdcard/capture/img.jpg";
        //problem.setImageDrawable(Drawable.createFromPath(mImageFileLocation));
        if(requestCode==ACTIVITY_START_CAM_APP && resultCode==RESULT_OK)
        {
            //Intent callPicHolderIntent = new Intent(this,PicHolder.class);
            // startActivity(callPicHolderIntent);
            Bitmap photoCaptureBitmap = BitmapFactory.decodeFile(mImageFileLocation);
            problem.setImageBitmap(photoCaptureBitmap);
           /* Bundle extra= data.getExtras();
            Bitmap photoCaptureBitmap= (Bitmap) extra.get("data");
            imageView.setImageBitmap(photoCaptureBitmap);*/

        }
    }

    File createImageFile() throws IOException {


        String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName="Image_"+timeStamp;

        File storageDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image= File.createTempFile(fileName,".jpg",storageDirectory);

        mImageFileLocation =image.getAbsolutePath();

        return image;
    }
}