package com.example.arduinosensors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button btnOn, btnOff;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3;
    Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);
        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1 = (TextView) findViewById(R.id.sensorView1);
        sensorView2 = (TextView) findViewById(R.id.sensorView2);
        sensorView3 = (TextView) findViewById(R.id.sensorView3);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {										//if message is what we want
                    int[] readMessage = new int[4];
                    readMessage[0] = Integer.parseInt((String) msg.obj);                                                                // msg.arg1 = bytes from connect thread
                    readMessage[1] = Integer.parseInt((String) msg.obj);
                    readMessage[2] = Integer.parseInt((String) msg.obj);
                    readMessage[3] = Integer.parseInt((String) msg.obj);
                    //recDataString.append(readMessage);      								//keep appending to string until ~
                    //int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (readMessage[3] != -1) {                                           // make sure there data before ~
                        //String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        for(int i=0; i<4;i++){
                            txtString.setText("Data Received = ");
                           // int dataLength = dataInPrint.length();							//get length of data received
                            //.setText("String Length = " + String.valueOf(dataLength));
                        }
                        //if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                        //{
                            //String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            //String sensor1 = recDataString.substring(6, 10);            //same again...
                            //String sensor2 = recDataString.substring(11, 15);
                            //String sensor3 = recDataString.substring(16, 20);

                            sensorView0.setText(" Sensor 0 Voltage = " + readMessage[0] + "V");	//update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Voltage = " + readMessage[1] + "V");
                            sensorView2.setText(" Sensor 2 Voltage = " + readMessage[2] + "V");
                            sensorView3.setText(" Sensor 3 Voltage = " + readMessage[3] + "V");
                        //}
                        //recDataString.delete(0, recDataString.length()); 					//clear all string data
                        // strIncom =" ";
                        //dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();


        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int[] dataRequestPacketPart1 = {0x00, 0x01, 0x02, 0x03}; //Version 0, Type 1 - DataRequest, Checksum, Reserved
                int[] dataRequestPacketPart2 = {0x00, 0x03, 0x00, 0x00}; //16bit length (in this case its saying 3 bytes), 16 bit reserved
                int[] dataRequestPacketPart3 = {0x02, 11, 12, 0x00}; //Starting row 700
                int[] dataRequestPacketPart4 = {0x00, 0x00, 0x00, 0x00}; // 16bit datachecksum, 16bit reserved
                mConnectedThread.write(dataRequestPacketPart1);
                mConnectedThread.write(dataRequestPacketPart2);
                mConnectedThread.write(dataRequestPacketPart3);
                mConnectedThread.write(dataRequestPacketPart4);
                /*
               mConnectedThread.write(0x00);    // Version 0
                mConnectedThread.write(0x01);    // Type 1 - Data Request
                mConnectedThread.write(0x02);    // Checksum
                mConnectedThread.write(0x03);    // Reserved
                mConnectedThread.write(0x04);    // length1
                mConnectedThread.write(0x05);    // length2
                mConnectedThread.write(0x06);    // reserved1
                mConnectedThread.write(0x07);    // reserved1
                */
                Toast.makeText(getBaseContext(), "PACKET SENT v3", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write(0x01);    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write(0x00);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(int[] input) {
            byte[] msgBuffer = new byte[4];
            for(int i=0; i<4; i++){
                msgBuffer[i] = (byte)input[i];
            }
            //byte msgBuffer;
            //msgBuffer = (byte) (input);
            //msgBuffer[2] = (byte) ((input >> 8) & 0xFF);
            //msgBuffer[1] = (byte) ((input >> 16) & 0xFF);
            //msgBuffer[0] = (byte) ((input >> 24) & 0xFF);
            //byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                for(int i = 0; i<4; i++){
                    mmOutStream.write(msgBuffer[i]);                //write bytes over BT connection via outstream
                }

            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}