package com.kamilu.pi4jweb;

import java.io.Serializable;

import com.pi4j.io.gpio.*;

public class VaadinPin implements Serializable {

	private static final long serialVersionUID = 3377383578818721022L;

	private GpioController gpio;
	private GpioPinDigitalOutput pin;
	private Pin pinNumber;
	private String pinName;

	public VaadinPin(GpioController gpioController, Pin pinNumber) {
		this.gpio = gpioController;
		this.pinNumber = pinNumber;
		this.pinName = pinNumber.getName();
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
			pin = gpio.provisionDigitalOutputPin(pinNumber, pinName, PinState.LOW);
			gpio.setShutdownOptions(false, PinState.LOW, PinPullResistance.OFF, PinMode.DIGITAL_OUTPUT, pin);
		}
	}

	public void shutdownPin() {
		if (gpio != null) {
			gpio.unprovisionPin(pin);
		}
	}

	public void togglePin() {
		if (pin != null) {
			pin.toggle();
		}
	}

	public void blink(long delay, long duration) {
		if (pin != null) {
			pin.blink(delay, duration);
		}
	}

	public PinState getState() {
		if (pin != null) {
			return pin.getState();
		}
		return null;
	}
}
