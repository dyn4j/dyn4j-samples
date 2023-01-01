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
import java.util.Iterator;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.World;
import org.dyn4j.world.result.DetectResult;

public class MousePickingInputHandler extends AbstractMouseInputHandler implements InputHandler {
	private final Object lock;
	private final Camera camera;
	private final World<SimulationBody> world;
	
	// state maintained by the main thread
	private boolean dragging;
	private Vector2 point;
	private SimulationBody body;
	
	// state maintained by the render thread
	private Joint<SimulationBody> mouseHandle;
	
	public MousePickingInputHandler(Component component, Camera camera, World<SimulationBody> world) {
		super(component, MouseEvent.BUTTON1);
		this.lock = new Object();
		this.camera = camera;
		this.world = world;
	}

	@Override
	protected void onMousePressed(Point point) {
		super.onMousePressed(point);
		this.handleMouseStartOrDrag(point);
	}

	@Override
	protected void onMouseDrag(Point start, Point current) {
		super.onMouseDrag(start, current);
		this.handleMouseStartOrDrag(current);
	}
	
	@Override
	protected void onMouseRelease() {
		this.onReleaseCleanUp();
		super.onMouseRelease();
	}

	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		if (!flag) {
			this.onReleaseCleanUp();
		}
	}
	
	@Override
	public boolean isActive() {
		return this.dragging;
	}

	@Override
	public void uninstall() {
		super.uninstall();
		this.onReleaseCleanUp();
	}
	
	private boolean handleMouseStartOrDrag(Point point) {
		Vector2 p = this.camera.toWorldCoordinates(this.component.getWidth(), this.component.getHeight(), point);
		
		synchronized (this.lock) {
			this.point = p;
			
			if (!this.dragging) {
				SimulationBody body = this.getBodyAt(p);
				if (body != null) {
					this.dragging = true;
					this.body = body;
					return true;
				}
			} else {
				return true;
			}
		}
		
		return false;
	}
	
	private void onReleaseCleanUp() {
		synchronized (this.lock) {
			this.point = null;
			this.body = null;
			this.dragging = false;
		}
	}
	
	public SimulationBody getBody() {
		if (this.mouseHandle != null) {
			return this.mouseHandle.getBody(0);
		}
		return null;
	}
	
	private SimulationBody getBodyAt(Vector2 p) {
		SimulationBody body = null;
		
		// detect bodies under the mouse pointer
		AABB aabb = new AABB(new Vector2(p.x, p.y), 0.0001);
		Iterator<DetectResult<SimulationBody, BodyFixture>> it = this.world.detectIterator(aabb, null);
		while (it.hasNext()) {
			SimulationBody b = it.next().getBody();
			
			// ignore infinite bodies
			if (b.getMass().isInfinite()) {
				continue;
			}
			
			// check point contains and take the first
			// one found
			if (b.contains(p)) {
				body = b;
				break;
			}
		}
		
		return body;
	}
	
	private Joint<SimulationBody> createControlJoint(SimulationBody body, Vector2 p) {
		PinJoint<SimulationBody> pj = new PinJoint<SimulationBody>(body, new Vector2(p.x, p.y));
		pj.setSpringEnabled(true);
		pj.setSpringFrequency(4.0);
		pj.setSpringDamperEnabled(true);
		pj.setSpringDampingRatio(0.3);
		pj.setMaximumSpringForceEnabled(true);
		pj.setMaximumSpringForce(500);
		return pj;
	}
	
	public void updateMousePickingState() {
		boolean dragging = false;
		Vector2 point = null;
		SimulationBody body = null;
		
		synchronized (this.lock) {
			dragging = this.dragging;
			point = this.point;
			body = this.body;
		}
		
		// 1. mouse picking begins
		if (dragging && this.mouseHandle == null && point != null) {
			// create a joint with the body
			Joint<SimulationBody> joint = this.createControlJoint(body, point);
			this.mouseHandle = joint;
			this.world.addJoint(joint);
			this.onPickingStart(body);
			return;
		}
		
		// 2. mouse picking continues
		if (dragging && this.mouseHandle != null && point != null) {
			Joint<SimulationBody> joint = this.mouseHandle;
			if (joint instanceof PinJoint) {
				PinJoint<?> pj = (PinJoint<?>)joint;
				pj.setTarget(new Vector2(point.x, point.y));
			}
			return;
		}
		
		// 3. mouse picking ends
		if (!dragging) {
			if (this.mouseHandle != null) {
				this.world.removeJoint(this.mouseHandle);
				this.onPickingEnd(body);
			}
			this.mouseHandle = null;
			return;
		}
	}
	
	public void onPickingStart(SimulationBody body) {
		
	}
	
	public void onPickingEnd(SimulationBody body) {
		
	}
}
