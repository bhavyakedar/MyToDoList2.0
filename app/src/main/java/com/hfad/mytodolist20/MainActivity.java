package com.hfad.mytodolist20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    EditText task;
    Button addTask, logout, shareList;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference taskNameRef, taskDescriptionRef, userRef;
    ArrayList<String> tasksArray = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = findViewById(R.id.task);
        addTask = findViewById(R.id.addTask);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null)
        {
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
            finish();
        }
        Toast.makeText(this,"Welcom to MyToDoList2.0, "+firebaseUser.getDisplayName(),Toast.LENGTH_SHORT).show();
        userName = firebaseUser.getUid();
        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tasksArray);
        userRef = database.getReference().child("user").child(userName);
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChild("taskName"))
                {
                    tasksArray.add(dataSnapshot.child("taskName").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("taskName"))
                {
                    tasksArray.remove(dataSnapshot.child("taskName").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setAdapter(arrayAdapter);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(task.getText().toString().trim().isEmpty())
                {
                    addTask.setEnabled(false);
                }
                else
                {
                    addTask.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        task.addTextChangedListener(textWatcher);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskNameRef = database.getReference().child("user").child(userName).child(task.getText().toString().trim()).child("taskName");
                taskNameRef.setValue(task.getText().toString().trim());
                taskDescriptionRef = database.getReference().child("user").child(userName).child(task.getText().toString().trim()).child("taskDescription");
                taskDescriptionRef.setValue("");
                task.setText("");
            }
        });
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String)parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this,TaskDetailActivity.class);
                intent.putExtra("taskName",s);
                startActivity(intent);
            }
        });
        shareList = findViewById(R.id.shareList);
        shareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tasks = "";
                for (int j = 0; j < tasksArray.size(); j++) {
                    tasks = tasks +(j+1)+") "+tasksArray.get(j)+"\n";
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,tasks);
                startActivity(intent);
            }
        });
    }
}
