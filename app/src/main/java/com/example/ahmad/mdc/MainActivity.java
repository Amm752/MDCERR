package com.example.ahmad.mdc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends Activity {

	private static final String TAG = "BlueTest5-MainActivity";
	private int mMaxChars = 500000000;//Default
	private UUID mDeviceUUID;
	private BluetoothSocket mBTSocket;
	private ReadInput mReadThread = null;
	private boolean mIsUserInitiatedDisconnect = false;

	// All controls here
	private TextView mTxtReceive;
	private EditText mEditSend;
	private Button mBtnDisconnect;
	private Button mBtnSend;
	private Button mBtnClear;
	private Button mBtnClearInput;
	private ScrollView scrollView;
	private CheckBox chkScroll;
	private CheckBox chkReceiveText;

	DatabaseHelper mydb;
    //EditText ed;


    private boolean mIsBluetoothConnected = false;

	private BluetoothDevice mDevice;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//startService(new Intent(this, MyService.class));
		Button btnSync, btnRequest, btnStop, btnCollect, btnQuery, btnDelete;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityHelper.initialize(this);

        //ed = (EditText) findViewById(R.id.contenttxt);

        mydb = new DatabaseHelper(this);

		btnRequest = (Button) findViewById(R.id.buttonRequest);
		btnSync = (Button) findViewById(R.id.buttonSync);
		btnStop = (Button) findViewById(R.id.buttonStop);
		btnCollect = (Button) findViewById(R.id.buttonCollect);
		btnQuery = (Button) findViewById(R.id.buttonQuery);
		btnDelete = (Button) findViewById(R.id.buttonDelete);
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		mDevice = b.getParcelable(Homescreen.DEVICE_EXTRA);
		mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
		mMaxChars = b.getInt(Homescreen.BUFFER_SIZE);

		Log.d(TAG, "Ready");

		mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnClear = (Button) findViewById(R.id.btnClear);
		mTxtReceive = (TextView) findViewById(R.id.txtReceive);
		mEditSend = (EditText) findViewById(R.id.editSend);
		scrollView = (ScrollView) findViewById(R.id.viewScroll);
		chkScroll = (CheckBox) findViewById(R.id.chkScroll);
		chkReceiveText = (CheckBox) findViewById(R.id.chkReceiveText);
		mBtnClearInput = (Button) findViewById(R.id.btnClearInput);

		mTxtReceive.setMovementMethod(new ScrollingMovementMethod());

		mBtnDisconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsUserInitiatedDisconnect = true;
				new DisConnectBT().execute();
			}
		});

		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					mBTSocket.getOutputStream().write(mEditSend.getText().toString().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		mBtnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mEditSend.setText("");
			}
		});
		
		mBtnClearInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTxtReceive.setText("");
			}
		});


		btnRequest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				long epochTime = 1459399388000L;
				ByteBuffer buffer = ByteBuffer.allocate(8);
				buffer.putLong(epochTime);
				int[] dataRequestPacketPart1 = {0x00, 0x01, 0x02, 0x03}; //Version 0, Type 1 - DataRequest, Checksum, Reserved
				int[] dataRequestPacketPart2 = {0x00, 11, 0x00, 0x00}; //16bit length (in this case its saying 3 bytes), 16 bit reserved
				int[] dataRequestPacketPart3 = {0, 0, 0, 0}; //Starting row 700 4 8 12 16
				//int[] dataRequestPacketPart4 = {2, 11, 10, 11}; //Starting row 700 20 24 28 32
				//int[] dataRequestPacketPart4 = {3, 5, 1, 0}; //Starting row 700 36 40 44 48
				int[] dataRequestPacketPart4 = {0x00, 0x00, 0x00, 0x00}; // 16bit datachecksum, 16bit reserved
				byte[] msgBuffer1 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer1[i] = (byte)dataRequestPacketPart1[i];
				}
				byte[] msgBuffer2 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer2[i] = (byte)dataRequestPacketPart2[i];
				}
				byte[] msgBuffer3 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer3[i] = (byte)dataRequestPacketPart3[i];
				}
				byte[] msgBuffer4 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer4[i] = (byte)dataRequestPacketPart4[i];
				}
				try {
					mBTSocket.getOutputStream().write(msgBuffer1);
					mBTSocket.getOutputStream().write(msgBuffer2);
					mBTSocket.getOutputStream().write(buffer.array());
					mBTSocket.getOutputStream().write(msgBuffer3);
					mBTSocket.getOutputStream().write(msgBuffer4);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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

		btnSync.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				long epochTime = System.currentTimeMillis();
				ByteBuffer buffer = ByteBuffer.allocate(8);
				buffer.putLong(epochTime);
				//txtString.setText("Data Received = " + epochTime);
				long[] dataRequestPacketPart1 = {epochTime, 0x00, 0x00, 0x00};
				//int[] dataRequestPacketPart2 = {0x00, 0x03, 0x00, 0x00}; //16bit length (in this case its saying 3 bytes), 16 bit reserved
				//int[] dataRequestPacketPart3 = {0x02, 11, 12, 0x00}; //Starting row 700
				//int[] dataRequestPacketPart4 = {0x00, 0x00, 0x00, 0x00}; // 16bit datachecksum, 16bit reserved
				//mBTSocket.sendSync(epochTime);
				try {
					mBTSocket.getOutputStream().write(buffer.array());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// mConnectedThread.write(dataRequestPacketPart2);
				//mConnectedThread.write(dataRequestPacketPart3);
				//mConnectedThread.write(dataRequestPacketPart4);
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
				Toast.makeText(getBaseContext(), "Sync Time", Toast.LENGTH_SHORT).show();
			}
		});

		btnStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int[] sleepPacketPart1 = {0x00, 0x03, 0x00, 0x00}; //Version 0, Type 1 - DataRequest, Checksum, Reserved
				int[] sleepPacketPart2 = {0x00, 0x03, 0x00, 0x00}; //16bit length (in this case its saying 3 bytes), 16 bit reserved
				byte[] msgBuffer1 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer1[i] = (byte)sleepPacketPart1[i];
				}
				byte[] msgBuffer2 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer2[i] = (byte)sleepPacketPart2[i];
				}
				//mBTSocket.write(sleepPacketPart1);
				//mBTSocket.write(sleepPacketPart2);
				try {
					mBTSocket.getOutputStream().write(msgBuffer1);
					mBTSocket.getOutputStream().write(msgBuffer2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getBaseContext(), "Sleep", Toast.LENGTH_SHORT).show();
			}
		});
		btnCollect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int[] collectPacketPart1 = {0x00, 0x04, 0x00, 0x00}; //Version 0, Type 1 - DataRequest, Checksum, Reserved
				int[] collectPacketPart2 = {0x00, 0x03, 0x00, 0x00}; //16bit length (in this case its saying 3 bytes), 16 bit reserved
				byte[] msgBuffer1 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer1[i] = (byte)collectPacketPart1[i];
				}
				byte[] msgBuffer2 = new byte[4];
				for(int i=0; i<4; i++){
					msgBuffer2[i] = (byte)collectPacketPart2[i];
				}
				//mBTSocket.write(collectPacketPart1);
				//mBTSocket.write(collectPacketPart2);
				try {
					mBTSocket.getOutputStream().write(msgBuffer1);
					mBTSocket.getOutputStream().write(msgBuffer2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getBaseContext(), "Started Data Collection", Toast.LENGTH_SHORT).show();
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                mydb.deleteDatabase();


			}
		});
		btnQuery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                //Cursor c = mydb.queryLast();
				String str = "";
                //String result = c.;
				Cursor c = mydb.getAllData();
				//c.moveToFirst();
				int count = (c.getCount());
                /*
				while(c.moveToNext()){
					str = (c.getString(c.getColumnIndex("SYNC")));
					//count--;
					final String strInput = str;


					if (chkReceiveText.isChecked()) {
						mTxtReceive.post(new Runnable() {
							@Override
							public void run() {
								mTxtReceive.append(strInput + "\n");

								int txtLength = mTxtReceive.getEditableText().length();
								if(txtLength > mMaxChars){
									mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
								}

								if (chkScroll.isChecked()) { // Scroll only if this is checked
									scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
										@Override
										public void run() {
											scrollView.fullScroll(View.FOCUS_DOWN);
										}
									});
								}
							}
						});
					}
				}*/



				Toast.makeText(getBaseContext(), Integer.toString(count), Toast.LENGTH_SHORT).show();
			}
		});


	}



	private class ReadInput implements Runnable {

		private boolean bStop = false;
		private Thread t;

		public ReadInput() {
			t = new Thread(this, "Input Thread");
			t.start();
		}

		public boolean isRunning() {
			return t.isAlive();
		}

		@Override
		public void run() {
			InputStream inputStream;

			try {
				BufferedInputStream buf = new BufferedInputStream(mBTSocket.getInputStream() );//inputStream = ;
				while (!bStop) {
					String fsr1 ="";
					String fsr2 ="";
					String fsr3 ="";
					String fsr4 ="";
					String ax ="";
					String ay ="";
					String az ="";
					String gx ="";
					String gy ="";
					String gz ="";
					String time ="";
					String finalStr = "";
                    int v = 0;
					byte[] buffer = new byte[256];
                    int j = 0;
                    if (buf.available() > 0) {
						buf.read(buffer);
						int i = 0;

						/*
						 * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
						 */

						int breakCount=0;
						String temp = "";
						for (i = 0; i < buffer.length && buffer[i] != 0; i++) {

						}
                        final String strInput = new String(buffer, 0, i);
                        final String output = Integer.toString(strInput.length());


                        /*
                        if(buffer[i] != '~' && buffer[i] != '*'){
                            finalStr = finalStr + (char)(buffer[i]);
                            temp = temp + (char)(buffer[i]);
                        }
                        if(buffer[i] == '~'){
                            if(breakCount == 0){
                                fsr1 = temp;
                            }
                            if(breakCount == 1){
                                fsr2 = temp;
                            }
                            if(breakCount == 2){
                                fsr3 = temp;
                            }
                            if(breakCount == 3){
                                fsr4 = temp;
                            }
                            if(breakCount == 4){
                                ax = temp;
                            }
                            if(breakCount == 5){
                                ay = temp;
                            }
                            if(breakCount == 6){
                                az = temp;
                            }
                            if(breakCount == 7){
                                gx = temp;
                            }
                            if(breakCount == 8){
                                gy = temp;
                            }
                            if(breakCount == 9){
                                gz = temp;
                            }
                            finalStr = finalStr + " ";
                            temp = "";
                            breakCount++;
                        }

                        if(buffer[i] == '*'){

                            finalStr = finalStr + "\n";
                            time = temp;

                            temp = "";
                            breakCount = 0;


                            //mydb.insertData(gx,gy,gz,fsr1,fsr2,fsr3,fsr4,az,ay,az, time);




                        }
                        */


                        if (chkReceiveText.isChecked()) {
                            mTxtReceive.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTxtReceive.append(strInput);

                                    //Uncomment below for testing
                                    //mTxtReceive.append("\n");
                                    //mTxtReceive.append("Chars: " + strInput.length() + " Lines: " + mTxtReceive.getLineCount() + "\n");

                                    int txtLength = mTxtReceive.getEditableText().length();
                                    if(txtLength > mMaxChars){
                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
                                    }

                                    if (chkScroll.isChecked()) { // Scroll only if this is checked
                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                }
                            });
                        }
					}
					Thread.sleep(500);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		public void stop() {
			bStop = true;
		}

	}

	private class DisConnectBT extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (mReadThread != null) {
				mReadThread.stop();
				while (mReadThread.isRunning())
					; // Wait until it stops
				mReadThread = null;

			}

			try {
				mBTSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mIsBluetoothConnected = false;
			if (mIsUserInitiatedDisconnect) {
				finish();
			}
		}

	}

	private void msg(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		if (mBTSocket != null && mIsBluetoothConnected) {
			new DisConnectBT().execute();
		}
		Log.d(TAG, "Paused");
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mBTSocket == null || !mIsBluetoothConnected) {
			new ConnectBT().execute();
		}
		Log.d(TAG, "Resumed");
		super.onResume();
	}


	@Override
    protected void onStop() {
		Log.d(TAG, "Stopped");
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	private class ConnectBT extends AsyncTask<Void, Void, Void> {
		private boolean mConnectSuccessful = true;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
		}

		@Override
		protected Void doInBackground(Void... devices) {

			try {
				if (mBTSocket == null || !mIsBluetoothConnected) {
					mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					mBTSocket.connect();
				}
			} catch (IOException e) {
				// Unable to connect to device
				e.printStackTrace();
				mConnectSuccessful = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (!mConnectSuccessful) {
				Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
				finish();
			} else {
				msg("Connected to device");
				mIsBluetoothConnected = true;
				mReadThread = new ReadInput(); // Kick off input reader
			}

			progressDialog.dismiss();
		}

	}


}
