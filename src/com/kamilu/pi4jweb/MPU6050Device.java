package com.kamilu.pi4jweb;

import java.io.IOException;
import java.io.Serializable;

import com.alsnightsoft.vaadin.widgets.canvasplus.CanvasPlus;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class MPU6050Device implements Serializable {

	private static final long serialVersionUID = 1L;
	I2CBus bus;
	I2CDevice device;
	byte[] accelData, gyroData;
	float gyroXangle;
	float gyroYangle;
	float gyroZangle;
	double CFangleX;
	double CFangleY;
	long startTime;
	private CanvasPlus canvas;
	private Label label;

	public MPU6050Device(CanvasPlus canvas, Label label) {
		this.canvas = canvas;
		this.label = label;

	}

	public void startReading() {
		try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
			device = bus.getDevice(0x68);
			device.write(0x6B, (byte) 0b00000000);
			device.write(0x6C, (byte) 0b00000000);
			device.write(0x1B, (byte) 0b11100000);
			device.write(0x1C, (byte) 0b00011001);
			new SensorsThread().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mockStartReading() {
		new MockSensorsThread().start();
	}

	class SensorsThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
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
					final float gain = 0.07f, DT = 0.002f, alfa = 0.8f;
					float rate_gyr_x = (float) gX * gain;
					float rate_gyr_y = (float) gY * gain;
					float rate_gyr_z = (float) gZ * gain;
					gyroXangle += rate_gyr_x * DT;
					gyroYangle += rate_gyr_y * DT;
					gyroZangle += rate_gyr_z * DT;
					if (aXsc > 180) {
						aXsc -= (float) 360.0;
					}
					if (aYsc > 180) {
						aYsc -= (float) 360.0;
					}
					double rotX = Math.toDegrees(Math.atan2(aXsc, Math.sqrt((aYsc * aYsc) + (aZsc * aZsc))));
					double rotY = -Math.toDegrees(Math.atan2(aYsc, Math.sqrt((aXsc * aXsc) + (aZsc * aZsc))));
					CFangleX = alfa * (CFangleX + rate_gyr_x * DT) + (1 - alfa) * rotX;
					CFangleY = alfa * (CFangleY + rate_gyr_x * DT) + (1 - alfa) * rotY;
					label.setValue("CFangleX: " + CFangleX + ", CFangleY: " + CFangleY);
					Thread.sleep(1000);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	class MockSensorsThread extends Thread {
		int count = 0;

		@Override
		public void run() {
			try {
				// Update the data for a while
				while (count < 100) {
					Thread.sleep(2000);
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
							label.setValue("Mock CFangleX: " + Math.random() + ", Mock CFangleY: " + Math.random());
						}
					});
				}
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						label.setValue("Done");
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		try {
			bus.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mockStop() {
		label.setValue("Device stopped");
	}

}