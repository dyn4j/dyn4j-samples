/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.samples;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A scene where a set of different sized bodies are strung together with
 * distance = 0 DistanceJoints that causes the bodies to try to self
 * organize.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.1.1
 */
public class Organize extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -2350301592218819726L;

	/** A point for tracking the mouse click */
	private Point point;
	
	/** A list of bodies that can be joined (i.e. excludes the static bodies) */
	private List<SimulationBody> bodies;
	
	/** True if the joints have been added */
	private boolean jointsAdded = false;
	
	/**
	 * A custom mouse adapter for listening for mouse clicks.
	 * @author William Bittle
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			// store the mouse click postion for use later
			point = new Point(e.getX(), e.getY());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// store the mouse click postion for use later
			point = new Point(e.getX(), e.getY());
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			point = null;
		}
	}

	/**
	 * Default constructor.
	 */
	public Organize() {
		super("Organize", 48.0);
		
		MouseAdapter ml = new CustomMouseAdapter();
		this.canvas.addMouseMotionListener(ml);
		this.canvas.addMouseWheelListener(ml);
		this.canvas.addMouseListener(ml);
		
		this.setMousePanningEnabled(false);
		this.setMousePickingEnabled(false);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		SimulationBody bottom = new SimulationBody();
		bottom.addFixture(Geometry.createRectangle(15.0, 1.0));
	    bottom.translate(new Vector2(0, -5));
	    bottom.setMass(MassType.INFINITE);
	    world.addBody(bottom);
	    
		SimulationBody top = new SimulationBody();
		top.addFixture(Geometry.createRectangle(15.0, 1.0));
	    top.translate(new Vector2(0, 5));
	    top.setMass(MassType.INFINITE);
	    world.addBody(top);
	    
		SimulationBody left = new SimulationBody();
		left.addFixture(Geometry.createRectangle(1.0, 15.0));
	    left.translate(new Vector2(-5, 0));
	    left.setMass(MassType.INFINITE);
	    world.addBody(left);
	    
		SimulationBody right = new SimulationBody();
		right.addFixture(Geometry.createRectangle(1.0, 15.0));
	    right.translate(new Vector2(5, 0));
	    right.setMass(MassType.INFINITE);
	    world.addBody(right);

	    Random r = new Random(123);
	    bodies = new ArrayList<SimulationBody>();
	    for (int i = 0; i < 20; i++) {
			SimulationBody body = new SimulationBody();
			body.addFixture(Geometry.createCircle(r.nextDouble()));
		    body.translate(r.nextDouble(),0);
		    body.setMass(new Mass(new Vector2(), 1, 10));
		    world.addBody(body);
		    
		    bodies.add(body);
	    }
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		// convert the point from panel space to world space
		Point p = this.point;
		if (p != null && !jointsAdded) {
			jointsAdded = true;
			Random r = new Random(44);
			Map<String, Boolean> used = new HashMap<String, Boolean>();
			for (int i = 0; i < 10; i++) {
				int n = r.nextInt(19);
				int m = r.nextInt(19);
				String name = (n < m ? n : m) + "," + (n > m ? n : m);
				if (n != m && !used.containsKey(name)) {
					used.put(name, true);
					SimulationBody b1 = this.bodies.get(n);
					SimulationBody b2 = this.bodies.get(m);

					DistanceJoint<SimulationBody> dj = new DistanceJoint<SimulationBody>(b1, b2, b1.getWorldCenter(), b2.getWorldCenter());
					dj.setCollisionAllowed(true);
					dj.setRestDistance(0.0);
					dj.setFrequency(2);
					dj.setDampingRatio(1.0);
					world.addJoint(dj);
				}
			}
		} else if (jointsAdded) {
			// once we've joined the bodies, re-enable picking/panning
			this.setMousePanningEnabled(true);
			this.setMousePickingEnabled(true);
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Organize simulation = new Organize();
		simulation.run();
	}
}
