package com.example.firebase;

import com.firebase.client.Firebase;

import android.app.Application;

	public class ToDoApplication extends Application {

	    @Override
	    public void onCreate() {
	        super.onCreate();

	        Firebase.setAndroidContext(this);
	    }
}
