package com.kamilu.pi4jweb;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

public class VaadinCameraController extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private VerticalLayout main;
	private Image capturedImage;

	public VaadinCameraController() {
		main = new VerticalLayout();
		main.setSpacing(true);
		main.setMargin(true);
		capturedImage = new Image("");
		main.addComponent(capturedImage);
		Button start = new Button("Capture image", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				StreamResource.StreamSource imagesource = new CameraSource();
				StreamResource resource = new StreamResource(imagesource, "camera.jpg");
				capturedImage.setSource(resource);
				capturedImage.markAsDirty();
			}
		});

		main.addComponent(start);
		setCompositionRoot(main);
	}
}
