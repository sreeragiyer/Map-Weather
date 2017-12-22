package com.example.ramesh.mapweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText un;
    EditText pw;
    Button k;
    TextView su;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        un =(EditText) findViewById(R.id.username);
        k=(Button) findViewById(R.id.ok);
    }

    public void login(View view)
    {
        String name=un.getText().toString();
        if(name!="") {
            Toast.makeText(getApplicationContext(), "Hello!", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(this, MapsActivity.class);
            startActivity(in);
        }
        else
            Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_SHORT).show();


    }

}
