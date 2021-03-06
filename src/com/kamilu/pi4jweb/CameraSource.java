package com.kamilu.pi4jweb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import com.vaadin.server.StreamResource.StreamSource;

public class CameraSource implements StreamSource {

	private static final long serialVersionUID = 1L;

	public InputStream getStream() {
		Mat matrix = new Mat(480, 640, 16);
		VideoCapture videoCapture = new VideoCapture(0);
		videoCapture.retrieve(matrix);
		ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
		try {
			ImageIO.write(matToBufferedImage(matrix), "png", imagebuffer);
		} catch (IOException e) {
		}
		videoCapture.release();
		return new ByteArrayInputStream(imagebuffer.toByteArray());
	}

	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
			case 1 :
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3 :
				type = BufferedImage.TYPE_3BYTE_BGR;
				// bgr to rgb
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default :
				return null;
		}
		BufferedImage image = new BufferedImage(cols, rows, type);
		image.getRaster().setDataElements(0, 0, cols, rows, data);
		return image;
	}
}