package com.akree.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BaseActivity extends AppCompatActivity {

    Button done;
    EditText amount_edit_text;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        amount_edit_text = findViewById(R.id.amount_edit_text);
        done = findViewById(R.id.done);

        amount = amount_edit_text.getText().toString();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(amount)){

                    amount_edit_text.setError("Enter your starting budget!");

                } else{

                    startActivity(new Intent(BaseActivity.this, NavActivity.class));
                    finish();

                }

            }
        });


    }
}