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

import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.decompose.Bayazit;
import org.dyn4j.geometry.decompose.Decomposer;
import org.dyn4j.geometry.decompose.EarClipping;
import org.dyn4j.geometry.decompose.SweepLine;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * An example of using the convex decomposition classes to 
 * build a set of convex objects from a simple polygon.
 * <p>
 * NOTE: Please review the definition of <a href="https://en.wikipedia.org/wiki/Simple_polygon">Simple Polygon</a>.
 * @author William Bittle
 * @since 5.0.0
 * @version 4.1.1
 */
public class Decomposition extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 805187905337457811L;

	/**
	 * Default constructor.
	 */
	public Decomposition() {
		super("Decomposition");
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeWorld()
	 */
	protected void initializeWorld() {

	    // Ground
		SimulationBody ground = new SimulationBody();
		ground.addFixture(Geometry.createRectangle(15.0, 1.0));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    // a (lowercase) "d" shape
	    Vector2[] dVerticies = new Vector2[] {
	    new Vector2(-0.703125, 0.859375), 
		  new Vector2(-0.828125, 0.625), 
		  new Vector2(-0.875, 0.25), 
		  new Vector2(-0.625, 0.0625), 
		  new Vector2(-0.359375, 0.046875),
		  new Vector2(-0.1875, 0.1875),
		  new Vector2(-0.21875, 0.0),
		  new Vector2(0.0, -0.0),
		  new Vector2(-0.015625, 1.625), 
		  new Vector2(-0.171875, 1.640625), 
		  new Vector2(-0.171875, 0.671875),
		  new Vector2(-0.375, 0.84375)
	    };
	    
	    Decomposer bayazit = new Bayazit();
	    List<Convex> parts = bayazit.decompose(dVerticies);
	    SimulationBody d = new SimulationBody();
	    for (Convex c : parts) {
	    	d.addFixture(c);
	    }
	    d.rotate(Math.toRadians(-0.17453292519943292));
	    d.translate(new Vector2(-1.950745294864571, 0.4726801090249715));
	    d.setMass(MassType.NORMAL);
	    world.addBody(d);

	    // a (lowercase) "y" shape
	    Vector2[] yVerticies = new Vector2[] {
	    	new Vector2(0.53125, -1.390625),
	    	new Vector2(1.1171875, -0.546875),
	    	new Vector2(1.703125, 0.296875), 
	    	new Vector2(1.421875, 0.3125), 
	    	new Vector2(1.015625, -0.34375), 
	    	new Vector2(0.703125, 0.28125), 
	    	new Vector2(0.4375, 0.265625), 
	    	new Vector2(0.8125, -0.484375),
	    	new Vector2(0.25, -1.1875)
	    };
	    
	    Decomposer sweep = new SweepLine();
	    parts = sweep.decompose(yVerticies);
	    SimulationBody y = new SimulationBody();
	    for (Convex c : parts) {
	    	y.addFixture(c);
	    }
	    y.getFixture(2).setSensor(true);
	    y.translate(new Vector2(-2.1822787101720906, 1.4407710711648776));
	    y.setMass(MassType.NORMAL);
	    world.addBody(y);

	    // a (lowercase) "n" shape
	    Vector2[] nVerticies = new Vector2[] {
    		new Vector2(-0.03125, 0.984375),
    		new Vector2(-0.015625, -0.015625), 
    		new Vector2(0.140625, -0.015625), 
	    	new Vector2(0.15625, 0.375),
	    	new Vector2(0.34375, 0.578125),
	    	new Vector2(0.5, 0.71875),
	    	new Vector2(0.6875, 0.671875),
	    	new Vector2(0.765625, 0.5625),
	    	new Vector2(0.734375, -0.0), 
	    	new Vector2(1.015625, -0.015625), 
	    	new Vector2(1.046875, 0.640625),
	    	new Vector2(0.953125, 0.84375), 
	    	new Vector2(0.78125, 0.96875),
	    	new Vector2(0.5, 0.96875), 
	    	new Vector2(0.296875, 0.84375),
	    	new Vector2(0.1875, 0.734375),
	    	new Vector2(0.1875, 0.984375),
	    };
	    
	    Decomposer ear = new EarClipping();
	    parts = ear.decompose(nVerticies);
	    SimulationBody n = new SimulationBody();
	    for (Convex c : parts) {
	    	n.addFixture(c);
	    }
	    n.translate(new Vector2(-0.5112465256848169, 0.6500452079566003));
	    n.setMass(MassType.NORMAL);
	    world.addBody(n);

	    // a "4" shape
	    Vector2[] fourVerticies = new Vector2[] {
	    	new Vector2(0.8125, 0.8125), 
    		new Vector2(0.828125, 0.046875), 
    		new Vector2(1.125, 0.046875), 
    		new Vector2(1.125, 1.828125), 
    		new Vector2(0.859375, 1.828125),
    		new Vector2(-0.21875, 0.8125)
	    };
	    
	    parts = bayazit.decompose(fourVerticies);
	    SimulationBody four = new SimulationBody();
	    for (Convex c : parts) {
	    	four.addFixture(c);
	    }
	    four.translate(new Vector2(0.26865946239058014, 0.9649433050584864));
	    four.setMass(MassType.NORMAL);
	    world.addBody(four);

	    // a (lowercase) "j" shape
	    Vector2[] jVerticies = new Vector2[] {
    		new Vector2(0.90625, 0.203125), 
    		new Vector2(0.96875, 0.34375), 
    		new Vector2(1.0, 1.21875), 
    		new Vector2(0.78125, 1.265625), 
    		new Vector2(0.734375, 0.34375),
    		new Vector2(0.5625, 0.296875),
    		new Vector2(0.34375, 0.296875),
    		new Vector2(0.234375, 0.4375),
    		new Vector2(0.234375, 0.65625), 
  		  	new Vector2(0.046875, 0.65625), 
  		  	new Vector2(0.046875, 0.5),
  		  new Vector2(0.078125, 0.28125), 
		  new Vector2(0.203125, 0.109375),
		  new Vector2(0.46875, 0.046875), 
		  new Vector2(0.734375, 0.109375),
		  
	    };
	    
	    parts = bayazit.decompose(jVerticies);
	    SimulationBody j = new SimulationBody();
	    for (Convex c : parts) {
	    	j.addFixture(c);
	    }
	    j.translate(new Vector2(0.8752422480620154, 0.4841370269037847));
	    j.setMass(MassType.NORMAL);
	    world.addBody(j);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 64.0;
		camera.offsetY = -200.0;
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Decomposition simulation = new Decomposition();
		simulation.run();
	}
}
