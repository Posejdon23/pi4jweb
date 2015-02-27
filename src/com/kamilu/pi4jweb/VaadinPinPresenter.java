package com.kamilu.pi4jweb;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class VaadinPinPresenter extends Panel {

	private static final long serialVersionUID = 4436789960456663197L;
	private final GpioController gpioController;
	private final VerticalLayout rootLayout;
	private final HorizontalLayout horiz;
	private final ToggleButton switchLHState;
	private final ToggleButton switchOnOffState;
	private VaadinPin vaadinPin;
	private Button deleteBtn;
	private ComboBox pinCombo;
	private TextField pinName;

	public VaadinPinPresenter(GpioController gpioController) {
		this.gpioController = gpioController;
		rootLayout = new VerticalLayout();
		horiz = new HorizontalLayout();
		pinCombo = new ComboBox();
		pinName = new TextField("Pin Name");
		switchOnOffState = new ToggleButton(false, "", new OnOffPinListener());
		switchLHState = new ToggleButton(false, "State ", new LowHighPinListener());
		deleteBtn = new Button("", (Button.ClickListener) event -> {
			removeFromParent(this);
		});
		horiz.addComponents(pinCombo, switchOnOffState, deleteBtn);
		rootLayout.addComponents(pinName, horiz, switchLHState);
		buildCombo();

		switchLHState.setCaptionOFF("LOW");
		switchLHState.setCaptionON("HIGH");
		switchLHState.setVisible(false);
		rootLayout.setSizeFull();
		setContent(rootLayout);
		rootLayout.setSpacing(true);
		horiz.setSpacing(true);
		deleteBtn.setIcon(FontAwesome.TRASH_O);
		setIcon(FontAwesome.LIGHTBULB_O);
	}

	private void buildCombo() {
		pinCombo.setNullSelectionAllowed(false);
		pinCombo.addItems(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05,
				RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_08, RaspiPin.GPIO_09, RaspiPin.GPIO_10, RaspiPin.GPIO_11,
				RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_15, RaspiPin.GPIO_16, RaspiPin.GPIO_17,
				RaspiPin.GPIO_18, RaspiPin.GPIO_19, RaspiPin.GPIO_20, RaspiPin.GPIO_21, RaspiPin.GPIO_22, RaspiPin.GPIO_23,
				RaspiPin.GPIO_24, RaspiPin.GPIO_25, RaspiPin.GPIO_26, RaspiPin.GPIO_27, RaspiPin.GPIO_28, RaspiPin.GPIO_29);
		pinCombo.select(RaspiPin.GPIO_00);
	}
	private void togglePin() {
		if (switchOnOffState.getValue()) {
			if (vaadinPin == null) {
				vaadinPin = new VaadinPin(gpioController, (Pin) pinCombo.getValue());
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
			pinCombo.setEnabled(!switchOnOffState.getValue());
			setCaption(pinName.getValue() + " - " + ((Pin) pinCombo.getValue()).toString());
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
		return (Pin) pinCombo.getValue();
	}

	public String getPinName() {
		return ((Pin) pinCombo.getValue()).getName();
	}
}
