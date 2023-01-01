/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.samples.framework.input;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.dyn4j.geometry.Vector2;

public final class MousePanningInputHandler extends AbstractMouseInputHandler implements InputHandler {
	private final Object lock;
	
	private boolean panning;
	private Point start;
	
	private double x;
	private double y;
	
	public MousePanningInputHandler(Component component) {
		super(component, MouseEvent.BUTTON1);
		this.panning = false;
		this.lock = new Object();
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
    	
    	// input from the mouse should be queued for processing
    	// to avoid mid-render changes to the camera
    	// input from AWT is coming in from the main thread of
    	// AWT, but rendering is performed on a different thread
    	// as such we should lock on the changes just to be sure
    	// we don't lose information
    	synchronized (this.lock) {
        	this.x += x;
        	this.y -= y;	
		}
    	
    	this.start = current;
		
		return true;
	}
	
	private void clearPanningState() {
		this.panning = false;
		this.start = null;
	}
	
	public Vector2 getOffsetAndReset() {
		synchronized (this.lock) {
			Vector2 offset = new Vector2(this.x, this.y);
			this.x = 0;
			this.y = 0;
			return offset;
		}
	}
}
