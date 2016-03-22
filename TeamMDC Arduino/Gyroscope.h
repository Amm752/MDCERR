#ifndef GYROSCOPE_h
#define GYROSCOPE_h

#include "SD.h"
#include "SPI.h"
#include <avr/io.h>
#include <avr/interrupt.h>


class Gyro {
public:
  char accelAddress = 0x1D;
  float  gx,gy,gz;
  float  gx_rate, gy_rate, gz_rate;
  int ix, iy, iz;
  float anglegx=0.0, anglegy=0.0, anglegz=0.0;
  int ax,ay,az;  
  int rawX, rawY, rawZ;
  float X, Y, Z;
  float rollrad, pitchrad;
  float rolldeg, pitchdeg;
  int error = 0; 
  float aoffsetX, aoffsetY, aoffsetZ;
  float goffsetX, goffsetY, goffsetZ;
  unsigned long time, looptime;

  //Registers
  char WHO_AM_I = 0x00;
  char SMPLRT_DIV= 0x15;
  char DLPF_FS = 0x16;
  char GYRO_XOUT_H = 0x1D;
  char GYRO_XOUT_L = 0x1E;
  char GYRO_YOUT_H = 0x1F;
  char GYRO_YOUT_L = 0x20;
  char GYRO_ZOUT_H = 0x21;
  char GYRO_ZOUT_L = 0x22;
  
  char DLPF_CFG_0 = 1<<0;
  char DLPF_CFG_1 = 1<<1;
  char DLPF_CFG_2 = 1<<2;
  char DLPF_FS_SEL_0 = 1<<3;
  char DLPF_FS_SEL_1 = 1<<4;
  char itgAddress = 0x69;

  int readX();
  int readY();
  int readZ();
  unsigned char itgRead(char address, char registerAddress);
  void itgWrite(char address, char registerAddress, char data);
    
};

#endif //GYROSCOPE_h
