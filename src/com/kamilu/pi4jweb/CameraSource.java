package com.kamilu.pi4jweb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.vaadin.server.StreamResource.StreamSource;

public class CameraSource implements StreamSource, Serializable {
	private static final long serialVersionUID = 1L;
	private ByteArrayOutputStream imagebuffer = null;
	private OpenCVFrameGrabber grabber;

	public InputStream getStream() {
		try {
			grabber = OpenCVFrameGrabber.createDefault(0);
			grabber.start();
			IplImage iplImage = grabber.grab();
			grabber.stop();
			BufferedImage image = iplImage.getBufferedImage();
			imagebuffer = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", imagebuffer);
			return new ByteArrayInputStream(imagebuffer.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}