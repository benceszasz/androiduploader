/* Copyright 2013 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: http://code.google.com/p/usb-serial-for-android/
 */

package com.nightscout.android.drivers.USB;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.nightscout.core.drivers.G4ConnectionState;

import java.io.IOException;
import java.util.HashMap;

import rx.functions.Action1;


/**
 * A base class shared by several driver implementations.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
abstract class CommonUsbSerialDriver implements UsbSerialDriver {

    public static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;

    protected final UsbDevice mDevice;
    protected final UsbDeviceConnection mConnection;

    protected final Object mReadBufferLock = new Object();
    protected final Object mWriteBufferLock = new Object();
    protected UsbManager mManager;

    protected boolean mPowerManagementEnabled = false;
    protected int vendorId;
    protected int productId;
    protected int deviceClass;
    protected int subClass;
    protected int protocol;

    protected Action1<G4ConnectionState> connectionStateListener;

    /**
     * Internal read buffer.  Guarded by {@link #mReadBufferLock}.
     */
    protected byte[] mReadBuffer;

    /**
     * Internal write buffer.  Guarded by {@link #mWriteBufferLock}.
     */
    protected byte[] mWriteBuffer;

    public CommonUsbSerialDriver(UsbDevice device, UsbDeviceConnection connection, UsbManager manager) {
        mDevice = device;
        mConnection = connection;
        mManager = manager;

        mReadBuffer = new byte[DEFAULT_READ_BUFFER_SIZE];
        mWriteBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];
    }

    public void setPowerManagementEnabled(boolean powerManagementEnabled) {
        this.mPowerManagementEnabled = powerManagementEnabled;
    }

    /**
     * Returns the currently-bound USB device.
     *
     * @return the device
     */
    public final UsbDevice getDevice() {
        return mDevice;
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for read operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setReadBufferSize(int bufferSize) {
        synchronized (mReadBufferLock) {
            if (bufferSize == mReadBuffer.length) {
                return;
            }
            mReadBuffer = new byte[bufferSize];
        }
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for write operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setWriteBufferSize(int bufferSize) {
        synchronized (mWriteBufferLock) {
            if (bufferSize == mWriteBuffer.length) {
                return;
            }
            mWriteBuffer = new byte[bufferSize];
        }
    }

    @Override
    public abstract void open() throws IOException;

    @Override
    public abstract void close() throws IOException;

    @Override
    public abstract int read(final byte[] dest, final int timeoutMillis) throws IOException;

    @Override
    public abstract int write(final byte[] src, final int timeoutMillis) throws IOException;

    @Override
    public abstract void setParameters(
            int baudRate, int dataBits, int stopBits, int parity) throws IOException;

    @Override
    public abstract boolean getCD() throws IOException;

    @Override
    public abstract boolean getCTS() throws IOException;

    @Override
    public abstract boolean getDSR() throws IOException;

    @Override
    public abstract boolean getDTR() throws IOException;

    @Override
    public abstract void setDTR(boolean value) throws IOException;

    @Override
    public abstract boolean getRI() throws IOException;

    @Override
    public abstract boolean getRTS() throws IOException;

    @Override
    public abstract void setRTS(boolean value) throws IOException;

    public boolean isConnected(int vendorId, int productId, int deviceClass, int subClass,
                               int protocol) {
        if (mManager == null) return false;
        HashMap<String, UsbDevice> deviceList = mManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == vendorId && device.getProductId() == productId &&
                    device.getDeviceClass() == deviceClass &&
                    device.getDeviceSubclass() == subClass &&
                    device.getDeviceProtocol() == protocol) {
                return true;
            }
        }
        return false;
    }

    public void setUsbCriteria(int vendorId, int productId, int deviceClass, int subClass, int protocol) {
        this.vendorId = vendorId;
        this.productId = productId;
        this.deviceClass = deviceClass;
        this.subClass = subClass;
        this.protocol = protocol;
    }

    @Override
    public boolean isConnected() {
        return this.isConnected(vendorId, productId, deviceClass, subClass, protocol);
    }

    @Override
    public void registerConnectionListener(Action1<G4ConnectionState> connectionListener) {
        connectionStateListener = connectionListener;
    }
}
