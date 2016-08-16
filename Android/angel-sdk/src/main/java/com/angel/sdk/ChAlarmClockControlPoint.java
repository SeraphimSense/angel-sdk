/*
 * Copyright (c) 2015, Seraphim Sense Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.angel.sdk;

import android.bluetooth.BluetoothGattCharacteristic;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.UUID;


/**
 * 
 */
public class ChAlarmClockControlPoint extends BleCharacteristic<ChAlarmClockControlPoint.AlarmClockValue> {
    private final static UUID CHARACTERISTIC_UUID = UUID.fromString("e4616b0c-22d5-11e4-a7bf-b2227cce2b54");


    public ChAlarmClockControlPoint(BluetoothGattCharacteristic vanillaCharacteristic,
                                    BleDevice bleDevice) {
        super(CHARACTERISTIC_UUID, vanillaCharacteristic, bleDevice);
    }

    public ChAlarmClockControlPoint() {
        super(CHARACTERISTIC_UUID);
    }


    public void requestNumberOfAlarms() {
        byte[] bytes = { OP_CODE_NUMBER_OF_ALARMS };
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }

    public void requestMaxNumberOfAlarms() {
        byte[] bytes = { OP_CODE_MAX_NUMBER_OF_ALARMS };
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }

    public void adjustTime(GregorianCalendar dateTime) {
        byte[] opcode   = {OP_CODE_AJUST_CLOCK_DATE_TIME};
        byte[] date     = BleDayDateTime.SerializeDateTime(dateTime);

        byte[] bytes    = this.concatBytes(opcode, date);

        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }


    public void addAlarm(GregorianCalendar dateTime) {
        byte[] opcode   = {OP_CODE_ADD_ALARM};
        byte[] date     = BleDayDateTime.SerializeDateTime(dateTime);

        byte[] bytes    = this.concatBytes(opcode, date);

        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }

    public void readAlarm(int alarmId) {
        byte[] bytes   = { OP_CODE_READ_ALARM, (new Integer(alarmId).byteValue())};
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }


    public void removeAlarm(int alarmId) {

        byte[] bytes    = {OP_CODE_REMOVE_ALARM, (new Integer(alarmId).byteValue())};

        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }


    public void removeAllAlarms() {
        byte[] bytes    = {OP_CODE_REMOVE_ALL_ALARMS};

        BluetoothGattCharacteristic c = getBaseGattCharacteristic();
        c.setValue(bytes);
        getBleDevice().writeCharacteristic(c);
    }


    private byte[] concatBytes(byte[] a, byte[] b) {
        byte[] bytes  = new byte[a.length + b.length];
        System.arraycopy(a, 0, bytes, 0, a.length);
        System.arraycopy(b, 0, bytes, a.length, b.length);

        return bytes;
    }


    @Override
    protected AlarmClockValue processCharacteristicValue() {
        BluetoothGattCharacteristic c = getBaseGattCharacteristic();

        int statusCode = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        AlarmClockValue alarmClockValue = new AlarmClockValue();
        alarmClockValue.mStatusCode = statusCode;
        alarmClockValue.mResponseValue = c.getValue();

        return alarmClockValue;
    }

    public class AlarmClockValue {

        public int mStatusCode;
        public byte[] mResponseValue;

        public int getNumberOfAlarms() {
            return ByteBuffer.wrap(mResponseValue).get(1);
        }

        public int getMaxNumberOfAlarms() {
            return ByteBuffer.wrap(mResponseValue).get(1);
        }

        public GregorianCalendar readAlarm() {
            return BleDayDateTime.Deserialize(Arrays.copyOfRange(mResponseValue, 1, 8));
        }

        public int getAlarmId() {
            return mStatusCode;
        }

        public int getStatus() {
            return mStatusCode;
        }

        public int getAlarmCount() {
            return 0;
        }
    }

    private final static short OP_CODE_MAX_NUMBER_OF_ALARMS       = 1;
    private final static short OP_CODE_NUMBER_OF_ALARMS           = 2;
    private final static short OP_CODE_READ_ALARM                 = 3;
    private final static short OP_CODE_ADD_ALARM                  = 4;
    private final static short OP_CODE_REMOVE_ALARM               = 5;
    private final static short OP_CODE_REMOVE_ALL_ALARMS          = 6;
    private final static short OP_CODE_AJUST_CLOCK_DATE_TIME      = 7;
}
