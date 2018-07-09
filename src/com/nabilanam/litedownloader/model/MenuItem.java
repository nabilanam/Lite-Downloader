package com.nabilanam.litedownloader.model;

/**
 *
 * @author nabil
 */
public enum MenuItem {
	START(0, "Start"), 
	PAUSE(1, "Pause"), 
	RESUME(2, "Resume"), 
	STOP(3, "Stop"), 
	COPY_LINK(4, "Copy link"),
	REMOVE_LINK(5,"Remove link"), 
	REMOVE_FILE(6,"Remove link & file"), 
	LAUNCH_FILE(7, "Open file"), 
	LAUNCH_FOLDER(8, "Open folder");

	private final int id;
	private final String text;

	private MenuItem(int id, String text) {
		this.id = id;
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}
}
