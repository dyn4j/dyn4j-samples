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

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * An example of using a "Concave" body.
 * @author William Bittle
 * @since 4.1.1
 * @version 4.1.1
 */
public class Concave extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 8797361529527319100L;

	/**
	 * Default constructor.
	 */
	public Concave() {
		super("Concave", 64.0);
		
		this.setOffsetY(-200);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeWorld()
	 */
	protected void initializeWorld() {
		// Ground
		SimulationBody body1 = new SimulationBody();
	    {// Fixture1
	      Convex c = Geometry.createRectangle(15.0, 1.0);
	      BodyFixture bf = new BodyFixture(c);
	      body1.addFixture(bf);
	    }
	    body1.setMass(MassType.INFINITE);
	    world.addBody(body1);

	    // Concave
	    SimulationBody body2 = new SimulationBody();
	    {// Fixture1
	      Convex c = Geometry.createRectangle(3.0, 1.0);
	      c.translate(new Vector2(0.0, 0.5));
	      BodyFixture bf = new BodyFixture(c);
	      body2.addFixture(bf);
	    }
	    {// Fixture2
	      Convex c = Geometry.createRectangle(1.0, 1.0);
	      c.translate(new Vector2(-1.0, -0.5));
	      BodyFixture bf = new BodyFixture(c);
	      body2.addFixture(bf);
	    }
	    {// Fixture3
	      Convex c = Geometry.createRectangle(1.0, 1.0);
	      c.translate(new Vector2(1.0, -0.5));
	      BodyFixture bf = new BodyFixture(c);
	      body2.addFixture(bf);
	    }
	    body2.translate(new Vector2(0.0, 4.0));
	    body2.setMass(MassType.NORMAL);
	    world.addBody(body2);

	    // Body3
	    SimulationBody body3 = new SimulationBody();
	    {// Fixture1
	      Convex c = Geometry.createRectangle(0.5, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      body3.addFixture(bf);
	    }
	    body3.translate(new Vector2(0.0, 1.0));
	    body3.setMass(MassType.NORMAL);
	    world.addBody(body3);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Concave simulation = new Concave();
		simulation.run();
	}
}