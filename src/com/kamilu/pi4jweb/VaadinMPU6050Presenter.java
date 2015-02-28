package com.kamilu.pi4jweb;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class VaadinMPU6050Presenter extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private VerticalLayout rootLayout;
	private MPU6050Device device;
	private Label label;
	private ToggleButton power;

	public VaadinMPU6050Presenter() {
		label = new Label("Test");
		label.setVisible(false);
		label.setImmediate(true);
		power = new ToggleButton(false, "");
		power.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (!power.getValue()) {
					label.setVisible(true);
					device.startReading();
				} else {
					label.setVisible(false);
					device.stopI2cBus();
				}
				power.toggle();
			}
		});
		device = new MPU6050Device(label);
		rootLayout = new VerticalLayout(power, label);
		setCompositionRoot(rootLayout);
	}
}
