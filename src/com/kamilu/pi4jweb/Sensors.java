package com.kamilu.pi4jweb;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class Sensors {

	I2CBus bus;
	I2CDevice device;
	byte[] accelData, gyroData;
	float gyroXangle;
	float gyroYangle;
	float gyroZangle;
	double CFangleX;
	double CFangleY;
	long startTime;

	public static void main(String[] args) {
		new Sensors();
	}

	public Sensors() {
		System.out.println("Starting sensors reading:");
		// get I2C bus instance
		try {
			// get i2c bus
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
			System.out.println("Connected to bus OK!");

			// get device itself 0x68
			device = bus.getDevice(0x68);
			System.out.println("Connected to device OK!");

			// start sensing, using config registries 6B and 6C
			device.write(0x6B, (byte) 0b00000000);
			device.write(0x6C, (byte) 0b00000000);
			System.out.println("Configuring Device OK!");

			// config gyro
			device.write(0x1B, (byte) 0b11100000);
			// config accel
			device.write(0x1C, (byte) 0b00011001);
			System.out.println("Configuring sensors OK!");

			startReading();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// Create a separate thread for reading the sensors
	public void startReading() {

		new Thread(new Runnable() {
			public void run() {
				try {
					readSensors();
				} catch (InterruptedException | IOException e) {
				}
			}
		}).start();
	}

	private void readSensors() throws IOException, InterruptedException {
		while (true) {
			startTime = System.nanoTime();
			accelData = new byte[6];
			gyroData = new byte[6];

			device.read(0x3B, accelData, 0, 6);

			int aX = (accelData[0] << 8) + accelData[1];
			if (aX >= 32768) {
				aX = -((65535 - aX) + 1);
			}
			int aY = (accelData[2] << 8) + accelData[3];
			if (aY >= 32768) {
				aY = -((65535 - aY) + 1);
			}
			int aZ = (accelData[4] << 8) + accelData[5];
			if (aZ >= 32768) {
				aZ = -((65535 - aZ) + 1);
			}
			double aXsc = aX / 16384.0;
			double aYsc = aY / 16384.0;
			double aZsc = aZ / 16384.0;

			device.read(0x43, gyroData, 0, 6);

			int gX = (gyroData[0] << 8) + gyroData[1];
			if (gX >= 0x8000) {
				gX = -((65535 - gX) + 1);
			}
			int gY = (gyroData[2] << 8) + gyroData[3];
			if (gY >= 0x8000) {
				gY = -((65535 - gY) + 1);
			}
			int gZ = (gyroData[4] << 8) + gyroData[5];
			if (gZ >= 0x8000) {
				gZ = -((65535 - gZ) + 1);
			}

			/*
			 * double gXsc = gX / 131; double gYsc = gY / 131; double gZsc = gZ
			 * / 131;
			 */
			final float gain = 0.07f, DT = 0.002f,
					alfa = 0.8f;

			float rate_gyr_x = (float) gX * gain;
			float rate_gyr_y = (float) gY * gain;
			float rate_gyr_z = (float) gZ * gain;
			gyroXangle += rate_gyr_x * DT;
			gyroYangle += rate_gyr_y * DT;
			gyroZangle += rate_gyr_z * DT;

			if (aXsc > 180) {
				aXsc -= (float) 360.0;
			}
			if (aYsc > 180)
				aYsc -= (float) 360.0;

			double rotX = Math.toDegrees(Math.atan2(aXsc,
					Math.sqrt((aYsc * aYsc) + (aZsc * aZsc))));

			double rotY = -Math.toDegrees(Math.atan2(aYsc,
					Math.sqrt((aXsc * aXsc) + (aZsc * aZsc))));

			CFangleX = alfa * (CFangleX + rate_gyr_x*DT) + (1 - alfa) * rotX;
			CFangleY = alfa * (CFangleY + rate_gyr_x*DT) + (1 - alfa) * rotY;

			// System.out.println("rotX: " + rotX + ", rotY: " + rotY);
			System.out.println("CFangleX: " + CFangleX + ", CFangleY: "
					+ CFangleY);
			// System.out.println("gXsc: " + gXsc + ", gYsc: " + gYsc +
			// ", gZsc: "
			// + gZsc);
			/*
			 * while (System.currentTimeMillis() - startTime < 20) {
			 * Thread.sleep(100); }
			 */
			// System.out.println(TimeUnit.MILLISECONDS.convert(
			// (System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
		}
	}

	// Helper method
	private static final int asInt(byte b) {
		int i = b;
		if (i < 0) {
			i = i + 256;
		}
		return i;
	}
}