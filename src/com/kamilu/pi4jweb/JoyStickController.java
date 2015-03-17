package com.kamilu.pi4jweb;

import java.io.Serializable;

import com.alsnightsoft.vaadin.widgets.canvasplus.CanvasPlus;
import com.vaadin.shared.MouseEventDetails;

public class JoyStickController extends CanvasPlus implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean drag = false;

	public JoyStickController() {

		setWidth("300");
		setHeight("300");
		setLineWidth(15.0);
		setLineCap("round");
		setLineJoin("round");
		addMouseMoveListener(new CanvasPlus.CanvasMouseMoveListener() {
			@Override
			public void onMove(MouseEventDetails mouseDetails) {
				if (drag) {
					drawCircle(mouseDetails.getRelativeX(), mouseDetails.getRelativeY());
				}
			}
		});
		addClickUpListener(new CanvasPlus.CanvasClickUpListener() {
			@Override
			public void onClickUp(MouseEventDetails mouseDetails) {
				drawCircle(mouseDetails.getRelativeX(), mouseDetails.getRelativeY());
				drag = false;
			}
		});

		addClickDownListener(new CanvasPlus.CanvasClickDownListener() {
			@Override
			public void onClickDown(MouseEventDetails mouseDetails) {
				drawCircle(mouseDetails.getRelativeX(), mouseDetails.getRelativeY());
				drag = true;
			}
		});
		clear();
		saveContext();
		restoreContext();
		beginPath();
		setLineWidth(15.0);
		setStrokeStyle("#f10");
		// moveTo(150, 150);
		arc(150d, 150d, 20d, 0d, 2 * Math.PI, Boolean.TRUE);
		// lineTo(relativeX, relativeY);
		// arc(150d, 150d, 20d, 0d, 2 * Math.PI, Boolean.TRUE);
		stroke();
	}
	private void drawCircle(double x, double y) {
		clear();
		saveContext();
		restoreContext();
		beginPath();
		setStrokeStyle("#f10");
		// moveTo(150, 150);
		arc(x, y, 20d, 0d, 2 * Math.PI, Boolean.TRUE);
		// lineTo(relativeX, relativeY);
		// arc(150d, 150d, 20d, 0d, 2 * Math.PI, Boolean.TRUE);
		stroke();
	}
}
