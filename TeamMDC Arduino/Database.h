//
// Created by Justin on 2/15/16.
//

#ifndef DATABASE_DATABASE_H
#define DATABASE_DATABASE_H

#include "SD.h"
#include "SPI.h"
#include <stdint.h>
#include <string.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include "Settings.h"

class Query;

typedef struct Row {
    uint64_t timestamp;
    uint16_t reserved;
    uint16_t gyroX;
    uint16_t gyroY;
    uint16_t gyroZ;
    int16_t accelX;
    int16_t accelY;
    int16_t accelZ;
    uint8_t fsr1;
    uint8_t fsr2;
    uint8_t fsr3;
    uint8_t fsr4;
} Row;

typedef struct PacketHeader {
    uint8_t version;
    uint8_t type;
    uint8_t checksum;
    uint8_t reserved;
    uint16_t length;
    uint16_t reserved2;
} Packet;

typedef struct DataRequestBody {
    uint64_t since;
};

typedef struct TimeSyncBody {
    uint64_t now;
};




struct Header {
    char signature[8];
    uint8_t reserved[56];
};

class Database {
private:
    File dbFile;
    Header header;
    uint64_t rowsSinceSave = 0;
    int error;

    int getHeader();

    int saveHeader();

    void setError(int error);

    int64_t getIndex(uint64_t position);

public:
    int initDatabase();

    int getError();

    int connect(int chipSelect);

    int disconnect();

    int writeRow(Row row);

    Row readRow(uint64_t index);

    Query query(uint64_t since);

    uint64_t count();
};

class Query {

public:
    uint64_t count();

    volatile uint64_t remaining();

    volatile uint8_t isEmpty();

    uint8_t execute(uint64_t initialIndex);

    Row fetch();

private:
    Database db;
    volatile uint64_t idx;
    uint64_t _count = 0;
    volatile uint64_t _remaining = 0;
};

#endif //DATABASE_DATABASE_H
