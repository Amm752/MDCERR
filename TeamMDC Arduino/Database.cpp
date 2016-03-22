//
// Created by Justin on 2/15/16.
//

#include "Database.h"

uint8_t Query::execute(uint64_t initialIndex) {
    Serial.println("Execute query");
    idx = initialIndex;
    Serial.printf("%llu",initialIndex);
    Serial.printf("Db.count() %llu", db.count());
    _count = db.count() - initialIndex;
    _remaining = _count;
    Serial.println("Done with query execution.");
    return 1;
};

uint64_t Query::count() {
    return _count;
}

volatile uint64_t Query::remaining() {
    return _remaining;
}

volatile uint8_t Query::isEmpty() {
    return _remaining <= 0;
}

Row Query::fetch() {
    _remaining--;
    Row r = db.readRow(idx++);
    Serial.printf("Index Retrieved: %llu Timestamp: %llu\n", idx, r.timestamp);
    return r;
};


void Database::setError(int error) {
    error = error;
}

int Database::initDatabase() {

    // write default header if no header retrieved
    if (!getHeader()) {
        for (uint8_t i = 0; i < 8; i++) {
            header.signature[i] = 0xFF;
        }
        if (!saveHeader()) return 0;
    }

    return 1;
}

int Database::getHeader() {
    // is file big enough for header?
    if (((uint64_t) dbFile.size()) <= sizeof(Header)) {
        return 0;
    }

    // move to start of file before reading
    dbFile.seek(0);

    // read bytes into array
    uint8_t bytes[sizeof(Header)];
    for (uint8_t i = 0; i < sizeof(Header); i++) {
        bytes[i] = dbFile.read();
    }

    // check signature
    for (uint8_t j = 0; j < 8; j++) {
        if (bytes[j] != 0xFF) {
            Serial.println("No match on header signature.");
            return 0;
        }
    }

    // cast bytes as struct
    header = *((Header *) bytes);

    return 1;
}

int Database::saveHeader() {
    unsigned char *bytes = (unsigned char *) &header;
    int bytes_written = dbFile.write(bytes, sizeof(Header));
    if (bytes_written != sizeof(Header)) {
        setError(5);
        return 0;
    }
    return 1;
};

int64_t Database::getIndex(uint64_t position) {
    cli();

    uint64_t fileSize = dbFile.size();

    // check validity of position
    if ((fileSize - sizeof(Header)) % sizeof(Row) != 0) {
        return -1;
    }

    sei();

    return (fileSize - sizeof(Header)) / sizeof(Row);
}

int Database::getError() {
    return error;
}

int Database::connect(int chipSelect) {
    if (!SD.begin(chipSelect)) {
        setError(1);
        return 0;
    }

    dbFile = SD.open("MDC", FILE_WRITE);

    if (!dbFile) {
        setError(2);
        return 0;
    }

    if (!initDatabase()) {
        setError(3);
        return 0;
    }

    return 1;
};

int Database::disconnect() {
    dbFile.close();
    return 1;
};

int Database::writeRow(Row row) {
    // interrupt protect
    cli();
    unsigned char *bytes = (unsigned char *) &row;
    dbFile.seek(dbFile.size()); // TODO Optimize this!

    if (dbFile.write(bytes, sizeof(Row)) != sizeof(Row)) {
        setError(4);
        return 0;
    }

    if (rowsSinceSave++ >= FLUSH_ROWS_AFTER) {
        dbFile.flush();
        rowsSinceSave = 0;
    }

    sei();
    // end interrupt protect
    return 1;
};

Row Database::readRow(uint64_t index) {
    uint8_t bytes[sizeof(Row)];
    //Serial.printf("Reading row#: %llu\n", index);
    // interrupt protect
    cli();
    dbFile.seek(sizeof(Header) + index * sizeof(Row));
    for (uint64_t i = 0; i < sizeof(Row); i++) {
        bytes[i] = dbFile.read();
    }
    sei();
    // end interrupt protect
    Row row = *((Row *) bytes);

    return row;
}

uint64_t Database::count() {
    Serial.printf("db filesize %lu\n", dbFile.size());
    return (uint64_t) (dbFile.size() - sizeof(Header) / sizeof(Row));
}

Query Database::query(uint64_t since) {
    int64_t min = 0;
    int64_t max = (int64_t) count();
    int64_t mid = 0;

    while (min <= max) {
        mid = min + (uint64_t)((max - min) / 2);
//        Serial.printf("Min: %lld Mid: %lld Max: %lld\n", min, mid, max);
        uint64_t timestamp = readRow(mid).timestamp;
//        Serial.printf("Timestamp: %llu Since: %llu\n", timestamp, since);
        if (timestamp > since) {
            max = mid - 1;
//            Serial.println("Lower Half");
        } else if (timestamp < since) {
            min = mid + 1;
//            Serial.println("Upper Half");
        } else {
            break;
        }
    }

    // since duplicate readings per ms are possible...
    while (readRow(mid).timestamp <= since) {
        mid++;
//        Serial.printf("Mid: %llu", mid);
    }
//    Serial.printf("Mid: %llu", mid);

    Query q;
    q.execute((uint64_t) mid);

//    Serial.println("Got result!");
    return q;
};



