package com.kamilu.pi4jweb;

import java.util.Arrays;
import java.util.Collection;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

@SuppressWarnings("serial")
public class VaadinMotorPresenter extends Panel {

	private VaadinMotor gpioMotor;
	private TwinColSelect select;
	private TextField interval;
	private ComboBox sequence;
	private TextField stepsPerRevolution;
	private HorizontalLayout actionHoriz, configHoriz;
	private final VerticalLayout rootLayout;
	private TextField motorName;
	private HorizontalLayout loadHoriz;
	private Button createBtn;
	private Button deleteBtn;
	private final GpioController gpioController;

	public VaadinMotorPresenter(GpioController gpioController) {
		this.gpioController = gpioController;
		rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		buildConfigMode();
		buildActionMode();
		setConfigMode();
		setContent(rootLayout);
		setImmediate(true);
		setIcon(FontAwesome.TAXI);
	}

	private void buildConfigMode() {
		configHoriz = new HorizontalLayout();
		motorName = new TextField("Motor Name");
		select = new TwinColSelect("Select GPIO Pins");
		select.addItems(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05,
				RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_08, RaspiPin.GPIO_09);
		select.setSizeFull();
		interval = new TextField("Step interval", "3");
		sequence = new ComboBox("Step sequence", Arrays.asList("Single step sequence", "double step sequence"));
		sequence.select("Single step sequence");
		sequence.setNullSelectionAllowed(false);
		stepsPerRevolution = new TextField("stepPerRevolution", "2038");
		createBtn = new Button("Create", (Button.ClickListener) event -> {
			createMotor();
			setActionMode();
		});
		createBtn.setIcon(FontAwesome.PLUS);
		deleteBtn = new Button("", (Button.ClickListener) event -> {
			removeFromParent(this);
		});
		deleteBtn.setIcon(FontAwesome.TRASH_O);
		configHoriz.addComponents(motorName, createBtn, deleteBtn);
		configHoriz.setComponentAlignment(createBtn, Alignment.BOTTOM_LEFT);
		configHoriz.setComponentAlignment(deleteBtn, Alignment.BOTTOM_LEFT);
		rootLayout.setSpacing(true);
		configHoriz.setSpacing(true);
	}

	private void buildActionMode() {
		loadHoriz = new HorizontalLayout();
		actionHoriz = new HorizontalLayout();
		actionHoriz.setVisible(false);
		Button configBtn = new Button("", (Button.ClickListener) event -> {
			setConfigMode();
		});
		configBtn.setIcon(FontAwesome.COGS);
		ToggleButton switchLoad = new ToggleButton(false, "", event -> {
			ToggleButton button = (ToggleButton) event.getButton();
			button.toggle();
			setMotorState(button.getValue());
			actionHoriz.setVisible(button.getValue());
			configBtn.setVisible(!button.getValue());
		});
		Button forwardBtn = new Button("", (Button.ClickListener) event -> {
			gpioMotor.forward();
			setCaption(motorName.getValue() + "  Forward");
		});
		forwardBtn.setIcon(FontAwesome.ARROW_UP);
		Button reverseBtn = new Button("", (Button.ClickListener) event -> {
			gpioMotor.reverse();
			setCaption(motorName.getValue() + "  Reverse");
		});
		reverseBtn.setIcon(FontAwesome.ARROW_DOWN);
		Button stopBtn = new Button("", (Button.ClickListener) event -> {
			gpioMotor.stop();
			setCaption(motorName.getValue() + "  Stopped");
		});
		stopBtn.setIcon(FontAwesome.STOP);
		loadHoriz.addComponents(switchLoad, configBtn);
		actionHoriz.addComponents(forwardBtn, reverseBtn, stopBtn);
	}

	private void setConfigMode() {
		rootLayout.removeAllComponents();
		rootLayout.addComponents(configHoriz, select, interval, sequence, stepsPerRevolution);
		setCaption("Motor Configuration");
	}

	private void setActionMode() {
		rootLayout.removeAllComponents();
		rootLayout.addComponents(loadHoriz, actionHoriz);
		setCaption(motorName.getValue());
	}

	@SuppressWarnings("unchecked")
	private void createMotor() {
		gpioMotor = new VaadinMotor(gpioController, (Collection<Pin>) select.getValue(), 
				Long.parseLong(interval.getValue()), true,
				Integer.parseInt(stepsPerRevolution.getValue()));
	}

	private void setMotorState(boolean state) {
		if (state) {
			gpioMotor.load();
		} else {
			gpioMotor.shutdown();
		}
	}
}
