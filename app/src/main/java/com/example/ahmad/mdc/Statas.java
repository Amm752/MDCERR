package com.example.ahmad.mdc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ahmad on 2/27/2016.
 */
public class Statas extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statas);
    }
    public void btnHome(View v) {
        if (v.getId() == R.id.idBack) {
            Intent i = new Intent(this , MainActivity.class);
            startActivity(i);
        }}
}
