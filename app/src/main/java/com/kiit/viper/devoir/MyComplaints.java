package com.kiit.viper.devoir;

/**
 * Created by VIPER on 10-Mar-17.
 */
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;
import com.kiit.viper.devoir.FireBase.FireBaseUtil;

import java.util.ArrayList;
import java.util.Arrays;


public class MyComplaints extends Fragment{

    ListView mainListView;
    private ArrayAdapter<String> listAdapter ;
    FirebaseDatabase players;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
        /*mainListView = (ListView) rootView.findViewById( R.id.mainListView );

        players.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot player : dataSnapshot.getChildren()) {
                    Log.i("player", player.getKey());
                    friends.add(player.getKey());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,
                        friends);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        ArrayList<String> list = new ArrayList<String>();
        list.addAll( Arrays.asList(Fire) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

        // Add more planets. If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.
        listAdapter.add( "Ceres" );
        listAdapter.add( "Pluto" );
        listAdapter.add( "Haumea" );
        listAdapter.add( "Makemake" );
        listAdapter.add( "Eris" );

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );*/
        return rootView;
    }


}
