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
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class ChGyroscopeWaveform extends BleCharacteristic<ChGyroscopeWaveform.GyroscopeWaveformValue> {
    public final static UUID CHARACTERISTIC_UUID = UUID.fromString("5df14ec3-fed1-4428-83bf-28ade00b0d98");
    private String TAG = ChGyroscopeWaveform.class.getSimpleName();

    public ChGyroscopeWaveform(BluetoothGattCharacteristic gattCharacteristic, BleDevice bleDevice) {
        super(CHARACTERISTIC_UUID, gattCharacteristic, bleDevice);
    }

    public ChGyroscopeWaveform() {
        super(CHARACTERISTIC_UUID);
    }

    @Override
    protected GyroscopeWaveformValue processCharacteristicValue() {
        GyroscopeWaveformValue result = new GyroscopeWaveformValue();

        BluetoothGattCharacteristic ch = getBaseGattCharacteristic();
        byte[] buffer = ch.getValue();

        final int SAMPLE_SIZE = 6;
        for (int i = SAMPLE_SIZE - 1; i < buffer.length; i += SAMPLE_SIZE) {

            int x = ((unsignedByte(buffer[i - 5]) << 8) | unsignedByte(buffer[i - 4]));
            int y = ((unsignedByte(buffer[i - 3]) << 8) | unsignedByte(buffer[i - 2]));
            int z = ((unsignedByte(buffer[i - 1]) << 8) | unsignedByte(buffer[i]));

            GyroscopeXYZValues xyz = new GyroscopeXYZValues(convertGyro(x), convertGyro(y), convertGyro(z));

            //Log.d(TAG, String.format("X=%f, Y=%f, Z=%f", xyz.getX(), xyz.getY(), xyz.getZ()));
            result.wave.add(xyz);
        }

        return result;
    }

    //Based on the Experiment02.pdf
    private float convertGyro(int gyro) {
        float samplingRate = 100.0f;  // Hz
        float sensitivity = 500.0f;   // deg/sec
        int halfRange = 2 << 15;// 16-bit signed value

        return gyro * sensitivity / halfRange / samplingRate;
    }

    public class GyroscopeWaveformValue {
        public ArrayList<GyroscopeXYZValues> wave = new ArrayList<GyroscopeXYZValues>();
    }

    public class GyroscopeXYZValues {
        private float mX;
        private float mY;
        private float mZ;

        public GyroscopeXYZValues(float x, float y, float z) {
            mX = x;
            mY = y;
            mZ = z;
        }

        public float getX() {
            return mX;
        }

        public float getY() {
            return mY;
        }

        public float getZ() {
            return mZ;
        }
    }


    private static int unsignedByte(byte x) {
        return x & 0xFF;
    }
}
