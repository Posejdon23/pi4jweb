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

public class PiController extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private GpioController gpioController;
	private VerticalLayout main, workspacePins;
	private HorizontalLayout menu, mainWS, workspaceMotors;
	private Button addGpio, addDevice;
	private ToggleButton load, showGyro, showCamera;
	private VaadinMPU6050Presenter gyroController;
	private VaadinCameraController cameraController;

	public PiController() {
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
		addGpio = new Button("Add Pin");
		addDevice = new Button("Add Device");
		showGyro = new ToggleButton(false, "Show MPU-6050");
		showCamera = new ToggleButton(false, "Show Camera");
		addGpio.setVisible(false);
		addDevice.setVisible(false);
		showGyro.setVisible(false);
		showCamera.setVisible(false);
		addGpio.setIcon(FontAwesome.PLUS);
		addDevice.setIcon(FontAwesome.PLUS);
		showGyro.setIcon(FontAwesome.PLUS);
		showCamera.setIcon(FontAwesome.PLUS);
		menu = new HorizontalLayout(load, addGpio, addDevice, showGyro, showCamera);
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
					showGyro.setVisible(true);
					showCamera.setVisible(true);
				} else {
					try {
						gpioController.shutdown();
						Notification.show("Disconnected", ASSISTIVE_NOTIFICATION);
					} catch (Exception | UnsatisfiedLinkError e) {
						Notification.show("Problem with shutdown", ERROR_MESSAGE);
					}
					addGpio.setVisible(false);
					addDevice.setVisible(false);
					showGyro.setVisible(false);
					showCamera.setVisible(false);
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
		showGyro.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (!showGyro.getValue()) {
					if (gyroController == null) {
						gyroController = new VaadinMPU6050Presenter();
					}
					workspacePins.addComponent(gyroController);
				} else {
					workspacePins.removeComponent(gyroController);
				}
				showGyro.toggle();
			}
		});
		showCamera.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (!showCamera.getValue()) {
					if (cameraController == null) {
						cameraController = new VaadinCameraController();
					}
					workspacePins.addComponent(cameraController);
				} else {
					workspacePins.removeComponent(cameraController);
				}
				showCamera.toggle();

			}
		});
	}
}
