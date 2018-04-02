package com.kiit.viper.devoir;

import android.content.Context;
import android.support.v4.app.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Created by VIPER on 10-Mar-17.
 */

public class Feeds extends Fragment {

    ListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main3, container, false);

        return rootView;
    }


    /*class FeedsAdapter extends ArrayAdapter<String>
    {
           FeedsAdapter(Context context, String[] titles)
           {
               super(context,R.layout.feeds_row, );
           }
    }*/
}
