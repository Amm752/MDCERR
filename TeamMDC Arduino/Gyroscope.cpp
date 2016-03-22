#include "Gyroscope.h"
#include <Wire.h>
// Datasheet - https://www.sparkfun.com/datasheets/Sensors/Gyro/PS-ITG-3200-00-01.4.pdf
//Every read to the gyroscope starts the same way
//First, a transmition is made for the wire library, because at this point
//the microcontroller does not know which device I want to talk to. There, I send the address of the
//device

//Second, I write to it which register I want to read on that device. Than I end the transmition, but don't close it completely
//Next, I ask the wire library for that device again, than request data from it. As long as data is available, I read it into a variable,
//than end the transmition

//Write is very similar, however, once I have the register I want to talk to, I just write to it. 
unsigned char Gyro::itgRead(char address, char registerAddress)
{
  unsigned char data=0;
  Wire.beginTransmission(address);
  Wire.write(registerAddress);
  Wire.endTransmission();
  Wire.beginTransmission(address);
  Wire.requestFrom(address, 1);
  if(Wire.available()){
    data = Wire.read();
  }
  Wire.endTransmission();
  return data;
};
void Gyro::itgWrite(char address, char registerAddress, char data)
{
  Wire.beginTransmission(address);
  Wire.write(registerAddress);
  Wire.write(data);
  Wire.endTransmission();
}


//the gyroscope axis's each have two registers. This is because the axis has 16 bit percision, with each register
//only being 8 bits. So there is a high register and a low register. Take both, shift the first over 8 bits, than concatinate it
//with the low register. 
int Gyro::readX()
{
  int data=0;
  data = itgRead(itgAddress, GYRO_XOUT_H)<<8;
  data |= itgRead(itgAddress, GYRO_XOUT_L);

  return data;
};
int Gyro::readZ(void)
{
  int data=0;
  data = itgRead(itgAddress, GYRO_ZOUT_H)<<8;
  data |= itgRead(itgAddress, GYRO_ZOUT_L);

  return data;
}
int Gyro::readY(void)
{
  int data=0;
  data = itgRead(itgAddress, GYRO_YOUT_H)<<8;
  data |= itgRead(itgAddress, GYRO_YOUT_L);

  return data;
}


