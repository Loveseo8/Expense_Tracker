package com.akree.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {

    Button done;
    SharedPreferences budget_set;
    EditText amount_edit_text;
    String amount;
    int set;
    DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        databaseReference = FirebaseDatabase.getInstance().getReference("user/" + user.getUid());

        budget_set = getSharedPreferences("set", Context.MODE_PRIVATE);
        set = budget_set.getInt("set", 0);

        if (set == 0) {


            amount_edit_text = findViewById(R.id.amount_edit_text);
            done = findViewById(R.id.done);

            amount = amount_edit_text.getText().toString();

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(amount)) {

                        amount_edit_text.setError("Enter your starting budget!");

                    } else {

                        budget_set.edit().putInt("set", 1).commit();
                        databaseReference.child("budget").setValue(amount);
                        startActivity(new Intent(BaseActivity.this, NavActivity.class));
                        finish();

                    }

                }
            });

        } else {

            startActivity(new Intent(BaseActivity.this, NavActivity.class));
            finish();

        }


    }
}