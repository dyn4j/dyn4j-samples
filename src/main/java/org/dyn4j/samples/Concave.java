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

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * An example of using a "Concave" body.
 * @author William Bittle
 * @since 5.0.0
 * @version 4.1.1
 */
public class Concave extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 8797361529527319100L;

	/**
	 * Default constructor.
	 */
	public Concave() {
		super("Concave");
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

	    // Concave
	    SimulationBody table = new SimulationBody();
	    {
	      Convex c = Geometry.createRectangle(3.0, 1.0);
	      c.translate(new Vector2(0.0, 0.5));
	      table.addFixture(c);
	    }
	    {
	      Convex c = Geometry.createRectangle(1.0, 1.0);
	      c.translate(new Vector2(-1.0, -0.5));
	      table.addFixture(c);
	    }
	    {
	      Convex c = Geometry.createRectangle(1.0, 1.0);
	      c.translate(new Vector2(1.0, -0.5));
	      table.addFixture(c);
	    }
	    table.translate(new Vector2(0.0, 4.0));
	    table.setMass(MassType.NORMAL);
	    world.addBody(table);

	    // Body3
	    SimulationBody box = new SimulationBody();
	    box.addFixture(Geometry.createSquare(0.5));
	    box.translate(new Vector2(0.0, 1.0));
	    box.setMass(MassType.NORMAL);
	    world.addBody(box);
	}
	
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
		Concave simulation = new Concave();
		simulation.run();
	}
}
