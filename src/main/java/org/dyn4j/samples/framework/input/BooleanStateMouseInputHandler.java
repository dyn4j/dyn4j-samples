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

public final class BooleanStateMouseInputHandler extends AbstractMouseInputHandler {
	private final Object lock;
	
	private boolean active;
	private Point location;
	private boolean hasBeenHandled;
	
	public BooleanStateMouseInputHandler(Component component, int button) {
		super(component, button);
		this.lock = new Object();
	}

	@Override
	protected void onMousePressed(Point point) {
		super.onMousePressed(point);
		synchronized (this.lock) {
			boolean active = this.active;
			
			this.active = true;
			this.location = point;

			// if the state transitioned from inactive to active
			// flag that it needs to be handled
			if (!active) {
				this.hasBeenHandled = false;
			}
		}
	}

	@Override
	protected void onMouseRelease() {
		this.active = false;
		super.onMouseRelease();
	}

	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		if (!flag) {
			this.clearState();
		}
	}
	
	@Override
	public void uninstall() {
		super.uninstall();
		this.clearState();
	}
	
	private void clearState() {
		this.active = false;
		this.location = null;
		this.hasBeenHandled = false;
	}
	
	public Point getMouseLocation() {
		synchronized (this.lock) {
			return this.location;
		}
	}

	@Override
	public boolean isActive() {
		return this.active;
	}
	
	public boolean isActiveButNotHandled() {
		if (this.hasBeenHandled)
			return false;
		
		return this.active;
	}
	
	public void setHasBeenHandled(boolean hasBeenHandled) {
		this.hasBeenHandled = hasBeenHandled;
	}
}
