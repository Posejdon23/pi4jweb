package com.kamilu.pi4jweb;

import static com.vaadin.ui.Notification.Type.ASSISTIVE_NOTIFICATION;
import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class VaadinGPIOController extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private GpioController gpioController;
	private VerticalLayout main, workspacePins;
	private HorizontalLayout menu, mainWS, workspaceMotors;
	private Button addGpio;
	private Button addDevice;
	private Button addGyro;
	private ToggleButton load;

	public VaadinGPIOController() {
		bulidMenu();
		workspacePins = new VerticalLayout();
		workspaceMotors = new HorizontalLayout();
		mainWS = new HorizontalLayout(workspacePins, workspaceMotors);
		main = new VerticalLayout(menu, mainWS);
		main.setMargin(true);
		main.setSpacing(true);
		mainWS.setSpacing(true);
		workspacePins.setSpacing(true);
		workspaceMotors.setSpacing(true);
		setCompositionRoot(main);
	}
	private void bulidMenu() {
		load = new ToggleButton(false, "GPIO Controller ");
		addGpio = new Button("Add pin");
		addDevice = new Button("Add device");
		addGyro = new Button("Add MPU-6050 Device");
		addGpio.setVisible(false);
		addDevice.setVisible(false);
		addGyro.setVisible(false);
		addGpio.setIcon(FontAwesome.PLUS);
		addDevice.setIcon(FontAwesome.PLUS);
		addGyro.setIcon(FontAwesome.PLUS);
		menu = new HorizontalLayout(load, addGpio, addDevice, addGyro);
		menu.setSpacing(true);

		load.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {

				if (!load.getValue()) {
					try {
						gpioController = GpioFactory.getInstance();
						Notification.show("Connected", ASSISTIVE_NOTIFICATION);
					} catch (Exception | UnsatisfiedLinkError e) {
						Notification.show("Problem with connection", ERROR_MESSAGE);
					}
					addGpio.setVisible(true);
					addDevice.setVisible(true);
					addGyro.setVisible(true);
				} else {
					try {
						gpioController.shutdown();
						Notification.show("Disconnected", ASSISTIVE_NOTIFICATION);
					} catch (Exception | UnsatisfiedLinkError e) {
						Notification.show("Problem with shutdown", ERROR_MESSAGE);
					}
					addGpio.setVisible(false);
					addDevice.setVisible(false);
					addGyro.setVisible(false);
				}
				load.toggle();
			}
		});
		addDevice.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				workspaceMotors.addComponent(new VaadinMotorPresenter(gpioController));
			}
		});
		addGpio.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				workspacePins.addComponent(new VaadinPinPresenter(gpioController));
			}
		});
		addGyro.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				workspacePins.addComponent(new VaadinMPU6050Presenter());
			}
		});
	}
}
