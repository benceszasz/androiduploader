package com.nightscout.android.drivers;

import java.util.UUID;

public class DexShareAttributes {

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Share Characteristic Strings
    public static final UUID AuthenticationCode = UUID.fromString("F0ABACAC-EBFA-F96F-28DA-076C35A521DB");
    public static final UUID ShareMessageReceiver = UUID.fromString("F0ABB20A-EBFA-F96F-28DA-076C35A521DB"); // Max 20 Bytes - Writable
    public static final UUID ShareMessageResponse = UUID.fromString("F0ABB20B-EBFA-F96F-28DA-076C35A521DB"); // Max 20 Bytes
    public static final UUID Command = UUID.fromString("F0ABB0CC-EBFA-F96F-28DA-076C35A521DB");
    public static final UUID Response = UUID.fromString("F0ABB0CD-EBFA-F96F-28DA-076C35A521DB"); // Writable?
    public static final UUID HeartBeat = UUID.fromString("F0AB2B18-EBFA-F96F-28DA-076C35A521DB");

    //Possible new uuids????  60bfxxxx-60b0-4d4f-0000-000160c48d70
    public static final UUID CradleService2 = UUID.fromString("F0ACA0B1-EBFA-F96F-28DA-076C35A521DB");
    public static final UUID AuthenticationCode2 = UUID.fromString("F0ACACAC-EBFA-F96F-28DA-076C35A521DB"); // read, write
    public static final UUID ShareMessageReceiver2 = UUID.fromString("F0ACB20A-EBFA-F96F-28DA-076C35A521DB"); // read, write
    public static final UUID ShareMessageResponse2 = UUID.fromString("F0ACB20B-EBFA-F96F-28DA-076C35A521DB"); // indicate, read
    public static final UUID Command2 = UUID.fromString("F0ACB0CC-EBFA-F96F-28DA-076C35A521DB"); // read, write
    public static final UUID Response2 = UUID.fromString("F0ACB0CD-EBFA-F96F-28DA-076C35A521DB"); // indicate, read, write
    public static final UUID HeartBeat2 = UUID.fromString("F0AC2B18-EBFA-F96F-28DA-076C35A521DB"); // notify, read
    public final static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    public final static UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");


    //Device Info
    public static final UUID DeviceService = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID PowerLevel = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
}