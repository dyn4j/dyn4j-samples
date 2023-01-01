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

import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

/**
 * A scene that replicates a piston in an ICE.
 * @author William Bittle
 * @since 5.0.0
 * @version 4.1.1
 */
public class Crank extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -602182516495632849L;

	/**
	 * Default constructor.
	 */
	public Crank() {
		super("Crank");
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeWorld()
	 */
	protected void initializeWorld() {
		SimulationBody ground = new SimulationBody();
		ground.addFixture(Geometry.createRectangle(10.0, 0.5));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    // piston+crank
	    
	    SimulationBody crank = new SimulationBody();
	    crank.addFixture(Geometry.createRectangle(0.5, 2.0), 2.0, 0.0, 0.0);
	    crank.translate(new Vector2(0.0, 5.0));
	    crank.setMass(MassType.NORMAL);
	    world.addBody(crank);

	    SimulationBody rod = new SimulationBody();
	    rod.addFixture(Geometry.createRectangle(0.5, 4.0), 2.0, 0.0, 0.0);
	    rod.translate(new Vector2(0.0, 7.5));
	    rod.setMass(MassType.NORMAL);
	    world.addBody(rod);

	    SimulationBody piston = new SimulationBody();
	    piston.addFixture(Geometry.createRectangle(1.5, 1.5), 2.0, 0.0, 0.0);
	    piston.translate(new Vector2(0.0, 9.75));
	    piston.setMass(MassType.FIXED_ANGULAR_VELOCITY);
	    world.addBody(piston);

	    // provides the motion
	    RevoluteJoint<SimulationBody> crankShaftJoint = new RevoluteJoint<SimulationBody>(ground, crank, new Vector2(0.0, 4.25));
	    crankShaftJoint.setMotorEnabled(true);
	    crankShaftJoint.setMotorSpeed(Math.toRadians(180.0));
	    crankShaftJoint.setMaximumMotorTorque(10000.0);
	    world.addJoint(crankShaftJoint);
	    
	    // links the crank to the rod
	    RevoluteJoint<SimulationBody> crankToRodJoint = new RevoluteJoint<SimulationBody>(crank, rod, new Vector2(0.0, 5.75));
	    world.addJoint(crankToRodJoint);
	    
	    // links the rod to the piston
	    RevoluteJoint<SimulationBody> rodToPistonJoint = new RevoluteJoint<SimulationBody>(rod, piston, new Vector2(0.0, 9.25));
	    world.addJoint(rodToPistonJoint);
	    
	    // keeps the piston moving along the y-axis
	    PrismaticJoint<SimulationBody> pistonPathJoint = new PrismaticJoint<SimulationBody>(ground, piston, new Vector2(0.0, 9.75), new Vector2(0.0, 1.0));
	    world.addJoint(pistonPathJoint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.framework.SimulationFrame#initializeCamera(org.dyn4j.samples.framework.Camera)
	 */
	@Override
	protected void initializeCamera(Camera camera) {
		super.initializeCamera(camera);
		camera.scale = 32.0;
		camera.offsetY = -200.0;
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Crank simulation = new Crank();
		simulation.run();
	}
}
