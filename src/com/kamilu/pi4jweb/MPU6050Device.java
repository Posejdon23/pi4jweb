package com.kamilu.pi4jweb;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import com.alsnightsoft.vaadin.widgets.canvasplus.CanvasPlus;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.vaadin.ui.UI;

public class MPU6050Device implements Serializable {

	private static final long serialVersionUID = 1L;
	I2CBus bus;
	I2CDevice device;
	byte[] accelData, gyroData;
	float gyroXangle;
	float gyroYangle;
	float gyroZangle;
	double cfAngleX;
	double cfAngleY;
	long startTime;
	private boolean reading = true;
	int xx = 0, yy = 0;
	private CanvasPlus canvas;

	public MPU6050Device(CanvasPlus canvas) {
		this.canvas = canvas;
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
		} catch (UnsatisfiedLinkError | Exception e) {
			new MockSensorsThread().start();
		}
	}

	// TODO pêtla czêœciej dla dok³adnych obliczeñ, update UI rzadziej?
	class SensorsThread extends Thread {
		@Override
		public void run() {
			while (reading) {
				try {
					Thread.sleep(500);
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
							try {
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
								cfAngleX = alfa * (cfAngleX + rate_gyr_x * DT) + (1 - alfa) * rotX;
								cfAngleY = alfa * (cfAngleY + rate_gyr_x * DT) + (1 - alfa) * rotY;
								drawXYAxis(cfAngleX, cfAngleY);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						private void drawXYAxis(double cfAngleX, double cfAngleY) {

							double[] xCoord = MathFunctions.getCoord(cfAngleX, 150, 150, 150);
							double[] yCoord = MathFunctions.getCoord(cfAngleY, 150, 450, 150);
							canvas.clear();
							canvas.saveContext();
							canvas.restoreContext();
							canvas.beginPath();
							canvas.setStrokeStyle("#f00");
							canvas.moveTo(xCoord[0], xCoord[1]);
							canvas.lineTo(xCoord[2], xCoord[3]);
							canvas.stroke();
							canvas.beginPath();
							canvas.setStrokeStyle("#00f");
							canvas.moveTo(yCoord[0], yCoord[1]);
							canvas.lineTo(yCoord[2], yCoord[3]);
							canvas.stroke();

						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class MockSensorsThread extends Thread {
		@Override
		public void run() {
			try {
				while (reading) {
					Thread.sleep(500);
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
							Random ranX = new Random();
							Random ranY = new Random();
							int x = ranX.nextInt(10) - 5;
							int y = ranY.nextInt(10) - 5;
							xx += x;
							yy += y;
							double[] xCoord = MathFunctions.getCoord((int) xx, 150, 150, 150);
							double[] yCoord = MathFunctions.getCoord((int) yy, 150, 450, 150);
							canvas.clear();
							canvas.saveContext();
							canvas.restoreContext();
							canvas.beginPath();
							canvas.setStrokeStyle("#f00");
							canvas.moveTo(xCoord[0], xCoord[1]);
							canvas.lineTo(xCoord[2], xCoord[3]);
							canvas.stroke();
							canvas.beginPath();
							canvas.setStrokeStyle("#00f");
							canvas.moveTo(yCoord[0], yCoord[1]);
							canvas.lineTo(yCoord[2], yCoord[3]);
							canvas.stroke();
						}
					});
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public void stopI2cBus() {
		try {
			if (bus != null) {
				bus.close();
			}
		} catch (IOException e) {
		}
	}
	public boolean isReading() {
		return reading;
	}

	public void setReading(boolean reading) {
		this.reading = reading;
	}
}
