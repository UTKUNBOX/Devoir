package com.kiit.viper.devoir;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ArrayAdapter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kiit.viper.devoir.FireBase.FireBaseUtil;
import com.kiit.viper.devoir.model.Issues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


/**
 * Created by VIPER on 10-Mar-17.
 */

public class ProblemCapture extends Fragment {

    static String mImageFileLocation="";
    ImageView button;
    EditText ed,description;
    TextView  textView;
    Bitmap photoCaptureBitmap;
    File photoFile = null;
    private Spinner spinner;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;
    private StorageReference mStorageRef;
    public static final int ACTIVITY_START_CAM_APP=0;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    //Issues issues;
    String address_final;

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

       /* locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                getLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        mStorageRef= FirebaseStorage.getInstance().getReference();
      //  problem = (ImageView) getView().findViewById(R.id.Problem);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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
        textView= (TextView) dialog.findViewById(R.id.location);
        ed=(EditText)dialog.findViewById(R.id.other);
        description = (EditText) dialog.findViewById(R.id.descriptionEditText);
        spinner = (Spinner)dialog.findViewById(R.id.issueTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.feedbacktypelist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:{
                        ed.setText("Dead Animal(s)");
                        break;
                    }
                    case 1:{
                        ed.setText("Dustbins not Cleaned");
                        break;
                    }
                    case 2:{
                        ed.setText("Garbage Dump");
                        break;
                    }
                    case 3:{
                        ed.setText("No Electricity");
                        break;
                    }
                    case 4:{
                        ed.setText("No Water Supply");
                        break;
                    }
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
                saveIssue(reference);
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

    private void getLocation() throws IOException {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double lati = location.getLatitude();
                double longi = location.getLongitude();
                //latitude = String.valueOf(lati);
                //longitude = String.valueOf(longi);
                /*address_final=lati+","+longi;
                    textView.setText(address_final);*/
                //getAddress(lati,longi);


                //progressBar.setVisibility(View.GONE);

            }
            else  if (location1 != null) {
                double lati = location1.getLatitude();
                double longi = location1.getLongitude();
                address_final=lati+","+longi;
                textView.setText(address_final);
               //getAddress(lati,longi);
                // textView.setText(address);


            }
            else  if (location2 != null) {
                double lati = location2.getLatitude();
                double longi = location2.getLongitude();
                address_final=lati+","+longi;
                textView.setText(address_final);
                //getAddress(lati,longi);


            }
            else{

                Toast.makeText(getActivity(),"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }




    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    void getAddress(double lat,double longi) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, longi, 1);

        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        System.out.print("City"+city);
        if(city.equals(null)){
            address_final="Bhubaneswar";
        }
        else{address_final = address+city+state;}

        //textView.setText(city);
    }

    public void saveIssue(final StorageReference reference){
        final Activity activity=   getActivity();
        final String desc=description.getText().toString();
        final String issueType=ed.getText().toString();
        final String lat_longi=textView.getText().toString();

        if(checkNetwork()){
            final Issues issue=new Issues();
            issue.setDescription(desc);
            issue.setIssueType(issueType);
            issue.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
            issue.setUserName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            issue.setLatlongi(lat_longi);
            issue.setImageUrl(reference.toString());
            FireBaseUtil.addTaskToFireBase(activity, issue, new FireBaseUtil.ChangeListener() {
                @Override
                public void onchange(String issueID) {
                    issue.setDescription(desc);
                    issue.setIssueType(issueType);
                    issue.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    issue.setUserName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    issue.setLatlongi(lat_longi);
                    issue.setImageUrl(reference.toString());
                    FireBaseUtil.addTaskToUser(activity, issueID);
                }
            });
        }

    }

    private boolean checkNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }

}