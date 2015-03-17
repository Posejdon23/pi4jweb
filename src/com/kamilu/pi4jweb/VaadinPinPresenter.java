package com.kamilu.pi4jweb;

import static com.vaadin.ui.Alignment.BOTTOM_LEFT;
import static com.vaadin.ui.Alignment.MIDDLE_CENTER;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class VaadinPinPresenter extends Panel {

	private static final long serialVersionUID = 4436789960456663197L;
	private final GpioController gpioController;
	private final VerticalLayout rootLayout;
	private final HorizontalLayout horiz1, horiz2;
	private final ToggleButton switchLHState;
	private final ToggleButton switchOnOffState;
	private VaadinPin vaadinPin;
	private Button deleteBtn;
	private ComboBox pinNumber, pinModeCombo;
	private TextField pinName;
	private Slider pwmValue;

	public VaadinPinPresenter(GpioController gpioController) {
		this.gpioController = gpioController;
		rootLayout = new VerticalLayout();
		horiz1 = new HorizontalLayout();
		horiz2 = new HorizontalLayout();
		pinNumber = new ComboBox("Pin Number");
		pinModeCombo = new ComboBox("Pin Mode");
		pinName = new TextField("Pin Name");
		pwmValue = new Slider("PWM", 1, 1023);
		pwmValue.setValue(1d);
		pwmValue.setResolution(0);
		pwmValue.setWidth("250px");
		switchOnOffState = new ToggleButton(false, "", new OnOffPinListener());
		switchLHState = new ToggleButton(false, "State ", new LowHighPinListener());
		deleteBtn = new Button("", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				VaadinPinPresenter.removeFromParent(VaadinPinPresenter.this);
			}
		});
		horiz1.addComponents(pinName, pinNumber, switchOnOffState);
		horiz2.addComponents(pinModeCombo, switchLHState, deleteBtn);
		rootLayout.addComponents(horiz1, horiz2, pwmValue);
		buildPinCombo();
		buildPinModeCombo();

		horiz1.setComponentAlignment(switchOnOffState, BOTTOM_LEFT);
		horiz2.setComponentAlignment(switchLHState, BOTTOM_LEFT);
		horiz2.setComponentAlignment(deleteBtn, BOTTOM_LEFT);
		rootLayout.setComponentAlignment(pwmValue, MIDDLE_CENTER);
		switchLHState.setCaptionOFF("LOW");
		switchLHState.setCaptionON("HIGH");
		switchLHState.setVisible(false);
		pwmValue.setVisible(false);
		rootLayout.setSizeFull();
		setContent(rootLayout);
		rootLayout.setSpacing(true);
		horiz1.setSpacing(true);
		horiz2.setSpacing(true);
		deleteBtn.setIcon(FontAwesome.TRASH_O);
		setIcon(FontAwesome.LIGHTBULB_O);
		pwmValue.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				vaadinPin.setupPwm(Double.valueOf(pwmValue.getValue()));
			}
		});
	}
	private void buildPinCombo() {
		pinNumber.setNullSelectionAllowed(false);
		pinNumber.addItems(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05,
				RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_08, RaspiPin.GPIO_09, RaspiPin.GPIO_10, RaspiPin.GPIO_11,
				RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_15, RaspiPin.GPIO_16, RaspiPin.GPIO_17,
				RaspiPin.GPIO_18, RaspiPin.GPIO_19, RaspiPin.GPIO_20, RaspiPin.GPIO_21, RaspiPin.GPIO_22, RaspiPin.GPIO_23,
				RaspiPin.GPIO_24, RaspiPin.GPIO_25, RaspiPin.GPIO_26, RaspiPin.GPIO_27, RaspiPin.GPIO_28, RaspiPin.GPIO_29);
		pinNumber.select(RaspiPin.GPIO_00);
	}

	private void buildPinModeCombo() {
		pinModeCombo.setNullSelectionAllowed(false);
		pinModeCombo.addItems(PinMode.all());
		pinModeCombo.select(PinMode.DIGITAL_OUTPUT);
	}

	private void togglePin() {
		if (switchOnOffState.getValue()) {
			if (vaadinPin == null) {
				vaadinPin = new VaadinPin(gpioController, (Pin) pinNumber.getValue(), (PinMode) pinModeCombo.getValue());
			}
			vaadinPin.loadPin();
		} else {
			switchLHState.setState(false);
			if (vaadinPin == null) {
				vaadinPin.shutdownPin();
			}
		}
	}

	private final class OnOffPinListener implements Button.ClickListener {

		private static final long serialVersionUID = -7341862608775631518L;

		@Override
		public void buttonClick(ClickEvent event) {
			switchOnOffState.toggle();
			togglePin();
			switchLHState.setVisible(switchOnOffState.getValue());
			deleteBtn.setVisible(!switchOnOffState.getValue());
			pinName.setVisible(!switchOnOffState.getValue());
			pwmValue.setVisible(pinModeCombo.getValue().equals(PinMode.PWM_OUTPUT) //
					&& switchOnOffState.getValue());
			pinNumber.setEnabled(!switchOnOffState.getValue());
			pinModeCombo.setEnabled(!switchOnOffState.getValue());
			setCaption(pinName.getValue() + " - " + ((Pin) pinNumber.getValue()).toString());
		}
	}

	private final class LowHighPinListener implements ClickListener {

		private static final long serialVersionUID = 8256754436132369306L;

		@Override
		public void buttonClick(ClickEvent event) {
			switchLHState.toggle();
			vaadinPin.togglePin();
		}
	}

	public Pin getPinNumber() {
		return (Pin) pinNumber.getValue();
	}

	public String getPinName() {
		return ((Pin) pinNumber.getValue()).getName();
	}
}
