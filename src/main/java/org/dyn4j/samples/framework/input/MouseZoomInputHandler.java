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

public final class MouseZoomInputHandler extends AbstractMouseInputHandler implements InputHandler {
	private final Object lock;
	
	private double scale;

	public MouseZoomInputHandler(Component component, int button) {
		super(component, button);
		this.lock = new Object();
		this.scale = 1.0;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	protected void onMouseWheel(double rotation) {
		super.onMouseWheel(rotation);
		
		// this happen sometimes on trackpads
		if (rotation == 0)
			return;
		
    	// input from the mouse should be queued for processing
    	// to avoid mid-render changes to the camera
    	// input from AWT is coming in from the main thread of
    	// AWT, but rendering is performed on a different thread
    	// as such we should lock on the changes just to be sure
    	// we don't lose information
		synchronized (this.lock) {
			if (rotation > 0) {
				this.scale *= 0.8;
			} else {
				this.scale *= 1.2;
			}	
		}
	}
	
	public double getScaleAndReset() {
		synchronized (this.lock) {
			double scale = this.scale;
			this.scale = 1.0;
			return scale;
		}
	}
}
