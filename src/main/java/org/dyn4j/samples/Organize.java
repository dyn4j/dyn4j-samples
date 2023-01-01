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
package org.dyn4j.samples;

import java.awt.event.KeyEvent;
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
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;

/**
 * A scene where a set of different sized bodies are strung together with
 * distance = 0 DistanceJoints that causes the bodies to try to self
 * organize.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.1.1
 */
public class Organize extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -2350301592218819726L;

	/** A list of bodies that can be joined (i.e. excludes the static bodies) */
	private List<SimulationBody> bodies;
	
	private final BooleanStateKeyboardInputHandler organize;
	
	/** True if the joints have been added */
	private boolean jointsAdded = false;
	
	/**
	 * Default constructor.
	 */
	public Organize() {
		super("Organize");
		
		this.organize = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_O);
		
		this.organize.install();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#printControls()
	 */
	@Override
	protected void printControls() {
		super.printControls();
		
		printControl("Organize", "o", "Use the o key to toggle the distance joints");
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
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 48.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#handleEvents()
	 */
	@Override
	protected void handleEvents() {
		super.handleEvents();
		
		if (this.organize.isActiveButNotHandled()) {
			this.organize.setHasBeenHandled(true);
			
			if (!this.jointsAdded) {
				this.jointsAdded = true;
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
						dj.setSpringEnabled(true);
						dj.setSpringDamperEnabled(true);
						dj.setSpringFrequency(2);
						dj.setSpringDampingRatio(1.0);
						this.world.addJoint(dj);
					}
				}
			} else {
				this.world.removeAllJoints();
				this.jointsAdded = false;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		this.jointsAdded = false;
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
