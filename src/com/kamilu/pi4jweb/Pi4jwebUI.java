package com.kamilu.pi4jweb;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("pi4jweb")
@Push
public class Pi4jwebUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(widgetset = "com.kamilu.pi4jweb.widgetset.Pi4jwebWidgetset", productionMode = false, ui = Pi4jwebUI.class)
	public static class Servlet extends VaadinServlet {
	}

	private VerticalLayout main;

	@Override
	protected void init(VaadinRequest request) {
		VaadinGPIOController controller = new VaadinGPIOController();
		VaadinCameraController camera = new VaadinCameraController();

		Label header = new Label("Log to your Pi");
		TextField username = new TextField("Username", "pi");
		PasswordField password = new PasswordField("Password");
		Button logIn = new Button("Log In", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				main.removeAllComponents();
				main.addComponent(controller);
				main.addComponent(camera);
			}
		});
		HorizontalLayout headerHoriz = new HorizontalLayout(header);
		HorizontalLayout horiz1 = new HorizontalLayout(username);
		HorizontalLayout horiz2 = new HorizontalLayout(password);
		HorizontalLayout btnHoriz = new HorizontalLayout(logIn);
		VerticalLayout mainVert = new VerticalLayout(headerHoriz, horiz1, horiz2, btnHoriz);
		mainVert.setComponentAlignment(headerHoriz, Alignment.TOP_CENTER);
		mainVert.setComponentAlignment(horiz1, Alignment.TOP_LEFT);
		mainVert.setComponentAlignment(horiz2, Alignment.TOP_LEFT);
		mainVert.setComponentAlignment(btnHoriz, Alignment.TOP_RIGHT);
		headerHoriz.setComponentAlignment(header, Alignment.TOP_CENTER);
		mainVert.setWidth("500px");
		horiz1.setComponentAlignment(username, Alignment.TOP_LEFT);
		horiz2.setComponentAlignment(password, Alignment.TOP_LEFT);
		btnHoriz.setComponentAlignment(logIn, Alignment.TOP_RIGHT);
		main = new VerticalLayout(mainVert);
		main.setComponentAlignment(mainVert, Alignment.TOP_CENTER);
		setContent(main);
		UI.getCurrent().getPage().setTitle("Raspberry Pi Controller");
	}
}