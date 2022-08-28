package com.cotulars.foxybot;

import com.google.firebase.database.*;
import com.infinitys.logger.Log;

import java.awt.*;

public class Database {
    private static DatabaseReference mDatabase;

    public interface OnTaskComplete{
        void onComplete();
    }

    public static void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public interface OnGetUserComplete {
        void onComplete(User user);
    }

    public static void getUser(String guild, String user, OnGetUserComplete listener){
        DatabaseReference userRef = mDatabase.child("guilds").child(guild).child("users").child(user).getRef();

        ValueEventListener _listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    User user = snapshot.getValue(User.class);
                    listener.onComplete(user);
                } catch (Exception e){
                    Log.e("GetUser", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("GetUser", error.getMessage());
            }
        };

        mDatabase.child("guilds").child(guild).child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(user).exists()){
                    userRef.addListenerForSingleValueEvent(_listener);
                } else {
                    listener.onComplete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("GetUser-check", error.getMessage());
            }
        });
    }

    public static void pushUser(String guild, User user){
        mDatabase.child("guilds").child(guild).child("users").child(user.id).getRef().setValueAsync(user);
    }
    public static void pushUser(String guild, User user, OnTaskComplete listener){
        mDatabase.child("guilds").child(guild).child("users").child(user.id).getRef().setValue(user, ((error, ref) -> {
            listener.onComplete();
        }));
    }

}
