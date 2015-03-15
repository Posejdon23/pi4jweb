package com.kamilu.pi4jweb;

import com.alsnightsoft.vaadin.widgets.canvasplus.CanvasPlus;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class VaadinMPU6050Presenter extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private VerticalLayout rootLayout;
	private MPU6050Device device;
	private Label label;
	private CanvasPlus canvas;
	private ToggleButton power;
	private TextField xAngle;
	private Button xBtn;

	public VaadinMPU6050Presenter() {
		xAngle = new TextField();
		xAngle.setCaption("x1");
		xBtn = new Button("Set");
		label = new Label("Test");
		canvas = new CanvasPlus();
		canvas.setWidth("600");
		canvas.setHeight("300");
		canvas.setCaption("Gyroscope");
		label.setVisible(false);
		label.setImmediate(true);
		power = new ToggleButton(false, "");
		power.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (!power.getValue()) {
					label.setVisible(true);
					canvas.setVisible(true);
					canvas.setLineWidth(8.0);
					device.startReading();
				} else {
					label.setVisible(false);
					canvas.setVisible(false);
					device.setReading(false);
					device.stopI2cBus();
				}
				power.toggle();
			}
		});
		xBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				drawLine();
			}

			private void drawLine() {
				int valueOf;
				try {
					valueOf = Integer.valueOf(xAngle.getValue());
				} catch (NumberFormatException e) {
					return;
				}
				double[] xCoord = MathFunctions.getCoord(valueOf, 150, 150, 150);
				double[] yCoord = MathFunctions.getCoord(valueOf, 150, 450, 150);
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
		device = new MPU6050Device(canvas);
		rootLayout = new VerticalLayout(power, label, canvas, xAngle, xBtn);
		setCompositionRoot(rootLayout);
	}
}
