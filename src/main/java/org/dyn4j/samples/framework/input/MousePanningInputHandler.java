package org.dyn4j.samples.framework.input;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.dyn4j.samples.framework.Camera;

public class MousePanningInputHandler extends AbstractMouseInputHandler implements InputHandler {
	private boolean panning;
	private Point start;
	
	public MousePanningInputHandler(Component component, Camera camera) {
		super(component, camera, MouseEvent.BUTTON1);
		this.panning = false;
	}

	@Override
	protected void onMousePressed(Point point) {
		super.onMousePressed(point);
		this.handleMouseStart(point);
	}

	@Override
	protected void onMouseDrag(Point start, Point current) {
		super.onMouseDrag(start, current);
		this.handleMouseDrag(current);
	}
	
	@Override
	protected void onMouseRelease() {
		this.clearPanningState();
		super.onMouseRelease();
	}

	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		if (!flag) {
			this.clearPanningState();
		}
	}
	
	@Override
	public boolean isActive() {
		return this.panning;
	}

	@Override
	public void uninstall() {
		super.uninstall();
		this.clearPanningState();
	}
	
	private boolean handleMouseStart(Point start) {
    	this.panning = true;
    	this.start = start;
		return true;
	}
	
	private boolean handleMouseDrag(Point current) {
    	this.panning = true;
    	
    	double x = current.getX() - this.start.getX();
    	double y = current.getY() - this.start.getY();
    	
    	this.camera.offsetX += x;
    	this.camera.offsetY -= y;
    	
    	this.start = current;
		
		return true;
	}
	
	private void clearPanningState() {
		this.panning = false;
		this.start = null;
	}
}
