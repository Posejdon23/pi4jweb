package com.kamilu.pi4jweb;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

public class VaadinCameraController extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private VerticalLayout layout;
	private Image image;
	private Button capture;

	public VaadinCameraController() {
		image = new Image();
		image.setImmediate(true);
		capture = new Button("Capture Image", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				CameraSource cameraStreamSource = new CameraSource();
				StreamResource source = new StreamResource(cameraStreamSource, "png");
				source.setCacheTime(0);
				image.setSource(source);
				image.markAsDirty();
				source.setFilename("camera-" + //
						new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())//
						+ ".png");
			}
		});
		layout = new VerticalLayout(capture, image);
		layout.setSpacing(true);
		layout.setMargin(true);
		setCompositionRoot(layout);
	}
}
