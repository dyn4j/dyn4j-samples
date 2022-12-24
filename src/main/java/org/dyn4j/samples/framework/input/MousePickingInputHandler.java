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

	private final World<SimulationBody> world;
	
	/** A joint for mouse picking */
	private Joint<SimulationBody> mouseHandle;
	
	public MousePickingInputHandler(Component component, Camera camera, World<SimulationBody> world) {
		super(component, camera, MouseEvent.BUTTON1);
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
		this.removeMouseHandleJoint();
		super.onMouseRelease();
	}

	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		if (!flag) {
			this.removeMouseHandleJoint();
		}
	}
	
	@Override
	public boolean isActive() {
		return this.mouseHandle != null;
	}

	@Override
	public void uninstall() {
		super.uninstall();
		this.removeMouseHandleJoint();
	}
	
	private boolean handleMouseStartOrDrag(Point point) {
		Vector2 p = this.toWorldCoordinates(point);
		
		if (!this.isActive()) {
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
			
			if (body != null) {
				PinJoint<SimulationBody> pj = new PinJoint<SimulationBody>(body, new Vector2(p.x, p.y));
				pj.setSpringEnabled(true);
				pj.setSpringFrequency(8.0);
				pj.setSpringDamperEnabled(true);
				pj.setSpringDampingRatio(0.2);
				pj.setMaximumSpringForceEnabled(true);
				pj.setMaximumSpringForce(500);
				
				this.world.addJoint(pj);
				this.mouseHandle = pj;
				
				return true;
			}
		} else {
			if (this.mouseHandle instanceof PinJoint) {
				((PinJoint<?>)this.mouseHandle).setTarget(new Vector2(p.x, p.y));
			}
			return true;
		}
		
		return false;
	}
	
	private void removeMouseHandleJoint() {
		Joint<SimulationBody> joint = this.mouseHandle;
		this.mouseHandle = null;
		this.world.removeJoint(joint);
	}
	
	public SimulationBody getBody() {
		if (this.mouseHandle != null) {
			return this.mouseHandle.getBody(0);
		}
		return null;
	}
}
