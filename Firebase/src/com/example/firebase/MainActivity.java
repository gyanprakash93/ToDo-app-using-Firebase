package com.example.firebase;

import java.io.ByteArrayOutputStream;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	private Firebase mRef;
	private String mUserId;
	private String itemsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap bmp =  BitmapFactory.decodeResource(getResources(), R.drawable.land);//your image
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT); 
        mRef.child("pic").push().setValue(imageFile);
 // Check Authentication
    mRef = new Firebase(Constants.FIREBASE_URL);
    if (mRef.getAuth() == null) {
        loadLoginView();
    }
    try {
        mUserId = mRef.getAuth().getUid();
    } catch (Exception e) {
        loadLoginView();
    }

    itemsUrl = Constants.FIREBASE_URL + "/users/" + mUserId + "/item";

    // Set up ListView
    final ListView listView = (ListView) findViewById(R.id.listView);
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text2);
    listView.setAdapter(adapter);

    // Add items via the Button and EditText at the bottom of the view.
    final EditText text = (EditText) findViewById(R.id.todoText);
    final Button button = (Button) findViewById(R.id.addButton);
    button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            Item item = new Item(text.getText().toString());
            new Firebase(itemsUrl).push().setValue(item);
                    
                    
        }
    });

    // Use Firebase to populate the list.
    new Firebase(itemsUrl)
            .addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    adapter.add((String) dataSnapshot.child("title").getValue());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) 
                {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) 
                {
                    adapter.remove((String) dataSnapshot.child("title").getValue());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s)
                {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) 
                {

                }
            });
 // Delete items when clicked
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            new Firebase(itemsUrl)
                    .orderByChild("title")
                    .equalTo((String) listView.getItemAtPosition(position))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                firstChild.getRef().removeValue();
                            }
                        }

                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
        }
    });
    }
    private void loadLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

