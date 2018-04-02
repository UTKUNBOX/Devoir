package com.kiit.viper.devoir.FireBase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

/**
 * Created by sukesh panwar on 09-12-2017.
 */

public class FireBaseUtil {

    private static String key;
    // String key;
    private static DatabaseReference mDatabase;

    private static ShortRoidPreferences shortRoidPreferences;
    private static final String TAG = FireBaseUtil.class.getName();

    public static boolean addTaskToUser(Activity activity, final String issueID)
    {
        try {
            String user=FirebaseAuth.getInstance().getCurrentUser().getUid();

            final DatabaseReference mDatabases=FirebaseDatabase.getInstance().getReference("users").
                    child(user).child("issueIDs");
            mDatabases.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("qwerty",dataSnapshot.toString());
                    String s=(String)dataSnapshot.getValue();
                    if(s!=null) {
                        if (s.contentEquals(","))
                            s = "";
                        s = s + issueID + ",";
                        mDatabases.setValue(s);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG,databaseError.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }
    public static boolean addTaskToFireBase(final Activity activity, final com.kiit.viper.devoir.model.Issues task, final ChangeListener changeListener)
    {
        mDatabase= FirebaseDatabase.getInstance().getReference("issues");
        final String taskId = mDatabase.push().getKey();
        task.setIssueID(taskId);
        mDatabase.child(taskId).setValue(task, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                changeListener.onchange(databaseReference.getKey());
                Log.d("qwerty",databaseReference.getKey());
            }
        });
        return true;

    }

    public interface ChangeListener{
        void onchange(String issueID);
    }
}
