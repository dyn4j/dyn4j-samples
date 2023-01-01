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

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A scene where we create a suspension bridge via RevoluteJoints.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.1.1
 */
public class Bridge extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -2350301592218819726L;

	/**
	 * Default constructor.
	 */
	public Bridge() {
		super("Bridge");
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {		
		// Ground
		SimulationBody ground = new SimulationBody();
		ground.addFixture(Geometry.createRectangle(50.0, 1.0));
	    ground.translate(new Vector2(0.6875, -8.75));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    final double y = 2.0;
	    final int n = 20;
	    final double hn = n / 2.0;
	    final double w = 1.0;
	    final double hw = w / 2.0;
	    final double h = 0.25;
	    
	    SimulationBody prev = ground;
	    for (int i = 0; i < n; i++) {
	    	SimulationBody section = new SimulationBody();
		    {// Fixture1
		      Convex c = Geometry.createRectangle(w, h);
		      BodyFixture bf = new BodyFixture(c);
		      section.addFixture(bf);
		    }
		    section.translate(new Vector2(i - hn, y));
		    section.setMass(MassType.NORMAL);
		    world.addBody(section);
		    
		    // connect the previous body with this body
		    RevoluteJoint<SimulationBody> rj = new RevoluteJoint<SimulationBody>(prev, section, new Vector2(i - hn - hw, y));
		    world.addJoint(rj);
		    
		    prev = section;
	    }
	    
	    // connect the last body with the ground
	    RevoluteJoint<SimulationBody> rj = new RevoluteJoint<SimulationBody>(prev, ground, new Vector2(hn + hw, y));
	    world.addJoint(rj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 32.0;
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Bridge simulation = new Bridge();
		simulation.run();
	}
}
