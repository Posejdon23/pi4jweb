package com.kamilu.pi4jweb;

import java.io.Serializable;

import com.pi4j.io.gpio.*;

public class VaadinPin implements Serializable {

	private static final long serialVersionUID = 3377383578818721022L;

	private GpioController gpio;
	private GpioPinOutput pin;
	private Pin pinNumber;
	private String pinName;

	private PinMode pinMode;

	public VaadinPin(GpioController gpioController, Pin pinNumber, PinMode pinMode) {
		this.gpio = gpioController;
		this.pinNumber = pinNumber;
		this.pinName = pinNumber.getName();
		this.pinMode = pinMode;
	}

	public Pin getPinNumber() {
		return pinNumber;
	}

	public String getPinName() {
		return pinName;
	}

	public void setPinNumber(Pin pinNumber) {
		this.pinNumber = pinNumber;
	}

	public void setPinName(String pinName) {
		this.pinName = pinName;
	}

	public void loadPin() {
		if (gpio != null) {
			switch (pinMode) {
				case ANALOG_INPUT :
					break;
				case ANALOG_OUTPUT :
					break;
				case DIGITAL_INPUT :
					break;
				case DIGITAL_OUTPUT :
					pin = gpio.provisionDigitalOutputPin(pinNumber, pinName, PinState.LOW);
					break;
				case PWM_OUTPUT :
					pin = gpio.provisionPwmOutputPin(pinNumber, pinName, 0);
					break;
				default :
					break;
			}
			gpio.setShutdownOptions(false, PinState.LOW, PinPullResistance.OFF, pinMode, pin);
		}
	}

	public void setupPwm(double value) {
		if (pin != null) {
			((GpioPinPwmOutput) pin).setPwm((int) value);
		}
	}
	public void shutdownPin() {
		if (gpio != null) {
			gpio.unprovisionPin(pin);
		}
	}

	public void togglePin() {
		if (pin != null) {
			((GpioPinDigitalOutput) pin).toggle();
		}
	}

	public void blink(long delay, long duration) {
		if (pin != null) {
			((GpioPinDigitalOutput) pin).blink(delay, duration);
		}
	}

	public PinState getState() {
		if (pin != null) {
			return ((GpioPinDigitalOutput) pin).getState();
		}
		return null;
	}
}
