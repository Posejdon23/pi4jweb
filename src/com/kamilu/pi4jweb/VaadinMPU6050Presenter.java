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
					device.startReading();
				} else {
					label.setVisible(false);
					canvas.setVisible(false);
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
				double radians = Math.toRadians(Double.valueOf(xAngle.getValue()));
				canvas.clear();
				canvas.stroke();
				canvas.setStrokeStyle("#f00");
				int x1 = 200, x2 = 400;
				int b = 150;
				canvas.moveTo(x1, x1 * radians + b);
				canvas.lineTo(x2, -x1 * radians + b);
				canvas.stroke();
			}
		});
		device = new MPU6050Device(canvas);
		rootLayout = new VerticalLayout(power, label, canvas, xAngle, xBtn);
		setCompositionRoot(rootLayout);
	}
}
