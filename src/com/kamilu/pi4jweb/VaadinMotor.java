package com.kamilu.pi4jweb;

import java.io.Serializable;
import java.util.Collection;

import com.pi4j.component.motor.MotorState;
import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.*;

public class VaadinMotor implements Serializable {

	private static final long serialVersionUID = -6558127689394260762L;

	private static final byte[] single_step_sequence = new byte[]{1, 2, 4, 8};

	private transient GpioStepperMotorComponent motor;
	private transient GpioPinDigitalOutput[] motorPins;
	private final GpioController gpio;
	private Collection<Pin> pins;
	private long stepInterval;
	private int stepsPerRevolution;

	public MotorState getState() {
		return motor.getState();
	}

	public void setState(MotorState state) {
		motor.setState(state);
	}

	public VaadinMotor(GpioController gpio, Collection<Pin> pins, long stepInterval, boolean singleSeqence, int stepsPerRevolution) {
		this.gpio = gpio;
		this.pins = pins;
		this.stepInterval = stepInterval;
		this.stepsPerRevolution = stepsPerRevolution;
	}

	public void load() {
		if (gpio != null) {
			GpioPinDigitalOutput[] motorPins = loadPins();
			motor = new GpioStepperMotorComponent(motorPins);
			gpio.setShutdownOptions(true, PinState.LOW, motorPins);
			motor.setStepInterval(stepInterval);
			motor.setStepSequence(single_step_sequence);
			motor.setStepsPerRevolution(stepsPerRevolution);
		}
	}

	private GpioPinDigitalOutput[] loadPins() {
		motorPins = new GpioPinDigitalOutput[pins.size()];
		int i = 0;
		for (Pin pin : pins) {
			GpioPinDigitalOutput motorPin = gpio.provisionDigitalMultipurposePin(pin, PinMode.DIGITAL_OUTPUT);
			motorPins[i++] = motorPin;
		}
		return motorPins;
	}

	public void shutdown() {
		if (gpio != null) {
			for (GpioPinDigitalOutput pin : motorPins) {
				gpio.unprovisionPin(pin);
			}
		}
	}

	public void forward() {
		if (motor != null) {
			motor.forward();
		}
	}

	public void reverse() {
		if (motor != null) {
			motor.reverse();
		}
	}

	public void stop() {
		if (motor != null) {
			motor.stop();
		}
	}
}
