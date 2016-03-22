#include <SoftwareSerial.h>
#include <SD.h>
#include <SPI.h>
#include "SD.h"
#include "SPI.h"
#include <stdint.h>
#include <string.h>
#include <Wire.h>
#include <avr/interrupt.h>
#include <avr/power.h>
#include <avr/sleep.h>
#include <avr/io.h> 
#include "Database.h"
#include "Settings.h"
#include "Accelerometer.h"
#include "Gyroscope.h"
#include "fsr.h"

elapsedMillis sinceStart;
Database database;
Gyro gyro;
MMA8452Q accel;

uint64_t startFrom;
unsigned long looptime, time;

SoftwareSerial bluetoothSerial(4, 3);

void setup(void) {
  startFrom = database.readRow(database.count() - 1).timestamp; //Start Timmer
  Serial.begin(9600); //To Do: USB serial for debugging. Remove for final release
  pinMode(4, INPUT);
  pinMode(3, OUTPUT);
  bluetoothSerial.begin(115200); //Start Bluetooth Serial
  if (!database.connect(CHIP_SELECT)) {
        Serial.printf("Could not connect to database! Error Code: %d", database.getError());
        return;
    } else {
        Serial.println("Connected to database!");
    }
  Wire.begin(); //Start i2c
  accel.init(); //Initalize accelerometer
  Serial.println("Serial Started"); //To Do: Debugging only. Remove

  //Sets the gyroscope scale
  gyro.itgWrite(gyro.itgAddress, gyro.DLPF_FS, (gyro.DLPF_FS_SEL_0|gyro.DLPF_FS_SEL_1|gyro.DLPF_CFG_0));
  //sets the sample rate
  gyro.itgWrite(gyro.itgAddress, gyro.SMPLRT_DIV, 9);

  
  //This for loop takes the first 200 values and averages them. Since the gyroscope exists in some space, it will need to be zeroed everytime. 
  for (int i = 0; i <= 200; i++) {
    gyro.gx = gyro.readX();
    gyro.gx = gyro.gx/14.375;  //14.375 is the sensativity scale on the degrees per second
    gyro.gy = gyro.readY();
    gyro.gy = gyro.gy/14.375;
    gyro.gz = gyro.readZ(); 
    gyro.gz = gyro.gz/14.375;
    if (i == 0) {
      gyro.goffsetX = gyro.gx;
      gyro.goffsetY = gyro.gy;
      gyro.goffsetZ = gyro.gz;
    }
    if (i > 1) {
      gyro.goffsetX = (gyro.gx + gyro.goffsetX) / 2;
      gyro.goffsetY = (gyro.gy + gyro.goffsetY) / 2;
      gyro.goffsetZ = (gyro.gz + gyro.goffsetZ) / 2;
    }
  }
  delay(1000);
}


void loop(void) {

  Serial.print("Hello");

}


