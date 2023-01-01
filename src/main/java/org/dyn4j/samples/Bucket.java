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

import java.util.Random;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A scene where we fill a "bucket" with shapes.
 * @author William Bittle
 * @since 5.0.0
 * @version 4.1.1
 */
public class Bucket extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -3837218136220591307L;

	/**
	 * Default constructor.
	 */
	public Bucket() {
		super("Bucket");
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
	    // Bottom
		SimulationBody bucketBottom = new SimulationBody();
		bucketBottom.addFixture(Geometry.createRectangle(15.0, 1.0));
	    bucketBottom.setMass(MassType.INFINITE);
	    world.addBody(bucketBottom);

	    // Left-Side
	    SimulationBody bucketLeft = new SimulationBody();
	    bucketLeft.addFixture(Geometry.createRectangle(1.0, 15.0));
	    bucketLeft.translate(new Vector2(-7.5, 7.0));
	    bucketLeft.setMass(MassType.INFINITE);
	    world.addBody(bucketLeft);

	    // Right-Side
	    SimulationBody bucketRight = new SimulationBody();
	    bucketRight.addFixture(Geometry.createRectangle(1.0, 15.0));
	    bucketRight.translate(new Vector2(7.5, 7.0));
	    bucketRight.setMass(MassType.INFINITE);
	    world.addBody(bucketRight);

	    Random r = new Random(23);
	    double xmin = -7.0;
	    double xmax = 7.0;
	    double ymin = 0.5;
	    double ymax = 7.0;
	    double maxSize = 0.6;
	    double minSize = 0.2;
	    
	    for (int i = 0; i < 200; i++) {
	    	double size = r.nextDouble() * maxSize + minSize;
	    	
	    	Convex c = null;
	    	
	    	int type = r.nextInt(2);
	    	switch (type) {
		    	case 0:
		    		c = Geometry.createRectangle(size, size);
		    		break;
		    	case 1:
	    		default:
		    		c = Geometry.createCircle(size * 0.5);
		    		break;
	    	}
	    	
	    	double x = r.nextDouble() * xmax * 2 + xmin;
	    	double y = r.nextDouble() * ymax + ymin;
	    	
		    SimulationBody b = new SimulationBody();
		    b.addFixture(c);
		    b.translate(new Vector2(x, y));
		    b.setMass(MassType.NORMAL);
		    world.addBody(b);
	    }
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
		Bucket simulation = new Bucket();
		simulation.run();
	}
}
