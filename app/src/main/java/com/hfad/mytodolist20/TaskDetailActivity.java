package com.hfad.mytodolist20;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetailActivity extends AppCompatActivity {

    Intent intent;
    EditText taskDescription;
    Button deleteTask, save;
    TextView taskName;
    String taskname, userId;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference taskDescriptionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userId = firebaseUser.getUid();
        taskName = findViewById(R.id.taskName);
        intent = getIntent();
        taskname = intent.getStringExtra("taskName");
        taskName.setText(taskname);
        taskDescription = findViewById(R.id.taskDescription);
        deleteTask = findViewById(R.id.deleteTask);
        save = findViewById(R.id.save);
        firebaseDatabase.getReference().child("user").child(userId).child(taskname).child("taskDescription").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskDescription.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(taskDescription.getText().toString().trim().isEmpty())
                {
                    save.setEnabled(false);
                }
                else
                {
                    save.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        taskDescription.addTextChangedListener(textWatcher);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDescriptionRef = firebaseDatabase.getReference().child("user").child(userId).child(taskname).child("taskDescription");
                taskDescriptionRef.setValue(taskDescription.getText().toString().trim());
                Toast.makeText(TaskDetailActivity.this,"Task Description Saved Successfully.",Toast.LENGTH_SHORT).show();
            }
        });
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.getReference().child("user").child(userId).child(taskname).removeValue();
                Intent intent = new Intent(TaskDetailActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
