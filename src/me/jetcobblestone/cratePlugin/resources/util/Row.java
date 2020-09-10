package me.jetcobblestone.cratePlugin.resources.util;

public class Row {
	private int row;
	private boolean direction;
	
	public Row(int row, boolean direction) {
		this.row = row;
		this.direction = direction;
	}
	
	public int getRow() {
		return row;
	}
	public boolean getDirection() {
		return direction;
	}
}
