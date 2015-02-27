package com.kamilu.pi4jweb;

import com.alsnightsoft.vaadin.widgets.canvasplus.CanvasPlus;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class VaadinMPU6050Presenter extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private VerticalLayout rootLayout;
	private CanvasPlus canvas;
	private MPU6050Device device;
	private Label label;
	private ToggleButton power;

	public VaadinMPU6050Presenter() {
		canvas = new CanvasPlus();
		canvas.setHeight("100px");
		canvas.setWidth("100px");
		label = new Label("Test");
		canvas.setVisible(false);
		label.setVisible(false);
		power = new ToggleButton(false, "");
		power.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (!power.getValue()) {
					canvas.setVisible(true);
					label.setVisible(true);
					device.startReading();
				} else {
					canvas.setVisible(false);
					label.setVisible(false);
					device.stop();
				}
				power.toggle();
			}
		});
		device = new MPU6050Device(canvas, label);
		rootLayout = new VerticalLayout(power, label);
		setCompositionRoot(rootLayout);
	}
}
