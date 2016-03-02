#include <LiquidCrystal.h>
#include <SoftwareSerial.h> // library that allows you to communicate in other series pins

int led = 13;

// Pins used for inputs and outputs********************************************************
const int analogInPin0 = A0;// Analog input pins
const int analogInPin1 = A1;
const int analogInPin2 = A2;
const int analogInPin3 = A3;
SoftwareSerial bluetoothSerial (4,3); // 10 RX, 11 TX.
 
//Arrays for the 4 inputs**********************************************
float sensorValue[4] = {0,0,0,0};
float voltageValue[4] = {0,0,0,0};
 
//Char used for reading in Serial characters
int inbyte = 0;
//*******************************************************************************************
 
void setup() {
  // initialise serial communications at 9600 bps:
  Serial.begin(9600);
 
  bluetoothSerial.begin(115200); //
  
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
}
 
void loop() {
  readSensors();
  getVoltageValue();
  
  sendAndroidValues();


  //when serial values have been received this will be true
  if( Serial.available() )       // if data is available to read
  {
    inbyte = Serial.read();         // read it and store it in 'val'
    Serial.write(inbyte);
  }
  if( inbyte == '0' )               // if 'H' was received
  {
    digitalWrite(led, HIGH);  // turn ON the LED
  } else { 
    digitalWrite(led, LOW);   // otherwise turn it OFF
  }
  delay(1000);                    // wait 100ms for next reading

  
  
  //when serial values have been received this will be true
  if( bluetoothSerial.available() )       // if data is available to read
  {
    inbyte = bluetoothSerial.read();         // read it and store it in 'val'
    Serial.write(inbyte);
  }
  if( inbyte == '0' )               // if 'H' was received
  {
    digitalWrite(led, HIGH);  // turn ON the LED
  } else { 
    digitalWrite(led, LOW);   // otherwise turn it OFF
  }
  delay(1000);                    // wait 100ms for next reading
}


  

 
void readSensors()
{
  // read the analog in value to the sensor array
  sensorValue[0] = analogRead(analogInPin0);
  sensorValue[1] = analogRead(analogInPin1);
  sensorValue[2] = analogRead(analogInPin2);
  sensorValue[3] = analogRead(analogInPin3);
}
//sends the values from the sensor over serial to BT module
void sendAndroidValues()
 {
  //puts # before the values so our app knows what to do with the data
  bluetoothSerial.print('#');
  //for loop cycles through 4 sensors and sends values via serial
  for(int k=0; k<4; k++)
  {
    bluetoothSerial.print(voltageValue[k]);
    bluetoothSerial.print('+');
    //technically not needed but I prefer to break up data values
    //so they are easier to see when debugging
  }
 bluetoothSerial.print('~'); //used as an end of transmission character - used in app for string length
 bluetoothSerial.println();
 delay(10);        //added a delay to eliminate missed transmissions
}
 

void getVoltageValue()
{
  for (int x = 0; x < 4; x++)
  {
    voltageValue[x] = ((sensorValue[x]/1023)*5);
  }
}
