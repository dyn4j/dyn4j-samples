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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class AbstractMouseInputHandler extends AbstractInputHandler implements InputHandler {
	
	protected final Component component;
	protected final int button;
	
	private final MouseAdapter mouseAdapter;
	
	private Point dragCurrent;
	private Point dragStart;
	
	public AbstractMouseInputHandler(Component component, int button) {
		this.component = component;
		this.button = button;
		this.mouseAdapter = new CustomMouseAdapter();
	}

	/**
	 * A custom mouse adapter to track mouse drag events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isConsumed()) return;
			
			if (e.getButton() == button) {
				// store the mouse click postion for use later
				dragCurrent = new Point(e.getX(), e.getY());
				dragStart = dragCurrent;
				if (isEnabled() && !isDependentBehaviorActive()) {
					onMousePressed(dragStart);
				}
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.isConsumed()) return;
			
			dragCurrent = new Point(e.getX(), e.getY());
			if (isEnabled() && !isDependentBehaviorActive() && dragStart != null) {
				onMouseDrag(dragStart, dragCurrent);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isConsumed()) return;
			
			if (e.getButton() == button) {
				dragCurrent = null;
				dragStart = null;
				if (isEnabled() && !isDependentBehaviorActive()) {
					onMouseRelease();
				}
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isConsumed()) return;
			
			double wheelRotation = e.getWheelRotation();
			if (isEnabled() && !isDependentBehaviorActive()) {
				onMouseWheel(wheelRotation);
			}
		}
	}

	@Override
	public void install() {
		this.component.addMouseListener(this.mouseAdapter);
		this.component.addMouseMotionListener(this.mouseAdapter);
		this.component.addMouseWheelListener(this.mouseAdapter);
	}
	
	@Override
	public void uninstall() {
		this.component.removeMouseListener(this.mouseAdapter);
		this.component.removeMouseMotionListener(this.mouseAdapter);
		this.component.removeMouseWheelListener(this.mouseAdapter);
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		if (!flag) {
			this.dragCurrent = null;
			this.dragStart = null;
		}
	}
	
	protected void onMousePressed(Point point) {
		
	}
	
	protected void onMouseDrag(Point start, Point current) {
		
	}
	
	protected void onMouseRelease() {
		
	}
	
	protected void onMouseWheel(double rotation) {
		
	}
}
