package com.kamilu.pi4jweb;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

public class ToggleButton extends Button {

	private static final long serialVersionUID = -4255076152983488270L;
	private boolean state;
	private Map<Boolean, String> captions;
	private Map<Boolean, Resource> icons;
	private Map<Boolean, String> styles;
	private String caption;

	public ToggleButton(boolean state, String caption) {
		this.state = state;
		this.caption = caption;
		setup();
	}

	public ToggleButton(boolean state, String caption, Button.ClickListener listener) {
		addClickListener(listener);
		this.state = state;
		this.caption = caption;
		setup();
	}

	private void setup() {
		captions = new HashMap<Boolean, String>(2);
		styles = new HashMap<Boolean, String>(2);
		icons = new HashMap<Boolean, Resource>(2);
		captions.put(true, caption + "ON");
		captions.put(false, caption + "OFF");
		styles.put(true, "toggle-btn-on");
		styles.put(false, "toggle-btn-off");
		icons.put(true, FontAwesome.CIRCLE);
		icons.put(false, FontAwesome.CIRCLE_O);
		update();
	}

	private void update() {
		setCaption(captions.get(state));
		setIcon(icons.get(state));
		setStyleName(styles.get(state));
	}

	public boolean getValue() {
		return state;
	}

	public void toggle() {
		state = !state;
		update();
	}

	public void setState(boolean state) {
		this.state = state;
		update();
	}

	public void setONStyle(String caption, Resource icon) {
		captions.put(true, caption);
		icons.put(true, icon);
		update();
	}

	public void setOFFStyle(String caption, Resource icon) {
		captions.put(false, caption);
		icons.put(false, icon);
		update();
	}

	public void setCaptionON(String caption) {
		captions.put(true, caption);
		update();
	}

	public void setCaptionOFF(String caption) {
		captions.put(false, caption);
		update();
	}
}
