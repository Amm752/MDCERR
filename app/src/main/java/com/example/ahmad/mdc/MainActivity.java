package com.example.ahmad.mdc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnBluetothc(View v) {
        if (v.getId() == R.id.idBluetoothc) {
            Intent i = new Intent(this , BluetoothC.class);
            startActivity(i);
        }}

    public void btnStatas(View v) {
        if (v.getId() == R.id.idStatas) {
            Intent i = new Intent(this , Statas.class);
            startActivity(i);
        }}
    public void btnAbout(View v) {
        if (v.getId() == R.id.idAbout) {
            Intent i = new Intent(this , About.class);
            startActivity(i);
        }}
    public void btnSettings(View v) {
        if (v.getId() == R.id.idSettings) {
            Intent i = new Intent(this , Settings.class);
            startActivity(i);
        }}
}
